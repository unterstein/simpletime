package logic

import neo4j.models.user.User
import neo4j.services.Neo4jProvider

/**
 * @author Johannes Unterstein (unterstein@me.com)
 */
object UserLogic {
  def createUser(email: String, password: String): User = {
    if (Neo4jProvider.get().userRepository.findByEmail(email) == null) {
      val user: User = new User
      user.email = email
      user.password = HashHelper.saltedHash(password)
      user
    } else {
      null
    }
  }

  def login(email: String, password: String): User = {
    val user = Neo4jProvider.get().userRepository.findByEmail(email)
    if (HashHelper.saltedHashValid(user.password, password) == true) {
      user
    } else {
      null
    }
  }
}
