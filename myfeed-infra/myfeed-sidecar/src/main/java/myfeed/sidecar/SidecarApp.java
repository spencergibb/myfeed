package myfeed.sidecar;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.sidecar.EnableSidecar;

/**
 * @author Spencer Gibb
 */
@SpringCloudApplication
@EnableSidecar
public class SidecarApp {

	public static void main(String[] args) {
		SpringApplication.run(SidecarApp.class, args);
	}

}


