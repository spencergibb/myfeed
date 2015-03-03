package myfeed.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Spencer Gibb
 */
@RestController
public class WhoAmIController {

	@Autowired
	private DiscoveryClient discovery;

	@RequestMapping("/whoami")
	public ServiceInstance whoami() {
		return discovery.getLocalServiceInstance();
	}
}
