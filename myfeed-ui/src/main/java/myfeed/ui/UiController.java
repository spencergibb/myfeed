package myfeed.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import myfeed.AsyncRest;
import myfeed.ObservableAdapter;
import myfeed.Rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;

import static rx.Observable.*;

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
	public DeferredResult<Feed> feed(@PathVariable("username") String username) {
		Observable<List<FeedItem>> feedItems = from(rest.get("http://myfeed-feed/{username}", FEED_ITEM_TYPE, username))
				.map(HttpEntity::getBody);

		Observable<Feed> feed = getUser("http://myfeed-user/@{username}", username)
				.map(HttpEntity::getBody)
				.flatMap(user -> {
					// given the user, go get each following user and return the list of usernames
					Observable<User> u = just(user);
					Observable<List<String>> following = from(user.getFollowing())
							.flatMap(userid ->
											getUser("http://myfeed-user/users/{userid}", userid)
													.map(HttpEntity::getBody)
													.map(User::getUsername)
							).toList();
					return zip(u, following, feedItems, Feed::new);
				});

		return new ObservableAdapter<>(feed);
	}

	private Observable<ResponseEntity<User>> getUser(String url, String username) {
		return from(rest.getForEntity(url, User.class, username));
	}

	@Data
	private static class Feed {
		private final User profile;
		private final List<String> following;
		private final List<FeedItem> feed;
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
