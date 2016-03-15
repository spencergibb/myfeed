package myfeed.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import myfeed.core.NotFoundException;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Spencer Gibb
 */
@SpringCloudApplication
@Import(RepositoryRestMvcConfiguration.class)
@EnableRedisRepositories
@EnableBinding(UserBinder.class)
@IntegrationComponentScan
@RestController
public class UserApp extends RepositoryRestConfigurerAdapter {

	@Autowired
	private UserRepository users;

	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
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
