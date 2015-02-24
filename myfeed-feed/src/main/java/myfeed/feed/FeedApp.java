package myfeed.feed;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedResources;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import de.svenjacobs.loremipsum.LoremIpsum;

/**
 * @author Spencer Gibb
 */
@SpringCloudApplication
@RestController
public class FeedApp {
	@Autowired
	private FeedService service;

	@RequestMapping(value = "/{username}", method = GET)
	public List<FeedItem> feedList(@PathVariable("username") String username) {
		return service.feed(username).toBlocking().first().getContent();
	}

	@RequestMapping(value = "/@{username}", method = GET)
	public Page<FeedItem> feed(@PathVariable("username") String username) {
		return service.feed(username).toBlocking().first();
	}

	@RequestMapping(value = "/@@{username}", method = GET)
	@HystrixCommand
	public PagedResources<FeedItem> getUserResource(@PathVariable("username") String username) {
		return service.getUserResource(username);
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
		SpringApplication.run(FeedApp.class, args);
	}
}
