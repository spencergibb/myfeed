package myfeed.feed;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Spencer Gibb
 */
@RestController
@Slf4j
public class FeedItemInitializer {

	private FeedItemRepository repo;
	private UserService user;

	@Autowired
	public FeedItemInitializer(FeedItemRepository repo, UserService user) {
		this.repo = repo;
		this.user = user;
	}

	@RequestMapping("/init")
	public Iterable<FeedItem> init() {
		String userid = user.findId("spencergibb");
		List<FeedItem> items = Arrays.asList(new FeedItem(userid, "first text"),
				new FeedItem(userid, "second text"));
		Iterable<FeedItem> saved = repo.save(items);
		return saved;
	}
}
