package myfeed.feed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Spencer Gibb
 */
@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class FeedApp {

	@RequestMapping("/")
	public String[] home() {
		return new String[]{ "Hello feed" };
	}

	public static void main(String[] args) {
		SpringApplication.run(FeedApp.class, args);
	}
}
