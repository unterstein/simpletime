package logic

import java.lang.reflect.Type

import com.google.gson._
import play.api.data.Form

import scala.collection.JavaConverters._

/**
 * @author Johannes Unterstein (unterstein@me.com)
 */
object JsonHelper {

  val gson = new GsonBuilder()
    .registerTypeAdapter(classOf[List[Any]], new ListSerializer())
    .registerTypeAdapter(classOf[Form[Any]], new FormSerializer())
    .create()


  def toJson(obj: Object): String = gson.toJson(obj)

  def fromJson[T](json: String, clazz: Class[T]): T = gson.fromJson(json, clazz)

  class ListSerializer extends JsonSerializer[List[Any]] {

    override def serialize(src: List[Any], typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
      gson.toJsonTree(src.asJava)
    }
  }

  class FormSerializer extends JsonSerializer[Form[Any]] {

    override def serialize(src: Form[Any], typeOfSrc: Type, context: JsonSerializationContext): JsonElement = {
      if (src.value.isDefined) {
        gson.toJsonTree(src.get)
      } else {
        // TODO, maybe create new form without constraints?
        gson.toJsonTree(src.data.asJava)
      }
    }
  }

}
