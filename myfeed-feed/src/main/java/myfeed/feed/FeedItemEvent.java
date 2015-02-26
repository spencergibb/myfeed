package myfeed.feed;

import org.springframework.context.ApplicationEvent;

/**
 * @author Spencer Gibb
 */
public class FeedItemEvent extends ApplicationEvent {
	/**
	 * Create a new ApplicationEvent.
	 *
	 * @param source the component that published the event (never {@code null})
	 */
	public FeedItemEvent(FeedItem source) {
		super(source);
	}

	public FeedItem getFeedItem() {
		return FeedItem.class.cast(source);
	}
}
