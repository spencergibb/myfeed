package myfeed;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.ribbon.RibbonInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Spencer Gibb
 */
@Configuration
public class MyfeedAutoConfig {

	@Bean
	public Rest rest(RibbonInterceptor interceptor) {
		Rest restTemplate = new Rest();
		configureRest(interceptor, restTemplate);

		return restTemplate;
	}

	private void configureRest(RibbonInterceptor interceptor, Rest restTemplate) {
		restTemplate.setInterceptors(Arrays.asList(interceptor));

		List<HttpMessageConverter<?>> converters = getHttpMessageConverters();
		restTemplate.setMessageConverters(converters);
	}

	private List<HttpMessageConverter<?>> getHttpMessageConverters() {
		List<HttpMessageConverter<?>> converters = new ArrayList<>();
		converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jackson2HalModule());
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

		converter.setObjectMapper(mapper);
		converter.setSupportedMediaTypes(Arrays.asList(MediaTypes.HAL_JSON));

		converters.add(converter);
		return converters;
	}

	@Bean
	public RibbonAsyncClientHttpRequestFactory asyncRequestFactory(LoadBalancerClient loadBalancerClient) {
		RibbonAsyncClientHttpRequestFactory requestFactory = new RibbonAsyncClientHttpRequestFactory(loadBalancerClient);
		requestFactory.setTaskExecutor(new SimpleAsyncTaskExecutor());
		return requestFactory;
	}

	@Bean
	public AsyncRest asyncRest(RibbonInterceptor interceptor, LoadBalancerClient loadBalancer) {
		RibbonAsyncClientHttpRequestFactory requestFactory = asyncRequestFactory(loadBalancer);
		Rest rest = new Rest(requestFactory);
		configureRest(interceptor, rest);
		AsyncRest asyncRest = new AsyncRest(requestFactory, rest);
		asyncRest.setMessageConverters(getHttpMessageConverters());
		return asyncRest;
	}

	@Bean
	public RestTemplate restTemplate(RibbonInterceptor interceptor) {
		return rest(interceptor);
	}

	@Bean
	public AsyncRestTemplate asyncRestTemplate(RibbonInterceptor interceptor, LoadBalancerClient loadBalancer) {
		return asyncRest(interceptor, loadBalancer);
	}

	@Bean
	public TraversonFactory traversonFactory(LoadBalancerClient loadBalancerClient) {
		return new TraversonFactory(loadBalancerClient);
	}
}
