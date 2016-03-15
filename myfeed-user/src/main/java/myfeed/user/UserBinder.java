package myfeed.user;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @author Spencer Gibb
 */
public interface UserBinder {
	String USER_INITIALIZED = "userinitialized";

	@Output(UserBinder.USER_INITIALIZED)
	MessageChannel userinitialized();
}
