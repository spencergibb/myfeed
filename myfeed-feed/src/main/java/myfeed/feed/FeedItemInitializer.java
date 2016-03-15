package myfeed.feed;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;
import rx.Single;

/**
 * @author Spencer Gibb
 */
@RestController
@Slf4j
public class FeedItemInitializer {

	private final FeedItemRepository repo;
	private final UserService userService;
	private final RandomText randomText;
	private final Random random = new Random();

	@Value("${myfeed.feed.initializer.maxItems:0}")
	int minItems = 0;

	@Value("${myfeed.feed.initializer.maxItems:10}")
	int maxItems = 10;

	@Value("${myfeed.feed.initializer.minWords:5}")
	int minWords = 5;

	@Value("${myfeed.feed.initializer.maxWords:20}")
	int maxWords = 20;

	@Autowired
	public FeedItemInitializer(FeedItemRepository repo, UserService userService, RandomText randomText) {
		this.repo = repo;
		this.userService = userService;
		this.randomText = randomText;
	}

	@RequestMapping("/init/all")
	public Single<Map<String, Collection<FeedItem>>> initAll() {
		return userService.getUsers()
				.map(Resource::getContent)
				.map(User::getUsername)
				.flatMap(this::initUser)
				.toMultimap(ufi -> ufi.username, ufi -> ufi.feedItem).toSingle();
	}

	@RequestMapping("/init")
	public Single<List<FeedItem>> init(@RequestParam(value = "user") String username) {
		return initUser(username)
				.map(UserFeedItem::getFeedItem)
				.toList().toSingle();
	}

	Observable<UserFeedItem> initUser(String username) {
		int numItems = getNumItems(minItems, maxItems);
		Single<String> userid = userService.findId(username);

		return Observable.range(0, numItems)
				.map(i -> numItems - i)
				.flatMap(i -> {
					Single<String> text = Single.just(randomText.getText(getNumItems(minWords, maxWords)));
					LocalDateTime dateTime = LocalDateTime.now().minusDays(i);
					Single<Date> created = Single.just(Date.from(dateTime.toInstant(ZoneOffset.UTC)));
					Single<String> feedUsername = getRandomUsername(username);
					Single<FeedItem> feedItem = Single.zip(userid, feedUsername, text, created, FeedItem::new);
					return Single.zip(Single.just(username), feedItem, UserFeedItem::new).toObservable();
				})
				.map(userFeedItem -> {
					FeedItem saved = repo.save(userFeedItem.feedItem);
					userFeedItem.feedItem = saved;
					return userFeedItem;
				});
	}

	@Data
	@AllArgsConstructor
	class UserFeedItem {
		String username;
		FeedItem feedItem;
	}

	private int getNumItems(int min, int bound) {
		int numItems = 0;
		while (numItems <= min) {
			numItems = random.nextInt(bound);
		}
		return numItems;
	}


	Single<String> getRandomUsername(String defaultUsername) {
		List<String> usernames = userService.getUsers()
				.map(Resource::getContent)
				.map(User::getUsername)
				.toList().toBlocking().first();

		if (usernames.isEmpty()) {
			return Single.just(defaultUsername);
		}
		return Single.just(usernames.get(random.nextInt(usernames.size())));
	}
}
