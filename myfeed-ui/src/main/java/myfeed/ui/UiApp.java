package myfeed.ui;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.security.oauth2.resource.EnableOAuth2Resource;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;

import static org.springframework.web.util.UriComponentsBuilder.*;

/**
 * @author Spencer Gibb
 */
@SpringBootApplication
@EnableDiscoveryClient
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
		@Value("${myfeed.ui.login-redirect:''}")
		private String loginRedirect;

		@RequestMapping("/profile/login")
		public String profile(HttpServletRequest req) {
			/*String url;
			if (StringUtils.hasText(loginRedirect)) {
				url = loginRedirect;
			} else {
				UriComponents uri = fromUriString(req.getRequestURI()).build();
				url = uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort();
			}
			return "redirect:" + url;*/
			return "login please";
		}

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
