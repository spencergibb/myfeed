package myfeed.feed;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.PagedResources;
import org.springframework.stereotype.Service;

import org.springframework.util.StringUtils;
import rx.Observable;

/**
 * @author Spencer Gibb
 */
@Service
public class FeedService {
	@Autowired
	FeedItemRepository repo;

	@Autowired
	UserService user;

	public Observable<Page<FeedItem>> feed(String username) {
		return user.findId(username)
				.filter(StringUtils::hasText)
				.map(userid -> repo.findByUseridOrderByCreatedDesc(userid, new PageRequest(0, 20)))
				.defaultIfEmpty(singletonFeed("Unknown user: " + username));
	}

	protected Page<FeedItem> defaultFeed(String username) {
		return singletonFeed("Something's not right.  Check back in a moment");
	}

	private Page<FeedItem> singletonFeed(String text) {
		return new PageImpl<>(Arrays.asList(new FeedItem("1", "myfeed", text)));
	}

	//TODO: Observable
	public PagedResources<FeedItem> getUserResource(String username) {
		Page<FeedItem> items = repo.findByUseridOrderByCreatedDesc(user.findId(username).toBlocking().first(),
				new PageRequest(0, 20));
		return new PagedResources<>(items.getContent(), getMetadata(items));
	}

	public static PagedResources.PageMetadata getMetadata(Page<?> page) {
		return new PagedResources.PageMetadata(page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages());
	}
}
