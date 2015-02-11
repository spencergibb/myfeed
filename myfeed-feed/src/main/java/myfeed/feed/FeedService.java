package myfeed.feed;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.PagedResources;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

/**
 * @author Spencer Gibb
 */
@Service
public class FeedService {
	@Autowired
	private FeedItemRepository repo;

	@Autowired
	private UserService user;

	@HystrixCommand(fallbackMethod = "defaultFeed")
	public Page<FeedItem> feed(String username) {
		if (username.equals("error")) throw new RuntimeException(username);
		String userid = user.findId(username);
		if (!StringUtils.hasText(userid)) {
			return singletonFeed("Unknown user: "+username);
		}
		Page<FeedItem> items = repo.findByUseridOrderByCreatedDesc(userid, new PageRequest(0, 20));
		return items;
	}

	protected Page<FeedItem> defaultFeed(String username) {
		return singletonFeed("Something's not right.  Check back in a moment");
	}

	private Page<FeedItem> singletonFeed(String text) {
		return new PageImpl<>(Arrays.asList(new FeedItem("1", "myfeed", text)));
	}

	public PagedResources<FeedItem> getUserResource(String username) {
		Page<FeedItem> items = repo.findByUseridOrderByCreatedDesc(user.findId(username),
				new PageRequest(0, 20));
		return new PagedResources<>(items.getContent(), getMetadata(items));
	}

	public static PagedResources.PageMetadata getMetadata(Page<?> page) {
		return new PagedResources.PageMetadata(page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages());
	}
}
