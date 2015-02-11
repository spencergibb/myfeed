package myfeed.user;

import myfeed.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * @author Spencer Gibb
 */
@SpringBootApplication
@EnableDiscoveryClient
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
