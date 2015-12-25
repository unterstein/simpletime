package neo4j.repositories;

import neo4j.models.project.Project;
import org.springframework.data.neo4j.repository.GraphRepository;

/**
 * @author Johannes Unterstein (unterstein@me.com)
 */
public interface ProjectRepository extends GraphRepository<Project> {

  Project findByHash(String hash);
}
