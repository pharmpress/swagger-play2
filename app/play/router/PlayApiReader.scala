package play.router
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.core.Documentation
import play.router.RoutesCompiler.{Rule, Route, RouteFileParser}
import play.modules.swagger.PlayApiSpecParser
import scala.io.Source

/**
 * Caches and retrieves API information for a given Swagger compatible class
 *
 * @author ayush
 * @since 10/9/11 7:13 PM
 *
 */
object PlayApiReader {
  private val endpointsCache = scala.collection.mutable.Map.empty[Class[_], Documentation]
  lazy val routesCache: Map[String, Route] = populateRoutesCache

  def read(hostClass: Class[_], apiVersion: String, swaggerVersion: String, basePath: String, apiPath: String): Documentation = {
    endpointsCache.get(hostClass) match {
      case None => val doc = new PlayApiSpecParser(hostClass, apiVersion, swaggerVersion, basePath, apiPath).parse(); endpointsCache += hostClass -> doc.clone().asInstanceOf[Documentation]; doc
      case Some(doc) => doc.clone().asInstanceOf[Documentation]
      case _ => null
    }
  }

  private def populateRoutesCache: Map[String, Route] = {
    val classLoader = this.getClass.getClassLoader
    val routesStream = classLoader.getResourceAsStream("routes")
    val routesString = Source.fromInputStream(routesStream).getLines().mkString("\n")
    val parser = new RouteFileParser
    val parsedRoutes = parser.parse(routesString)
    if(parsedRoutes.successful){
      parsedRoutes.get.collect{ case route: Route  =>
        val routeName = route.call.packageName + "." + route.call.controller + "$." + route.call.method
        routeName -> route
      } toMap
    } else Map.empty
  }
}
