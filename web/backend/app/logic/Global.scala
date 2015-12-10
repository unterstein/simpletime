package logic

import neo4jplugin.Neo4jPlugin
import java.util
import java.util.concurrent.Callable

import play.inject.ApplicationLifecycle
import play.libs.F.Promise

import play.api.{Mode, Application, GlobalSettings}
import scala.collection.JavaConversions._


/**
 * @author Johannes Unterstein (unterstein@me.com)
 */
object Global extends GlobalSettings {

  private val hooks = new util.ArrayList[Callable[Promise[Void]]]()

  private var app: Application = null

  def isDev: Boolean = app.mode == Mode.Dev

  override def onStart(app: Application): Unit = {
    super.onStart(app)
    this.app = app
    new Neo4jPlugin(new ApplicationLifecycle {
      override def addStopHook(hook: Callable[Promise[Void]]): Unit = hooks.add(hook)
    })

  }

  override def onStop(app: Application): Unit = {
    super.onStop(app)

    hooks.map(f => f.call())
  }

}
