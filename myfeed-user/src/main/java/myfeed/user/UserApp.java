package myfeed.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

/**
 * @author Spencer Gibb
 */
@SpringBootApplication
@EnableDiscoveryClient
@Import(RepositoryRestMvcConfiguration.class)
public class UserApp extends RepositoryRestMvcConfiguration {

	@Override
	protected void configureRepositoryRestConfiguration( RepositoryRestConfiguration config) {
		config.exposeIdsFor(User.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(UserApp.class, args);
	}
}
