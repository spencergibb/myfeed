package myfeed.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Spencer Gibb
 */
@Data
@Document
public class User {

	@Id
	private final String id;
}
