package myfeed.user;

import java.util.ArrayList;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Spencer Gibb
 */
@Component
@Slf4j
public class UserInitializer implements ApplicationRunner {

	private final Random random = new Random();
	private final UserRepository repo;
	private final UserIntializedSender sender;

	@Autowired
	public UserInitializer(UserRepository repo, UserIntializedSender sender) {
		this.repo = repo;
		this.sender = sender;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		User[] users = new User[] {
				new User("spencergibb", "Spencer Gibb"),
				new User("joshlong", "Josh Long"),
				new User("dsyer", "Dave Syer"),
				new User("philwebb", "Phil Webb"),
				new User("dussab", "Brian Dussault"),
				new User("rwinch", "Rob Winch"),
				new User("pperalta", "Patrick Peralta")
		};

		for (User toCreate : users) {
			User user = repo.findByUsername(toCreate.getUsername());
			if (user == null) {
				user = repo.save(toCreate);
				log.info("Created: " + user);
			}
		}
		ArrayList<User> existingUsers = Lists.newArrayList(repo.findAll());
		for (User toUpdate : existingUsers) {
			if (toUpdate.getFollowing().isEmpty()) {
				int numFollowers = 0;
				while (numFollowers == 0) {
					numFollowers = random.nextInt(existingUsers.size());
				}

				for (int i = 0; i < numFollowers; i++) {
					String toFollow = existingUsers.get(i).getId();
					if (!toFollow.equals(toUpdate.getId())) {
						toUpdate.getFollowing().add(toFollow);
					}
				}

				if (toUpdate.getFollowing().contains(toUpdate.getId())) {
					toUpdate.getFollowing().remove(toUpdate.getId());
					log.info("Updated, removed self from following: " + toUpdate);
				}

				User saved = repo.save(toUpdate);
				sender.send(saved);
				log.info("Updated: " + saved);
			}
		}
	}
}
