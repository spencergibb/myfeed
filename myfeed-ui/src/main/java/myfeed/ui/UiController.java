package myfeed.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import myfeed.AsyncRest;
import myfeed.Rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rx.Observable;

/**
 * @author Spencer Gibb
 */
@RestController
public class UiController {
	//public static final ParameterizedTypeReference<PagedResources<FeedItem>> FEED_ITEM_TYPE = new ParameterizedTypeReference<PagedResources<FeedItem>>() {};
	public static final ParameterizedTypeReference<List<FeedItem>> FEED_ITEM_TYPE = new ParameterizedTypeReference<List<FeedItem>>() {};

	@Autowired
	private AsyncRest rest;

	@Autowired
	private Rest syncRest;

	@RequestMapping("/ui/feed/{username}")
	public Feed feed(@PathVariable("username") String username) {
		HashMap<String, Object> map = new HashMap<>();

		return Observable.from(rest.get("http://myfeed-feed/{username}", FEED_ITEM_TYPE, username))
				.map(HttpEntity::getBody)
				.zipWith(Observable.from(rest.getForEntity("http://myfeed-user/@{username}", User.class, username))
						.map(HttpEntity::getBody)
						, (feedItems, user) -> {
					Feed feed = new Feed(feedItems, user);
					return feed;
				})
				.toBlocking()
				.first();
	}

	@Data
	private static class Feed {
		private final List<FeedItem> feed;
		private final User profile;
	}

	@Data
	@NoArgsConstructor
	private static class FeedItem {
		private String id;
		private String userid;
		private String username;
		private String text;
		private Date created;
	}

	@Data
	@NoArgsConstructor
	private static class User {
		private String id;
		private String username;
		private String name;
		private List<String> following = new ArrayList<>();
	}
}
