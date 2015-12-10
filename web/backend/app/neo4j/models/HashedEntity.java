package neo4j.models;

import logic.HashHelper;
import org.springframework.data.neo4j.annotation.Indexed;


/**
 * @author Johannes Unterstein (unterstein@me.com)
 */
public abstract class HashedEntity extends AbstractEntity {

  @Indexed
  public String hash;

  public HashedEntity() {
    hash = HashHelper.uuid();
  }
}
