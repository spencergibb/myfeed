package myfeed.feed;

import com.mongodb.DBObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.stereotype.Component;

/**
 * @author Spencer Gibb
 */
@Component
@Slf4j
public class FeedItemListener extends AbstractMongoEventListener<FeedItem> {
	@Override
	public void onAfterSave(FeedItem feedItem, DBObject dbo) {
		log.info("Saved feedItem: {}", feedItem);
	}
}
