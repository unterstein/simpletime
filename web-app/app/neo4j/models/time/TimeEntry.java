package neo4j.models.time;

import neo4j.Relations;
import neo4j.models.HashedEntity;
import neo4j.models.project.Project;
import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Map;

/**
 * @author Johannes Unterstein (unterstein@me.com)
 */
@NodeEntity
@TypeAlias("TimeEntry")
public class TimeEntry extends HashedEntity {

  @RelatedTo(type = Relations.TIME_PROJECT, direction = Direction.OUTGOING)
  public Project project;

  public Long startTime;

  public Long endTime;

  public Map<String, Object> properties;
}
