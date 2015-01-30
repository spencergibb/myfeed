package myfeed.router;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author Spencer Gibb
 */
@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
@EnableRedisHttpSession
public class RouterApp {
	public static void main(String[] args) {
		SpringApplication.run(RouterApp.class, args);
	}
}
