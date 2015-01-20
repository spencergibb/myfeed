package myfeed.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Spencer Gibb
 */
@Component
public class UserInitializer {

	@Autowired
	public UserInitializer(UserRepository repo) {
		User user = repo.findOne("spencergibb");
		if (user == null) {
			user = repo.save(new User("spencergibb"));
		}
	}
}
