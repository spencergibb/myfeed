package myfeed.feed;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import myfeed.core.NotFoundException;
import rx.Observable;
import rx.Single;

/**
 * @author Spencer Gibb
 */
@Service
@Slf4j
@Value
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FeedService {
	private final UserService user;
	private final FeedItemRepository repo;
	private final FeedItemSubmitter feedItemSubmitter;

    //@HystrixCommand(fallbackMethod = "defaultFeed")
	public Observable<List<FeedItem>> feed(String username) {
		return user.findId(username).toObservable()
				.flatMap(userid -> {
					if (StringUtils.hasText(userid)) {
						return Observable.from(repo.findByUserid(userid));
					} else {
						return Observable.just(singletonFeed("Unknown user: " + username));
					}
				})
				// sort by created desc since redis repo doesn't support order
				.flatMapIterable(feedItems -> feedItems)
				.toSortedList((feedItem1, feedItem2) -> feedItem2.getCreated().compareTo(feedItem1.getCreated()));
	}

	@SuppressWarnings("unused") //see feed()
	protected List<FeedItem> defaultFeed(String username) {
		return singletonFeed("Something's not right.  Check back in a moment");
	}

	private List<FeedItem> singletonFeed(String text) {
		return Arrays.asList(singleFeedItem(text));
	}

	private FeedItem singleFeedItem(String text) {
		return new FeedItem("1", "myfeed", text);
	}

	public Single<FeedItem> addFeedItem(String username, String text) {
		return user.findId(username).toObservable()
				.map(userid -> {
					if (StringUtils.hasText(userid)) {
						return new FeedItem(userid, username, text);
					} else {
						throw new NotFoundException("username: "+username);
					}
				})
				.map(repo::save).toSingle();
	}

	@ServiceActivator(inputChannel = Sink.INPUT)
	@SuppressWarnings("unused")
	public void propagate(FeedItem feedItem) {
		/*String username = feedItem.getUsername();
		String userid = user.findId(username).toBlocking().value();
		List<Resource<User>> following = user.getFollowing(userid);
		List<FeedItem> toSave = new ArrayList<>();
		for (Resource<User> followed : following) {
			User user = followed.getContent();
			String followingUserid = user.getUserId(); //getId(user);
			String followingUsername = user.getUsername();
			log.info("Saving feed item to {}:{}", followingUsername, followingUserid);
			toSave.add(new FeedItem(followingUserid, feedItem.getUsername(),
					feedItem.getText(), feedItem.getCreated()));
		}
		Iterable<FeedItem> saved = repo.save(toSave);
		log.info("Saved: "+saved);*/
	}
}
