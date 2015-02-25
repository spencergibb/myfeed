package myfeed.user;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Spencer Gibb
 */
public interface UserRepository extends PagingAndSortingRepository<User, String> {

	public User findByUsername(@Param("username") String username);
	public String deleteByUsername(@Param("username") String username);
	public List<User> findByFollowing(@Param("userId") String userId);
}
