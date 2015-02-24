package myfeed.admin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;
import myfeed.Rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Spencer Gibb
 */
@SpringCloudApplication
@EnableHystrixDashboard
@RestController
@EnableRedisHttpSession
public class AdminApp {
	public static final ParameterizedTypeReference<Resources<Resource<User>>> USERS_TYPE = new ParameterizedTypeReference<Resources<Resource<User>>>() {};

	@Autowired
	Rest rest;

	@Autowired
	LoadBalancerClient loadBalancerClient;

	@RequestMapping("/")
	public ModelAndView home() {
		HashMap<String, String> map = new HashMap<>();
		map.putAll(getUrl("myfeed-turbine", "turbineUrl"));
		map.putAll(getUrl("myfeed-router", "routerUrl"));
		return new ModelAndView("admin", map);
	}

	private Map<String, String> getUrl(String serviceId, String key) {
		String url = null;
		try {
			ServiceInstance instance = loadBalancerClient.choose(serviceId);
			if (instance != null) {
				url = instance.getUri().toString();
			}
		} catch (IllegalStateException e) {
			// no instances of serviceId
		}
		return Collections.singletonMap(key, url);
	}

	@RequestMapping("/users")
	@SuppressWarnings("unchecked")
	public ModelAndView users() {
		//TODO: why doesn't ID show up
		ResponseEntity<Resources<Resource<User>>> response = rest.get("http://myfeed-user/users", USERS_TYPE);
		return new ModelAndView("users", Collections.singletonMap("users", response.getBody().getContent()));
	}

	@Data
	@NoArgsConstructor
	public static class User {
		private String id;
		private String username;
		private String name;
	}

	public static void main(String[] args) {
		SpringApplication.run(AdminApp.class, args);
	}
}
