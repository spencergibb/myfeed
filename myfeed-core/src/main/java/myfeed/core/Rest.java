package myfeed.core;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * @author Spencer Gibb
 */
public class Rest extends RestTemplate {

	public Rest() {
	}

	public Rest(ClientHttpRequestFactory requestFactory) {
		super(requestFactory);
	}

	public Rest(List<HttpMessageConverter<?>> messageConverters) {
		super(messageConverters);
	}

	public <T> ResponseEntity<T> get(String url, ParameterizedTypeReference<T> responseType, Object... uriVariables) {
		return exchange(url, HttpMethod.GET, null, responseType, uriVariables);
	}
}
