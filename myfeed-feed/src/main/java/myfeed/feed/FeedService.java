package myfeed.feed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import myfeed.core.NotFoundException;
import rx.Observable;

/**
 * @author Spencer Gibb
 */
@Service
@Slf4j
public class FeedService {
	@Autowired
	FeedItemRepository repo;

	@Autowired
	ApplicationEventPublisher publisher;

	@Autowired
	UserService user;

    //@HystrixCommand
	public Observable<List<FeedItem>> feed(String username) {
		return user.findId(username)
				.filter(StringUtils::hasText)
				.map(userid -> repo.findByUserid(userid))//, new PageRequest(0, 20)))
				// sort by created desc since redis repo doesn't support order
				.flatMapIterable(feedItem -> feedItem)
				.toSortedList((feedItem1, feedItem2) -> feedItem2.getCreated().compareTo(feedItem1.getCreated()))
				.defaultIfEmpty(singletonFeed("Unknown user: " + username));
	}

	//protected Page<FeedItem> defaultFeed(String username) {
	protected List<FeedItem> defaultFeed(String username) {
		return singletonFeed("Something's not right.  Check back in a moment");
	}

	private List<FeedItem> singletonFeed(String text) {
		//return new PageImpl<>(Arrays.asList(new FeedItem("1", "myfeed", text)));
		return Arrays.asList(new FeedItem("1", "myfeed", text));
	}

	public FeedItem addFeedItem(String username, String text) {
		String userid = user.findId(username).toBlocking().first();
		if (userid == null) {
			throw new NotFoundException("username: "+username);
		}
		FeedItem feedItem = repo.save(new FeedItem(userid, username, text));
		publisher.publishEvent(new FeedItemEvent(feedItem));
		return feedItem;
	}

	//TODO: Observable
	public List<FeedItem> getUserResource(String username) {
		List<FeedItem> items = repo.findByUserid(user.findId(username).toBlocking().first()); //, new PageRequest(0, 20));
		//return new PagedResources<>(items.getContent(), getMetadata(items));
		return items;
	}

	//TODO: send to queue and process queue
	@Async
	public void propagate(FeedItem feedItem) {
		String username = feedItem.getUsername();
		String userid = user.findId(username).toBlocking().first();
		List<Resource<User>> following = user.getFollowing(userid);
		List<FeedItem> toSave = new ArrayList<>();
		for (Resource<User> user : following) {
			String followingUserid = user.getContent().getUserId(); //getId(user);
			String followingUsername = user.getContent().getUsername();
			log.info("Saving feed item to {}:{}", followingUsername, followingUserid);
			toSave.add(new FeedItem(followingUserid, feedItem.getUsername(),
					feedItem.getText(), feedItem.getCreated()));
		}
		Iterable<FeedItem> saved = repo.save(toSave);
		log.info("Saved: "+saved);
	}

	public static PagedResources.PageMetadata getMetadata(Page<?> page) {
		return new PagedResources.PageMetadata(page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages());
	}
}
