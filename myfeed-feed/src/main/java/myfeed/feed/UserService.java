package myfeed.feed;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author Spencer Gibb
 */
@Service
public class UserService {
	@Autowired
	private RestTemplate rest;

	@SuppressWarnings("unchecked")
	@HystrixCommand(fallbackMethod = "defaultId")
	public String findId(String username) {
		ResponseEntity<Map> user = rest.getForEntity("http://myfeed-user/@spencergibb", Map.class);
		if (user.getStatusCode().equals(HttpStatus.OK)) {
			return (String) user.getBody().get("id");
		}
		return null;
	}

	public String defaultId(String username) {
		return "";
	}
}
