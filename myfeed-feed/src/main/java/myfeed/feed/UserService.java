package myfeed.feed;

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
	public String findId(String username) {
		ResponseEntity<Map> user = rest.getForEntity("http://myfeed-user/@spencergibb", Map.class);
		if (user.getStatusCode().equals(HttpStatus.OK)) {
			return (String) user.getBody().get("id");
		}
		return null;
	}
}
