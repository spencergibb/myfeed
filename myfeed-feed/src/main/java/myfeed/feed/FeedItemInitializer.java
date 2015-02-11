package myfeed.feed;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

	@Value("${myfeed.feed.initializer.maxItems:10}")
	private int maxItems;

	@Autowired
	public FeedItemInitializer(FeedItemRepository repo, UserService userService, RandomText randomText) {
		this.repo = repo;
		this.userService = userService;
		this.randomText = randomText;
	}

	@RequestMapping("/init")
	public Iterable<FeedItem> init(@RequestParam(value = "user") String username) {
		String userid = userService.findId(username);
		int numItems = 0;

		while (numItems == 0) {
			numItems = random.nextInt(maxItems);
		}

		List<Resource<User>> users = userService.getUsers();

		List<FeedItem> items = new ArrayList<>(numItems);
		for (int i = numItems; i > 0; i--) {
			int numWords = random.nextInt(20);
			String text = randomText.getText(numWords);
			LocalDateTime dateTime = LocalDateTime.now().minusDays(i);
			Instant instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();
			Date created = Date.from(instant);

			String feedUsername = username;

			if (!users.isEmpty()) {
				User user = users.get(random.nextInt(users.size())).getContent();
				feedUsername = user.getUsername();
			}

			//TODO: figure out feed owner userid vs userid of user that posted the item
			FeedItem item = new FeedItem(userid, feedUsername, text, created);
			items.add(item);

		}
		Iterable<FeedItem> saved = repo.save(items);
		return saved;
	}
}
