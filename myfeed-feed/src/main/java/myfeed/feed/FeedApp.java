package myfeed.feed;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.svenjacobs.loremipsum.LoremIpsum;
import rx.Single;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Spencer Gibb
 */
@SpringCloudApplication
@EnableRedisRepositories
@EnableBinding( {Source.class, Sink.class} )
@IntegrationComponentScan
@RestController
public class FeedApp extends RepositoryRestConfigurerAdapter {
	@Autowired
	private FeedService service;

	@RequestMapping(value = "/list/{username}", method = GET)
	public Single<List<FeedItem>> feedList(@PathVariable("username") String username) {
		return service.feed(username).toSingle();
	}

	@RequestMapping(value = "/@{username}", method = POST)
	public Single<FeedItem> addFeedItem(@PathVariable("username") String username, @RequestBody String text) {
		return service.addFeedItem(username, text);
	}

	@RequestMapping(value = "/@{username}", method = GET)
	public Single<List<FeedItem>> feed(@PathVariable("username") String username) {
		return service.feed(username).toSingle();
	}

	@RequestMapping(value = "/@@{username}", method = GET)
	//@HystrixCommand
	public Single<List<FeedItem>> getUserResource(@PathVariable("username") String username) {
		return service.feed(username).toSingle();
	}

	@Override
	public void configureRepositoryRestConfiguration( RepositoryRestConfiguration config) {
		config.exposeIdsFor(FeedItem.class);
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
