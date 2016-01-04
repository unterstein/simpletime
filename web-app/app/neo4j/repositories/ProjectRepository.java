package neo4j.repositories;

import neo4j.models.project.Project;
import neo4j.models.user.User;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.List;

/**
 * @author Johannes Unterstein (unterstein@me.com)
 */
public interface ProjectRepository extends GraphRepository<Project> {

  Project findByHashAndUser(String hash, User user);

  List<Project> findByUserAndActive(User user, boolean active);
}
