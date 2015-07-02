package play.modules.swagger

import com.wordnik.swagger.core._
import com.wordnik.swagger.core.ApiValues._
import com.wordnik.swagger.core.util.ReflectionUtil
import play.router.PlayApiReader
import javax.ws.rs._
import javax.ws.rs.core.Context
import java.lang.annotation.Annotation

import java.lang.reflect.{ Type, Field, Modifier, Method }

import play.api.Play.current
import play.api.Logger
import play.router.RoutesCompiler.{RouteFileParser, Route}
import play.router.DynamicPart
import play.router.StaticPart
import scala.collection.JavaConversions._
import scala.io.Source



/**
 * Reads swaggers annotations, play route information and uses reflection to build API information on a given class
 */
class PlayApiSpecParser(_hostClass: Class[_], _apiVersion: String, _swaggerVersion: String, _basePath: String, _resourcePath: String)
  extends ApiSpecParserTrait {

  override def hostClass = _hostClass
  override def apiVersion = _apiVersion
  override def swaggerVersion = _swaggerVersion
  override def basePath = _basePath
  override def resourcePath = _resourcePath

  val documentation = new Documentation
  val apiEndpoint = hostClass.getAnnotation(classOf[Api])

  override def getPath(method: Method) = {
    val fullMethodName = method.getDeclaringClass.getName + "." + method.getName
    val lookup = PlayApiReader.routesCache.get(fullMethodName)
    lookup match {
      case Some(route) => route.path.parts map { part =>
        {
          part match {
            case DynamicPart(name, _) => "{" + name + "}"
            case StaticPart(name) => name
          }
        }
      } mkString
      case None => Logger error "Cannot determine Path. Nothing defined in play routes file for api method " + method.toString; this.resourcePath
    }
  }
  override protected def processOperation(method: Method, o: DocumentationOperation) = {
    val fullMethodName = method.getDeclaringClass.getCanonicalName + "." + method.getName
    val lookup = PlayApiReader.routesCache.get(fullMethodName)
    lookup match {
      case Some(route) => o.httpMethod = route.verb.value
      case None => Logger error "Could not find route " + fullMethodName
    }
    o
  }

  override def processParamAnnotations(docParam: DocumentationParameter, paramAnnotations: Array[Annotation], method: Method): Boolean = {
    var ignoreParam = false
    for (pa <- paramAnnotations) {
      pa match {
        case apiParam: ApiParam => parseApiParam(docParam, apiParam, method)
        case wsParam: QueryParam => {
          docParam.name = readString(wsParam.value, docParam.name)
          docParam.paramType = readString(TYPE_QUERY, docParam.paramType)
        }
        case wsParam: PathParam => {
          docParam.name = readString(wsParam.value, docParam.name)
          docParam.required = true
          docParam.paramType = readString(TYPE_PATH, docParam.paramType)
        }
        case wsParam: MatrixParam => {
          docParam.name = readString(wsParam.value, docParam.name)
          docParam.paramType = readString(TYPE_MATRIX, docParam.paramType)
        }
        case wsParam: HeaderParam => {
          docParam.name = readString(wsParam.value, docParam.name)
          docParam.paramType = readString(TYPE_HEADER, docParam.paramType)
        }
        case wsParam: FormParam => {
          docParam.name = readString(wsParam.value, docParam.name)
          docParam.paramType = readString(TYPE_FORM, docParam.paramType)
        }
        case wsParam: CookieParam => {
          docParam.name = readString(wsParam.value, docParam.name)
          docParam.paramType = readString(TYPE_COOKIE, docParam.paramType)
        }
        case wsParam: Context => ignoreParam = true
        case _ => Unit
      }
    }
    ignoreParam
  }

  override def parseHttpMethod(method: Method, apiOperation: ApiOperation): String = {
    if (apiOperation.httpMethod() != null && apiOperation.httpMethod().trim().length() > 0)
      apiOperation.httpMethod().trim()
    else {
      val wsGet = method.getAnnotation(classOf[javax.ws.rs.GET])
      val wsDelete = method.getAnnotation(classOf[javax.ws.rs.DELETE])
      val wsPost = method.getAnnotation(classOf[javax.ws.rs.POST])
      val wsPut = method.getAnnotation(classOf[javax.ws.rs.PUT])
      val wsHead = method.getAnnotation(classOf[javax.ws.rs.HEAD])

      if (wsGet != null) ApiMethodType.GET
      else if (wsDelete != null) ApiMethodType.DELETE
      else if (wsPost != null) ApiMethodType.POST
      else if (wsPut != null) ApiMethodType.PUT
      else if (wsHead != null) ApiMethodType.HEAD
      else null
    }
  }
}
