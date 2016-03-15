package myfeed.user;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

/**
 * @author Spencer Gibb
 */
@MessagingGateway(name = "userIntializedSender")
public interface UserIntializedSender {
	@Gateway(requestChannel = UserBinder.USER_INITIALIZED)
	void send(User user);
}
