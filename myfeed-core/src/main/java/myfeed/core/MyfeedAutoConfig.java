package myfeed.core;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
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
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import rx.Observable;

import javax.annotation.PostConstruct;
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
	public Rest rest(LoadBalancerInterceptor interceptor) {
		Rest restTemplate = new Rest();
		configureRest(interceptor, restTemplate);

		return restTemplate;
	}

	private void configureRest(LoadBalancerInterceptor interceptor, Rest restTemplate) {
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
	public AsyncRest asyncRest(LoadBalancerInterceptor interceptor, LoadBalancerClient loadBalancer) {
		RibbonAsyncClientHttpRequestFactory requestFactory = asyncRequestFactory(loadBalancer);
		Rest rest = new Rest(requestFactory);
		configureRest(interceptor, rest);
		AsyncRest asyncRest = new AsyncRest(requestFactory, rest);
		asyncRest.setMessageConverters(getHttpMessageConverters());
		return asyncRest;
	}

	@Bean
	public RestTemplate restTemplate(LoadBalancerInterceptor interceptor) {
		return rest(interceptor);
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

	@Configuration
	@ConditionalOnClass(Observable.class)
	public static class WebConfig extends WebMvcConfigurerAdapter {
		@Autowired
		private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

		@Bean
		public ObservableReturnValueHandler observableReturnValueHandler() {
			return new ObservableReturnValueHandler();
		}

		@PostConstruct
		public void init() {
			final List<HandlerMethodReturnValueHandler> originalHandlers = new ArrayList<>(requestMappingHandlerAdapter.getReturnValueHandlers());
			originalHandlers.add(0, observableReturnValueHandler());
			requestMappingHandlerAdapter.setReturnValueHandlers(originalHandlers);
		}

		/*@Override
		public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
			returnValueHandlers.add(0, observableReturnValueHandler());
		}*/
	}
}
