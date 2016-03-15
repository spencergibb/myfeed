package myfeed.feed;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author Spencer Gibb
 */
public interface FeedBinder {
	String USER_INITIALIZED = "userinitialized";

	@Input(FeedBinder.USER_INITIALIZED)
	SubscribableChannel userInitialized();
}
