package myfeed.ui;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.security.oauth2.resource.EnableOAuth2Resource;
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

	@RequestMapping("/message")
	public Map<String, Object> profile() {
		return Collections.singletonMap("message", "Yay!");
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
