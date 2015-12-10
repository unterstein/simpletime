package logic

import java.security.{MessageDigest, SecureRandom}
import java.util.UUID

import org.apache.commons.codec.binary.Hex

/**
 * @author Johannes Unterstein (unterstein@me.com)
 */
object HashHelper {

  private val random = new SecureRandom()

  def uuid(): String = UUID.randomUUID().toString.replace("-", "")

  def saltedHash(in: String): String = {
    val saltAsArray: Array[Byte] = new Array[Byte](5)
    random.nextBytes(saltAsArray)

    val salt: String = new String(Hex.encodeHex(saltAsArray))
    val digest: String = sha(salt + in)
    salt + "-" + digest
  }

  def sha(in: String): String = {
    try {
      val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
      val digestAsArr: Array[Byte] = digest.digest(in.getBytes)
      new String(Hex.encodeHex(digestAsArr))
    } catch {
      case o_O: Exception => {
        throw new RuntimeException(o_O)
      }
    }
  }
}
