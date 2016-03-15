package myfeed.feed;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import myfeed.core.TraversonFactory;
import rx.Observable;
import rx.Single;

/**
 * @author Spencer Gibb
 */
@Service
public class UserService {
	private static final ParameterizedTypeReference<PagedResources<Resource<User>>> TYPE_PAGED_USERS = new ParameterizedTypeReference<PagedResources<Resource<User>>>() {};

	private static final ParameterizedTypeReference<Resources<Resource<User>>> TYPE_USERS = new ParameterizedTypeReference<Resources<Resource<User>>>() {};

	@Autowired
	private RestTemplate rest;

	@Autowired
	private TraversonFactory traverson;

	@HystrixCommand(fallbackMethod = "defaultId")
	public Single<String> findId(String username) {
		ResponseEntity<User> user = rest.getForEntity("http://myfeed-user/@{username}", User.class, username);
		if (user.getStatusCode().equals(HttpStatus.OK)) {
			return Single.just(user.getBody().getUserId());
		}
		return Single.just(null);
	}

	public Single<String> defaultId(String username) {
		return Single.just(null);
	}

	@HystrixCommand(fallbackMethod = "defaultUsers")
	Observable<Resource<User>> getUsers() {
		PagedResources<Resource<User>> users = traverson.create("myfeed-user").follow("users").toObject(TYPE_PAGED_USERS);
		return Observable.from(users.getContent());
	}

	@SuppressWarnings("unsued") // see getUsers()
	Observable<Resource<User>> defaultUsers() {
		return Observable.empty();
	}

	public Observable<Resource<User>> getFollowing(String userid) {
		Resources<Resource<User>> users = traverson.create("myfeed-user")
				.follow("users", "search", "findByFollowing")
				.withTemplateParameters(Collections.singletonMap("userId", userid))
				.toObject(TYPE_USERS);

		return Observable.from(users.getContent());
	}
}
