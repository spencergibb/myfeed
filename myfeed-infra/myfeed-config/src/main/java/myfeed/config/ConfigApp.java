package myfeed.config;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @author Spencer Gibb
 */
@SpringCloudApplication
@EnableConfigServer
public class ConfigApp {
	public static void main(String[] args) {
		SpringApplication.run(ConfigApp.class, args);
	}
}
