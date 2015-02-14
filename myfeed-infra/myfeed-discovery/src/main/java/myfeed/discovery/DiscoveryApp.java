package myfeed.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author Spencer Gibb
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryApp {
	public static void main(String[] args) {
		SpringApplication.run(DiscoveryApp.class, args);
	}
}
