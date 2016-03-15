package myfeed.core;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Traverson;

/**
 * @author Spencer Gibb
 */
public class TraversonFactory {

	private final LoadBalancerClient loadBalancerClient;

	public TraversonFactory(LoadBalancerClient loadBalancerClient) {
		this.loadBalancerClient = loadBalancerClient;
	}

	public Traverson create(String serviceId) {
		ServiceInstance instance = loadBalancerClient.choose(serviceId);
		if (instance == null) {
			throw new IllegalStateException("No instances for service: "+serviceId);
		}
		return new Traverson(instance.getUri(), MediaTypes.HAL_JSON);
	}
}
