package myfeed.ui;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * @author Spencer Gibb
 */
@Controller
public class UiMessageController {

	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public String greeting(String message) {
		return "Hello, " + message + "!";
	}

}
