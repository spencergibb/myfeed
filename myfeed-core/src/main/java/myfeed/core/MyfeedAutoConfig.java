package myfeed.core;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer;
import org.springframework.cloud.netflix.ribbon.RibbonClientHttpRequestFactory;
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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Spencer Gibb
 */
@Configuration
public class MyfeedAutoConfig {

    @Bean
    public RestTemplateCustomizer restTemplateCustomizer(final RibbonClientHttpRequestFactory requestFactory) {
        return restTemplate -> {
            restTemplate.setRequestFactory(requestFactory);
            addConverters(restTemplate);
        };
    }

	private void addConverters(RestTemplate restTemplate) {
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
	public AsyncRest asyncRest(LoadBalancerInterceptor interceptor, LoadBalancerClient loadBalancer) {
		RibbonAsyncClientHttpRequestFactory requestFactory = asyncRequestFactory(loadBalancer);
		Rest rest = new Rest(requestFactory);
		addConverters(rest);
		AsyncRest asyncRest = new AsyncRest(requestFactory, rest);
		asyncRest.setMessageConverters(getHttpMessageConverters());
		return asyncRest;
	}

	@Bean
    @LoadBalanced
	public RestTemplate loadBalancedRestTemplate(RestTemplateCustomizer customizer) {
		return rest(customizer);
	}

	@Bean
	public Rest rest(RestTemplateCustomizer customizer) {
		Rest rest = new Rest();
		customizer.customize(rest);
		return rest;
	}

	@Bean
	public AsyncRestTemplate asyncRestTemplate(LoadBalancerInterceptor interceptor, LoadBalancerClient loadBalancer) {
		return asyncRest(interceptor, loadBalancer);
	}

	@Bean
	public TraversonFactory traversonFactory(LoadBalancerClient loadBalancerClient) {
		return new TraversonFactory(loadBalancerClient);
	}

	@Bean
	public WhoAmIController whoAmIController() {
		return new WhoAmIController();
	}

}
