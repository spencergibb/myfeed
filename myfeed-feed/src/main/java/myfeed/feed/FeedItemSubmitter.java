package myfeed.feed;

import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

/**
 * @author Spencer Gibb
 */
@MessagingGateway(name = "feedItemSubmitter")
public interface FeedItemSubmitter {
	@Gateway(requestChannel = Source.OUTPUT)
	void submit(FeedItem feedItem);
}
