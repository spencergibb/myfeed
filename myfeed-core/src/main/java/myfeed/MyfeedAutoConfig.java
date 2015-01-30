package myfeed;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.netflix.ribbon.RibbonInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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

	@Bean
	RestTemplate restTemplate(RibbonInterceptor interceptor) {
		return rest(interceptor);
	}
}
