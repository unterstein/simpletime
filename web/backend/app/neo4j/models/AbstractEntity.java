package neo4j.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.neo4j.annotation.GraphId;


/**
 * @author Johannes Unterstein (unterstein@me.com)
 */
public abstract class AbstractEntity {

  @GraphId
  public long id;

  @CreatedDate
  public Long createdDate;

  @LastModifiedDate
  public Long lastModifiedDate;

}
