package myfeed.ui;

import static rx.Observable.*;

import java.security.Principal;
import java.util.List;

import lombok.Data;
import myfeed.core.AsyncRest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.security.oauth2.resource.EnableOAuth2Resource;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rx.Observable;

/**
 * @author Spencer Gibb
 */
@SpringCloudApplication
@EnableOAuth2Resource
@RestController
@RequestMapping("/profile")
public class UiApp {

	@Autowired
	private AsyncRest rest;

	@RequestMapping("/view")
	public Observable<Profile> profile(Principal principal) {
		return from(rest.getForEntity("http://myfeed-user/@{username}", User.class, principal.getName()))
				.map(HttpEntity::getBody)
				.flatMap(user -> {
					Observable<User> u = just(user);
					Observable<List<String>> following = from(user.getFollowing())
							.flatMap(userid ->
											from(rest.getForEntity("http://myfeed-user/users/{userid}", User.class, userid))
													.map(HttpEntity::getBody)
													.map(User::getUsername)
							).toList();
					return zip(u, following, Profile::new);
				});
	}

	@Data
	private static class Profile {
		private final User profile;
		private final List<String> following;
	}

	@RequestMapping("/user")
	public Principal user(Principal user) {
		return user;
	}

	@Controller
	public static class ExternalLinksController {
		@RequestMapping("/@{username}")
		public String feed(@PathVariable("username") String username) {
			return "forward:index.html";
		}

		/*@RequestMapping("/profile")
		public String profile() {
			return "forward:index.html";
		}*/
	}

	public static void main(String[] args) {
		SpringApplication.run(UiApp.class, args);
	}
}
