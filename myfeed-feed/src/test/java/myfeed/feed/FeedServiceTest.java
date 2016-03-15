package myfeed.feed;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import rx.Observable;
import rx.Single;
import rx.plugins.DebugHook;
import rx.plugins.DebugNotification;
import rx.plugins.DebugNotificationListener;
import rx.plugins.RxJavaPlugins;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Spencer Gibb
 */
@SuppressWarnings("unchecked")
public class FeedServiceTest {
	public static final String USERID = "111";
	public static final String USERNAME = "userA";
	public static final String FEEDTEXT = "this is a feeditem";
	public static Logger logger = LoggerFactory.getLogger(FeedServiceTest.class);

	static {
		RxJavaPlugins.getInstance().registerObservableExecutionHook(new DebugHook(new DebugNotificationListener() {
			public Object onNext(DebugNotification n) {
				logger.info("onNext on " + n);
				return super.onNext(n);
			}

			public Object start(DebugNotification n) {
				logger.info("start on " + n);
				return super.start(n);
			}

			public void complete(Object context) {
				super.complete(context);
				logger.info("complete on " + context);
			}

			public void error(Object context, Throwable e) {
				super.error(context, e);
				logger.error("error on " + context, e);
			}
		}));
	}

	@Mock private FeedItemRepository repo;
	@Mock private UserService user;
	@Mock private FeedItemSubmitter feedItemSubmitter;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void feed() {
		when(repo.findByUserid(eq(USERID)))//, isA(PageRequest.class)))
				.thenReturn(Arrays.asList(new FeedItem(USERID, USERNAME, FEEDTEXT, Date.from(LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC))), new FeedItem(USERID, USERNAME, FEEDTEXT+"2")));
		when(user.findId(USERNAME)).thenReturn(Single.just(USERID));

		FeedService service = new FeedService(user, repo, feedItemSubmitter);

		Observable<List<FeedItem>> feed = service.feed(USERNAME);

		assertNotNull("feed was null", feed);
		List<FeedItem> content = feed.toBlocking().first();
		assertEquals("wrong size", 2, content.size());
		//List<FeedItem> content = page.getContent();
		FeedItem item = content.get(0);
		assertNotNull("null item id", item.getId());
		assertEquals("wrong item userid", USERID, item.getUserid());
		assertEquals("wrong item username", USERNAME, item.getUsername());
		assertEquals("wrong item text", FEEDTEXT, item.getText());

		verify(repo).findByUserid(eq(USERID)); //, isA(PageRequest.class));
		verify(user).findId(USERNAME);
	}

	@Test
	public void feedNotFound() {
		when(user.findId(USERNAME)).thenReturn(Single.just(null));

		FeedService service = new FeedService(user, repo, feedItemSubmitter);
		Observable<List<FeedItem>> feed = service.feed(USERNAME);

		assertNotNull("feed was null", feed);
		List<FeedItem> content = feed.toBlocking().first(); //page.getContent();
		assertEquals("wrong feed size", 1, content.size());
		FeedItem item = content.get(0);
		assertEquals("wrong item text", "Unknown user: "+USERNAME, item.getText());

		verify(user).findId(USERNAME);
	}
}
