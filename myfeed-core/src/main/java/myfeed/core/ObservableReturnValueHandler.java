package myfeed.core;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import rx.Observable;

/**
 * @author Spencer Gibb
 */
public class ObservableReturnValueHandler implements HandlerMethodReturnValueHandler {
	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return Observable.class.isAssignableFrom(returnType.getParameterType());
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
		if (returnValue == null) {
			return;
		}

		Observable<?> observable = Observable.class.cast(returnValue);

		ObservableAdapter<?> adapter = new ObservableAdapter<>(observable);
		WebAsyncUtils.getAsyncManager(webRequest)
				.startDeferredResultProcessing(adapter, mavContainer);
	}
}
