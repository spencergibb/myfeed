package myfeed.admin;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.ribbon.RibbonInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.nio.charset.Charset;
import java.util.*;

/**
 * @author Spencer Gibb
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableHystrixDashboard
@RestController
public class AdminApp {
	public static final ParameterizedTypeReference<Resources<Resource<User>>> USERS_TYPE = new ParameterizedTypeReference<Resources<Resource<User>>>() {
	};

	@Bean
	public RestTemplate restTemplate(RibbonInterceptor interceptor) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setInterceptors(Arrays.asList(interceptor));

		//List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
		List<HttpMessageConverter<?>> converters = new ArrayList<>();
		converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jackson2HalModule());
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

		converter.setObjectMapper(mapper);
		converter.setSupportedMediaTypes(Arrays.asList(MediaTypes.HAL_JSON));

		converters.add(converter);
		restTemplate.setMessageConverters(converters);

		return restTemplate;
	}

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	LoadBalancerClient lb;

	@RequestMapping("/")
	public ModelAndView home() {
		return new ModelAndView("admin");
	}

	@RequestMapping("/users")
	@SuppressWarnings("unchecked")
	public ModelAndView users() {
		//TODO: why doesn't ID show up
		ResponseEntity<Resources<Resource<User>>> response = restTemplate.exchange("http://myfeed-user/users", HttpMethod.GET, null, USERS_TYPE, Collections.emptyMap());
		return new ModelAndView("users", Collections.singletonMap("users", response.getBody().getContent()));
	}

	@Data
	public static class User {
		private String id;
		private String username;
		private String name;

		private User() {
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(AdminApp.class, args);
	}
}
