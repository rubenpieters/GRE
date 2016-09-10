package be.rubenpieters.gre

import cats.free.Free
import cats.free.Free.liftF
import cats.data.State
import cats.arrow.FunctionK
import cats.{Id, ~>}

import scala.collection.mutable

/**
  * Created by ruben on 9/09/2016.
  */
object Notepad extends App {
  /*
    type Properties[A] = Map[String, A]

    sealed trait PropertyOperationA[A]
    case class SetProperty[T](propertyName: String, value: T) extends PropertyOperationA[Unit]
    case class GetProperty[T](propertyName: String) extends PropertyOperationA[Option[T]]
    case class DeleteProperty(propertyName: String) extends PropertyOperationA[Unit]

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
        vMaybe <- getProperty(propertyName)
        _ <- vMaybe.map(v => setProperty[T](propertyName, f(v))).getOrElse(Free.pure())
      } yield ()
    }

    type PropertyOperationState[A] = State[Map[String, Any], A]
    val pureCompiler: PropertyOperationA ~> PropertyOperationState = new (PropertyOperationA ~> PropertyOperationState) {
      def apply[A](fa: PropertyOperationA[A]): PropertyOperationState[A] =
        fa match {
          case SetProperty(key, value) => State.modify[Map[String, Any]](_.updated(key, value))
          case GetProperty(key) => State.inspect(_.get(key).map(_.asInstanceOf[A]))
          case DeleteProperty(key) => State.modify[Map[String, Any]](_ - key)
        }
    }

    def program: PropertyOperation[Option[Int]] =
      for {
        _ <- setProperty("wild-cats", 2)
        _ <- updateProperty[Int]("wild-cats", (_ + 12))
        _ <- setProperty("tame-cats", 5)
        n <- getProperty[Int]("wild-cats")
        _ <- deleteProperty("tame-cats")
      } yield n

    val result: (Map[String, Any], Option[Int]) = program.foldMap(pureCompiler).run(Map.empty).value
  */
}


import cats.free.Free
import cats.free.Free.liftF
import cats.data.State
import cats.arrow.FunctionK
import cats.{Id, ~>}

object CatsFree extends App {
  sealed trait KVStoreA[A]
  case class Put[T](key: String, value: T) extends KVStoreA[Unit]
  case class Get[T](key: String) extends KVStoreA[Option[T]]
  case class Delete(key: String) extends KVStoreA[Unit]

  type KVStore[A] = Free[KVStoreA, A]

  // Put returns nothing (i.e. Unit).
  def put[T](key: String, value: T): KVStore[Unit] =
    liftF[KVStoreA, Unit](Put[T](key, value))

  // Get returns a T value.
  def get[T](key: String): KVStore[Option[T]] =
    liftF[KVStoreA, Option[T]](Get[T](key))

  // Delete returns nothing (i.e. Unit).
  def delete(key: String): KVStore[Unit] =
    liftF(Delete(key))

  // Update composes get and set, and returns nothing.
  def update[T](key: String, f: T => T): KVStore[Unit] =
    for {
      vMaybe <- get[T](key)
      _ <- vMaybe.map(v => put[T](key, f(v))).getOrElse(Free.pure(()))
    } yield ()

  def program: KVStore[Option[Int]] =
    for {
      _ <- put("wild-cats", 2)
      _ <- update[Int]("wild-cats", (_ + 12))
      _ <- put("tame-cats", 5)
      n <- get[Int]("wild-cats")
      _ <- delete("tame-cats")
    } yield n

  type KVStoreState[A] = State[Map[String, Any], A]
  val pureCompiler: KVStoreA ~> KVStoreState = new (KVStoreA ~> KVStoreState) {
    def apply[A](fa: KVStoreA[A]): KVStoreState[A] =
      fa match {
        case Put(key, value) => State.modify(_.updated(key, value))
        case Get(key) =>
          State.inspect(_.get(key).map(_.asInstanceOf[A]))
        case Delete(key) => State.modify(_ - key)
      }
  }

  println(program.foldMap(pureCompiler).run(Map.empty).value)
}

