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
		User[] users = new User[] {
				new User("spencergibb", "Spencer Gibb"),
				new User("joshlong", "Josh Long")
		};

		for (User toCreate : users) {
			User user = repo.findByUsername(toCreate.getUsername());
			if (user == null) {
				user = repo.save(toCreate);
				log.info("Created: " + user);
			}
		}
	}
}
