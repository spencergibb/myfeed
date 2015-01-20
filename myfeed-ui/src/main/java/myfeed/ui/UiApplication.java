package myfeed.ui;

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
public class UiApplication {

	@RequestMapping("/")
	public String home() {
		return "Hello myfeed";
	}

	public static void main(String[] args) {
		SpringApplication.run(UiApplication.class, args);
	}
}
