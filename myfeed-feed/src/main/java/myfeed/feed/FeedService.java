package myfeed.feed;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.PagedResources;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import de.svenjacobs.loremipsum.LoremIpsum;

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
	public Page<FeedItem> feed(@PathVariable("username") String username) {
		if (username.equals("error")) throw new RuntimeException(username);
		Page<FeedItem> items = repo.findByUseridOrderByCreatedDesc(user.findId(username), new PageRequest(0, 20));
		return items;
	}

	protected Page<FeedItem> defaultFeed(String username) {
		return new PageImpl<>(Arrays.asList(new FeedItem("1", "myfeed", "Something's not right.  Check back in a moment")));
	}

	@RequestMapping(value = "/@@{username}", method = GET)
	public PagedResources<FeedItem> getUserResource(@PathVariable("username") String username) {
		Page<FeedItem> items = repo.findByUseridOrderByCreatedDesc(user.findId(username),
				new PageRequest(0, 20));
		return new PagedResources<>(items.getContent(), getMetadata(items));
	}

	public static PagedResources.PageMetadata getMetadata(Page<?> page) {
		return new PagedResources.PageMetadata(page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages());
	}

	@Bean
	public RandomText randomText() {
		return new RandomText() {
			LoremIpsum loremIpsum = new LoremIpsum();

			@Override
			public String getText(int numWords) {
				String lorem = loremIpsum.getWords(numWords);
				String[] words = lorem.split(" ");
				List<String> list = Arrays.asList(words);
				Collections.shuffle(list);
				return StringUtils.collectionToDelimitedString(list, " ");
			}
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(FeedService.class, args);
	}
}
