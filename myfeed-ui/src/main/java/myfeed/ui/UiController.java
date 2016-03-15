package myfeed.ui;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;
import myfeed.core.AsyncRest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import rx.Observable;
import rx.Single;

import javax.servlet.http.HttpSession;

/**
 * @author Spencer Gibb
 */
@RestController
public class UiController {
	@Autowired
	private AsyncRest rest;

	@Autowired
	private FeedClient feedClient;

    @Autowired
    private FeatureService featureService;

    @RequestMapping("/ui/features")
    public Map<String, String> features() {
        return Collections.singletonMap("featureAaaFlag", featureService.getFeatureAaaFlag());
    }

	@RequestMapping("/ui/feed/{username}")
	public Single<Feed> feed(@PathVariable("username") String username, HttpSession session) {
		Single<Feed> feed = getUser("http://myfeed-user/@{username}", username)
				.map(HttpEntity::getBody)
				.flatMap(user -> {
					// given the user, go get each following user and return the list of usernames
					Observable<User> u = Observable.just(user);
					Observable<List<FeedItem>> feedItems = feedClient.feedItems(user.getUsername());
					Observable<List<String>> following = Observable.from(user.getFollowing())
							.flatMap(userid -> getUser("http://myfeed-user/users/{userid}", userid)
													.map(HttpEntity::getBody)
													.map(User::getUsername)
							).toList();
					return Observable.zip(u, following, feedItems, Observable.just(session.getId()), Feed::new);
				}).toSingle();

		return feed;
	}

	@FeignClient("myfeed-feed")
	public interface FeedClient {
		@RequestMapping(value = "/list/{username}", method = RequestMethod.GET)
		Observable<List<FeedItem>> feedItems(@PathVariable("username") String username);
	}

	private Observable<ResponseEntity<User>> getUser(String url, String username) {
		return Observable.from(rest.getForEntity(url, User.class, username));
	}

	@Data
	private static class Feed {
		private final User profile;
		private final List<String> following;
		private final List<FeedItem> feed;
		private final String sessionId;
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

}
