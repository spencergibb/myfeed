package myfeed.ui;

import static rx.Observable.*;

import java.security.Principal;
import java.util.List;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import myfeed.core.AsyncRest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.security.oauth2.resource.EnableOAuth2Resource;
import org.springframework.context.annotation.Bean;
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
@EnableFeignClients
@EnableConfigurationProperties
@RestController
@RequestMapping("/profile")
@Slf4j
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

    @Bean UiProps uiProps() {
        return new UiProps();
    }

    @Bean
    @RefreshScope
    public FeatureService myService(UiProps props) {
        log.info("\n\n\n\n\n\n\n\n\n*****************\nUpdating FeatureService with featureAaaFlag: "+props.getFeatureAaaFlag());
        return new FeatureService(props.getFeatureAaaFlag());
    }

	public static void main(String[] args) {
		SpringApplication.run(UiApp.class, args);
	}
}
