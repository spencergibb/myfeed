package myfeed.core;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

/**
 * @author Spencer Gibb
 */
public class AsyncRest extends AsyncRestTemplate {

	public AsyncRest() {
	}

	public AsyncRest(AsyncListenableTaskExecutor taskExecutor) {
		super(taskExecutor);
	}

	public AsyncRest(AsyncClientHttpRequestFactory asyncRequestFactory) {
		super(asyncRequestFactory);
	}

	public AsyncRest(AsyncClientHttpRequestFactory asyncRequestFactory, ClientHttpRequestFactory syncRequestFactory) {
		super(asyncRequestFactory, syncRequestFactory);
	}

	public AsyncRest(AsyncClientHttpRequestFactory requestFactory, RestTemplate restTemplate) {
		super(requestFactory, restTemplate);
	}

	public <T> ListenableFuture<ResponseEntity<T>> get(String url, ParameterizedTypeReference<T> responseType, Object... uriVariables) {
		return exchange(url, HttpMethod.GET, null, responseType, uriVariables);
	}
}
