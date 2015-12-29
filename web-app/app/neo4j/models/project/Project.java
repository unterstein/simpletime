package neo4j.models.project;

import neo4j.Relations;
import neo4j.models.HashedEntity;
import neo4j.models.time.TimeEntry;
import neo4j.models.user.User;
import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.List;
import java.util.Set;

/**
 * @author Johannes Unterstein (unterstein@me.com)
 */
@NodeEntity
@TypeAlias("Project")
public class Project extends HashedEntity {

  public String name;

  public List<ProjectColumn> columns;

  @RelatedTo(type = Relations.USER_PROJECT, direction = Direction.INCOMING)
  public User user;

  @RelatedTo(type = Relations.TIME_PROJECT, direction = Direction.INCOMING)
  public Set<TimeEntry> timeEntries;

}
