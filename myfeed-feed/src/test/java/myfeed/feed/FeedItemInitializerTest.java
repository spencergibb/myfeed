package myfeed.feed;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.Resource;

import rx.Observable;
import rx.Single;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

/**
 * @author Spencer Gibb
 */
public class FeedItemInitializerTest {
	@Mock private FeedItemRepository repo;
	@Mock private UserService user;
	@Mock private RandomText randomText;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testInitUser() {
		when(user.findId("me")).thenReturn(Single.just("1"));
		when(randomText.getText(anyInt())).thenReturn("My random text");
		when(user.getUsers()).thenReturn(Observable.from(Arrays.asList(new Resource<>(new User("1", "me", "Me")))));
		FeedItemInitializer initializer = new FeedItemInitializer(this.repo, this.user, this.randomText);
		initializer.minItems = 5;
		Single<List<FeedItem>> single = initializer.init("me");
		assertThat("single was null", single, is(notNullValue()));
		List<FeedItem> items = single.toBlocking().value();
		assertThat("items was null", items, is(notNullValue()));
		assertThat("items was wrong size", items.size(), is(greaterThanOrEqualTo(5)));
	}
}
