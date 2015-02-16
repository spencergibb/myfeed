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
import rx.plugins.DebugHook;
import rx.plugins.DebugNotification;
import rx.plugins.DebugNotificationListener;
import rx.plugins.RxJavaPlugins;

import java.util.Arrays;
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

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void feed() {
		when(repo.findByUseridOrderByCreatedDesc(eq(USERID), isA(PageRequest.class)))
				.thenReturn(new PageImpl<>(Arrays.asList(new FeedItem(USERID, USERNAME, FEEDTEXT), new FeedItem(USERID, USERNAME, FEEDTEXT+"2"))));
		when(user.findId(USERNAME)).thenReturn(Observable.just(USERID));

		FeedService service = createService();

		Observable<Page<FeedItem>> feed = service.feed(USERNAME);

		assertNotNull("feed was null", feed);
		Page<FeedItem> page = feed.toBlocking().first();
		assertEquals("wront page size", 2, page.getNumberOfElements());
		List<FeedItem> content = page.getContent();
		FeedItem item = content.get(0);
		assertNotNull("null item id", item.getId());
		assertEquals("wrong item userid", USERID, item.getUserid());
		assertEquals("wrong item username", USERNAME, item.getUsername());
		assertEquals("wrong item text", FEEDTEXT, item.getText());

		verify(repo).findByUseridOrderByCreatedDesc(eq(USERID), isA(PageRequest.class));
		verify(user).findId(USERNAME);
	}

	@Test
	public void feedNotFound() {
		when(user.findId(USERNAME)).thenReturn(Observable.just(null));

		FeedService service = createService();
		Observable<Page<FeedItem>> feed = service.feed(USERNAME);

		assertNotNull("feed was null", feed);
		Page<FeedItem> page = feed.toBlocking().first();
		assertEquals("wront page size", 1, page.getNumberOfElements());
		List<FeedItem> content = page.getContent();
		FeedItem item = content.get(0);
		assertEquals("wrong item text", "Unknown user: "+USERNAME, item.getText());

		verify(user).findId(USERNAME);
	}

	private FeedService createService() {
		FeedService service = new FeedService();
		service.user = user;
		service.repo = repo;
		return service;
	}
}
