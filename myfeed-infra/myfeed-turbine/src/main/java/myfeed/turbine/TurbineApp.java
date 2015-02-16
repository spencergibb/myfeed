package myfeed.turbine;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.turbine.amqp.EnableTurbineAmqp;

/**
 * @author Spencer Gibb
 */
@SpringCloudApplication
@EnableTurbineAmqp
public class TurbineApp {

	public static void main(String[] args) {
		SpringApplication.run(TurbineApp.class, args);
	}
}
