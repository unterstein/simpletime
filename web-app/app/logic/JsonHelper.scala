package logic

import java.lang.reflect.Type

import com.google.gson._

import scala.collection.JavaConverters._

/**
 * @author Johannes Unterstein (unterstein@me.com)
 */
object JsonHelper {

  val gson = new GsonBuilder().registerTypeAdapter(classOf[List[Any]], new ListSerializer()).create()


  def toJson(obj: Object): String = gson.toJson(obj)

  def fromJson[T](json: String, clazz: Class[T]): T = gson.fromJson(json, clazz)

  class ListSerializer extends JsonSerializer[List[Any]] {

    override def serialize(src: List[Any], typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
      gson.toJsonTree(src.asJava)
    }
  }

}
