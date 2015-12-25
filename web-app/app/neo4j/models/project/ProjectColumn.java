package neo4j.models.project;

import java.util.Map;

/**
 * @author Johannes Unterstein (unterstein@me.com)
 */
public class ProjectColumn {

  public String key;

  public String name;

  public ProjectColumnType type;

  public Map<String, String> properties;
}
