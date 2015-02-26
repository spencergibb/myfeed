package myfeed.feed;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Spencer Gibb
 */
@Component
@Slf4j
public class FeedItemListener implements ApplicationListener<FeedItemEvent> {

	@Autowired
	FeedService service;

	@Override
	public void onApplicationEvent(FeedItemEvent event) {
		log.info("Propagate feedItem: {}", event.getFeedItem());
		service.propagate(event.getFeedItem());
	}
}
