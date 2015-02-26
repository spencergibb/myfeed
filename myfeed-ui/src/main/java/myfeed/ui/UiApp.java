package myfeed.ui;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

import myfeed.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.security.oauth2.resource.EnableOAuth2Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Spencer Gibb
 */
@SpringCloudApplication
@EnableOAuth2Resource
@RestController
@RequestMapping("/profile")
public class UiApp {

	@Autowired
	private Rest rest;

	@RequestMapping("/view")
	public Map<String, User> profile(Principal principal) {
		ResponseEntity<User> user = rest.getForEntity("http://myfeed-user/@{username}", User.class, principal.getName());
		return Collections.singletonMap("profile", user.getBody());
	}

	@RequestMapping("/user")
	public Principal user(Principal user) {
		return user;
	}

	@Controller
	public static class LoginErrors {
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
