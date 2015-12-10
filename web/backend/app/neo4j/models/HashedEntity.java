package neo4j.models;

import logic.HashHelper;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.annotation.TypeAlias;

/**
 * @author Johannes Unterstein (unterstein@me.com)
 */
@NodeEntity
@TypeAlias("HashedEntity")
public abstract class HashedEntity extends AbstractEntity {

  @Indexed
  public String hash;

  public HashedEntity() {
    hash = HashHelper.uuid();
  }
}
