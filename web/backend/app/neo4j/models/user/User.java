package neo4j.models.user;

import logic.HashHelper;
import neo4j.models.HashedEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;

/**
 * @author Johannes Unterstein (unterstein@me.com)
 */
@NodeEntity
@TypeAlias("User")
public class User extends HashedEntity {

  @Indexed
  public String email;

  public String password;

  public boolean checkPassword(String password) {
    return StringUtils.equals(this.password, HashHelper.saltedHash(password));
  }
}
