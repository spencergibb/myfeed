package myfeed.feed;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author Spencer Gibb
 */
public interface FeedItemRepository extends CrudRepository<FeedItem, String> { //PagingAndSortingRepository<FeedItem, String> {

	//Page<FeedItem> findByUseridOrderByCreatedDesc(@Param("userid") String userid, Pageable pageable);
	List<FeedItem> findByUserid(@Param("userid") String userid);
}
