package myfeed.ui;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.security.oauth2.resource.EnableOAuth2Resource;
import org.springframework.cloud.security.oauth2.sso.OAuth2SsoConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Spencer Gibb
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableOAuth2Resource
@RestController
@RequestMapping("/dashboard")
public class UiApp {

	@RequestMapping("/message")
	public Map<String, Object> dashboard() {
		return Collections.singletonMap("message", "Yay!");
	}

	@RequestMapping("/user")
	public Principal user(Principal user) {
		return user;
	}

	@RequestMapping("/")
	public String home() {
		return "Hello myfeed";
	}

	@Controller
	public static class LoginErrors {
		@RequestMapping("/dashboard/login")
		public String dashboard(HttpServletRequest req) {
			UriComponents uri = UriComponentsBuilder.fromUriString(req.getRequestURI()).build();
			String url = uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort();
			return "redirect:"+url;
		}
	}

	@Component
	public static class LoginConfigurer extends OAuth2SsoConfigurerAdapter {

		@Override
		public void match(RequestMatchers matchers) {
			matchers.antMatchers("/dashboard/**");
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			http.antMatcher("/dashboard/**").authorizeRequests().anyRequest()
					.authenticated();
		}

		/*private Filter csrfHeaderFilter() {
			return new OncePerRequestFilter() {
				@Override
				protected void doFilterInternal(HttpServletRequest request,
												HttpServletResponse response, FilterChain filterChain)
						throws ServletException, IOException {
					CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
					if (csrf != null) {
						Cookie cookie = new Cookie("XSRF-TOKEN", csrf.getToken());
						cookie.setPath("/");
						response.addCookie(cookie);
					}
					filterChain.doFilter(request, response);
				}
			};
		}

		private CsrfTokenRepository csrfTokenRepository() {
			HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
			repository.setHeaderName("X-XSRF-TOKEN");
			return repository;
		}*/
	}

	public static void main(String[] args) {
		SpringApplication.run(UiApp.class, args);
	}
}
