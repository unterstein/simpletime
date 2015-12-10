package neo4j.repositories;

import neo4j.models.user.User;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.List;

/**
 * @author Johannes Unterstein (unterstein@me.com)
 */
public interface UserRepository extends GraphRepository<User> {

  List<User> findByHash(String hash);
}
