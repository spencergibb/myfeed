package myfeed.core;

import org.springframework.web.context.request.async.DeferredResult;
import rx.Observable;

/**
 * http://www.nurkiewicz.com/2013/03/deferredresult-asynchronous-processing.html
 * @author Spencer Gibb
 */
public class ObservableAdapter<T> extends DeferredResult<T> {

	public ObservableAdapter(Observable<T> observable) {
		observable.subscribe(this::setResult, this::setErrorResult);
	}
}
