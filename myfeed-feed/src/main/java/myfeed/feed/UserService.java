package myfeed.feed;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import myfeed.TraversonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Spencer Gibb
 */
@Service
public class UserService {
	private static final ParameterizedTypeReference<PagedResources<Resource<User>>> TYPE_REFERENCE = new ParameterizedTypeReference<PagedResources<Resource<User>>>() {};


	@Autowired
	private RestTemplate rest;

	@Autowired
	private TraversonFactory factory;

	@SuppressWarnings("unchecked")
	@HystrixCommand(fallbackMethod = "defaultId")
	public String findId(String username) {
		ResponseEntity<Map> user = rest.getForEntity("http://myfeed-user/@{username}", Map.class, username);
		if (user.getStatusCode().equals(HttpStatus.OK)) {
			return (String) user.getBody().get("id");
		}
		return null;
	}

	public String defaultId(String username) {
		return "";
	}

	@HystrixCommand(fallbackMethod = "defaultUsers")
	public List<Resource<User>> getUsers() {
		PagedResources<Resource<User>> users = factory.create("myfeed-user").follow("users").toObject(TYPE_REFERENCE);
		return new ArrayList<>(users.getContent());
	}

	@SuppressWarnings("unchecked")
	public List<Resource<User>> defaultUsers() {
		return Collections.EMPTY_LIST;
	}

}
