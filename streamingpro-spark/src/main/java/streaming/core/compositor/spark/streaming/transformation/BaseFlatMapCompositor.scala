package streaming.core.compositor.spark.streaming.transformation

import java.util

import org.apache.log4j.Logger
import org.apache.spark.streaming.dstream.DStream
import serviceframework.dispatcher.{Compositor, Processor, Strategy}

import scala.collection.JavaConversions._
import scala.reflect.ClassTag

/**
  * @author bbword
  *         2016-05-02 可以在元数据中配置DStreamd的flatMap操作，flatMap的具体函数需在子类中实现
  */
abstract class BaseFlatMapCompositor[T, S: ClassTag, U: ClassTag] extends Compositor[T] {

  protected var _configParams: util.List[util.Map[Any, Any]] = _

  val logger = Logger.getLogger(classOf[BaseFlatMapCompositor[T, S, U]].getName)

  override def initialize(typeFilters: util.List[String], configParams: util.List[util.Map[Any, Any]]): Unit = {
    this._configParams = configParams
  }

  override def result(alg: util.List[Processor[T]], ref: util.List[Strategy[T]], middleResult: util.List[T], params: util.Map[Any, Any]): util.List[T] = {
    val dstream = middleResult(0).asInstanceOf[DStream[S]]
    val func = flatMap
    val newDstream = dstream.flatMap(line => func(line))
    List(newDstream.asInstanceOf[T])
  }

  def flatMap: S => Iterable[U]
}
