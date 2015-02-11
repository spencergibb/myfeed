package myfeed;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Traverson;

import java.net.URI;

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
		//TODO: supprt https?  ServiceInstance create URI?
		URI uri = URI.create(String.format("http://%s:%s", instance.getHost(), instance.getPort()));
		Traverson traverson = new Traverson(uri, MediaTypes.HAL_JSON);
		return traverson;
	}
}
