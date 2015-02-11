package myfeed.feed;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Spencer Gibb
 */
@RestController
@Slf4j
public class FeedItemInitializer {

	private final FeedItemRepository repo;
	private final UserService user;
	private final RandomText randomText;
	private final Random random = new Random();

	@Autowired
	public FeedItemInitializer(FeedItemRepository repo, UserService user, RandomText randomText) {
		this.repo = repo;
		this.user = user;
		this.randomText = randomText;
	}

	@RequestMapping("/init")
	public Iterable<FeedItem> init(@RequestParam(value = "user") String username) {
		String userid = user.findId(username);
		int numItems = random.nextInt(5);

		List<FeedItem> items = new ArrayList<>(numItems);
		for (int i = numItems; i > 0; i--) {
			int numWords = random.nextInt(20);
			String text = randomText.getText(numWords);
			LocalDateTime dateTime = LocalDateTime.now().minusDays(i);
			Instant instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();
			Date created = Date.from(instant);
			FeedItem item = new FeedItem(userid, username, text, created);
			items.add(item);

		}
		Iterable<FeedItem> saved = repo.save(items);
		return saved;
	}
}
