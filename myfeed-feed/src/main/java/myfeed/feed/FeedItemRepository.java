package myfeed.feed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author Spencer Gibb
 */
public interface FeedItemRepository extends PagingAndSortingRepository<FeedItem, String> {

	public Page<FeedItem> findByUseridOrderByCreatedDesc(@Param("userid") String userid, Pageable pageable);
}
