package neo4j.repositories;

import neo4j.models.project.Project;
import neo4j.models.time.TimeEntry;
import org.springframework.data.neo4j.repository.GraphRepository;

import java.util.List;

/**
 * @author Johannes Unterstein (unterstein@me.com)
 */
public interface TimeEntryRepository extends GraphRepository<TimeEntry> {

  TimeEntry findByHash(String hash);

  List<TimeEntry> findByProject(Project project);
}
