package myfeed.user;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import myfeed.core.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Spencer Gibb
 */
@SpringCloudApplication
@Import(RepositoryRestMvcConfiguration.class)
@RestController
public class UserApp extends RepositoryRestMvcConfiguration {

	@Autowired
	private UserRepository users;

	@Override
	protected void configureRepositoryRestConfiguration( RepositoryRestConfiguration config) {
		config.exposeIdsFor(User.class);
	}

	@RequestMapping(value = "/@{username}", method = GET)
	public User getUser(@PathVariable("username") String username) {
		User user = users.findByUsername(username);
		if (user == null) {
			throw new NotFoundException("Not found: "+username);
		}
		return user;
	}

	@RequestMapping(value = "/@{username}", method = DELETE)
	public String deleteUser(@PathVariable("username") String username) {
		return users.deleteByUsername(username);
	}

	public static void main(String[] args) {
		SpringApplication.run(UserApp.class, args);
	}
}
