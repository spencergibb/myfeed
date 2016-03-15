package myfeed.user;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author Spencer Gibb
 */
public interface UserRepository extends CrudRepository<User, String> { //PagingAndSortingRepository<User, String> {

	User findByUsername(@Param("username") String username);
	String deleteByUsername(@Param("username") String username);
	List<User> findByFollowing(@Param("userId") String userId);
}
