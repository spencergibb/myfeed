package myfeed.admin;

import java.util.Collections;

import lombok.Data;

import myfeed.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Spencer Gibb
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableHystrixDashboard
@RestController
public class AdminApp {
	public static final ParameterizedTypeReference<Resources<Resource<User>>> USERS_TYPE = new ParameterizedTypeReference<Resources<Resource<User>>>() {
	};

	@Autowired
	Rest rest;

	@RequestMapping("/")
	public ModelAndView home() {
		return new ModelAndView("admin");
	}

	@RequestMapping("/users")
	@SuppressWarnings("unchecked")
	public ModelAndView users() {
		//TODO: why doesn't ID show up
		ResponseEntity<Resources<Resource<User>>> response = rest.get("http://myfeed-user/users", USERS_TYPE);
		return new ModelAndView("users", Collections.singletonMap("users", response.getBody().getContent()));
	}

	@Data
	public static class User {
		private String id;
		private String username;
		private String name;

		private User() {
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(AdminApp.class, args);
	}
}
