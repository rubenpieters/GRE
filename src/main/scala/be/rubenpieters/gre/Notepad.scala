package be.rubenpieters.gre


import cats.free.Free
import cats.free.Free.liftF
import cats.data.State
import cats.arrow.FunctionK
import cats.{Id, ~>}

/**
  * Created by ruben on 9/09/2016.
  */
object Notepad extends App {
  import PropertyOperations._

  val prog = for {
    _ <- setProperty("a", 1)
    _ <- setProperty("b", 2)
  } yield ()


  val entity = EntityImpl()
  prog.foldMap(entity)
}

case class EntityImpl(properties: Properties[Long] = Map()) extends Entity {

}

trait Entity extends (PropertyOperationA ~> Id) {
  def properties: Properties[Long]

  def apply[A](fa: PropertyOperationA[A]) =
    fa match {
      case SetProperty(key, value) => properties + (key -> value)
      case GetProperty(key) => properties.get(key)
      case DeleteProperty(key) => properties
    }
}

sealed trait PropertyOperationA[A]
case class SetProperty[T](propertyName: String, value: T) extends PropertyOperationA[Unit]
case class GetProperty[T](propertyName: String) extends PropertyOperationA[Option[T]]
case class DeleteProperty(propertyName: String) extends PropertyOperationA[Unit]

object PropertyOperations {
  type PropertyOperation[A] = Free[PropertyOperationA, A]

  def setProperty[T](propertyName: String, value: T): PropertyOperation[Unit] = {
    liftF[PropertyOperationA, Unit](SetProperty(propertyName, value))
  }

  def getProperty[T](propertyName: String): PropertyOperation[Option[T]] = {
    liftF[PropertyOperationA, Option[T]](GetProperty(propertyName))
  }

  def deleteProperty(propertyName: String): PropertyOperation[Unit] = {
    liftF[PropertyOperationA, Unit](DeleteProperty(propertyName))
  }

  def updateProperty[T](propertyName: String, f: T => T): PropertyOperation[Unit] = {
    for {
      vMaybe <- getProperty[T](propertyName)
      _ <- vMaybe.map(v => setProperty[T](propertyName, f(v))).getOrElse(Free.pure())
    } yield ()
  }

  type PropertyOperationState[A] = State[Properties[Any], A]
  val pureCompiler: PropertyOperationA ~> PropertyOperationState = new (PropertyOperationA ~> PropertyOperationState) {
    def apply[A](fa: PropertyOperationA[A]): PropertyOperationState[A] =
      fa match {
        case SetProperty(key, value) => State.modify(_.updated(key, value))
        case GetProperty(key) => State.inspect(_.get(key).map(_.asInstanceOf[A]))
        case DeleteProperty(key) => State.modify(_ - key)
      }
  }
}