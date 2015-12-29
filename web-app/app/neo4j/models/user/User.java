package neo4j.models.user;

import neo4j.Relations;
import neo4j.models.HashedEntity;
import neo4j.models.project.Project;
import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.util.Set;

/**
 * @author Johannes Unterstein (unterstein@me.com)
 */
@NodeEntity
@TypeAlias("User")
public class User extends HashedEntity {

  @Indexed
  public String email;

  public String password;

  @RelatedTo(type = Relations.TIME_PROJECT, direction = Direction.OUTGOING)
  public Set<Project> projects;
}
