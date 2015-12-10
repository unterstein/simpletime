package neo4j.services;

import neo4j.repositories.UserRepository;
import neo4jplugin.Neo4jPlugin;
import neo4jplugin.Neo4jServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Johannes Unterstein (unterstein@me.com)
 */
@Component
public class DataBaseServiceProvider extends Neo4jServiceProvider {

  @Autowired
  public UserRepository entryRepository;

  private DataBaseServiceProvider() {
  }

  public static DataBaseServiceProvider get() {
    return Neo4jPlugin.get();
  }
}
