package myfeed;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.io.IOException;
import java.net.URI;

/**
 * @author Spencer Gibb
 */
public class RibbonAsyncClientHttpRequestFactory extends SimpleClientHttpRequestFactory {
	private LoadBalancerClient loadBalancer;

	public RibbonAsyncClientHttpRequestFactory(LoadBalancerClient loadBalancer) {
		this.loadBalancer = loadBalancer;
	}

	@Override
	public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
		return super.createRequest(expand(uri), httpMethod);
	}

	@Override
	public AsyncClientHttpRequest createAsyncRequest(URI uri, HttpMethod httpMethod) throws IOException {
		return super.createAsyncRequest(expand(uri), httpMethod);
	}

	private URI expand(URI uri) {
		String serviceId = uri.getHost();
		ServiceInstance instance = this.loadBalancer.choose(serviceId);
		return this.loadBalancer.reconstructURI(instance, uri);
	}
}
