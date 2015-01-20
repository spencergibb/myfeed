package myfeed.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Spencer Gibb
 */
@Component
@Slf4j
public class UserInitializer {

	@Autowired
	public UserInitializer(UserRepository repo) {
		User user = repo.findByUsername("spencergibb");
		if (user == null) {
			user = repo.save(new User("spencergibb", "Spencer Gibb"));
			log.info("Created spencergibb: "+user);
		}
	}
}
