package neo4j.models.project;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import neo4j.Relations;
import neo4j.models.HashedEntity;
import neo4j.models.time.TimeEntry;
import neo4j.models.user.User;
import org.neo4j.graphdb.Direction;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import java.beans.Transient;
import java.util.List;
import java.util.Set;

/**
 * @author Johannes Unterstein (unterstein@me.com)
 */
@NodeEntity
@TypeAlias("Project")
public class Project extends HashedEntity {

  public String name;

  public String columns;

  public boolean active = true;

  @RelatedTo(type = Relations.USER_PROJECT, direction = Direction.INCOMING)
  public User user;

  @RelatedTo(type = Relations.TIME_PROJECT, direction = Direction.INCOMING)
  public Set<TimeEntry> timeEntries;

  public List<ProjectColumn> getColumns() {
    return new Gson().fromJson(columns, new TypeToken<List<ProjectColumn>>(){}.getType());
  }

  public void setColumns(List<ProjectColumn> columns) {
    this.columns = new Gson().toJson(columns);
  }
}
