package it.be.rubenpieters.model

import be.rubenpieters.containers.PostgresContainer
import doobie.imports._
import doobie.util.iolite.IOLite
import it.be.rubenpieters.specs.PostgresSpec
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import cats.implicits._
import cats._
import cats.data._
import doobie.imports._
import doobie.util.compat.cats.monad._

/**
  * Created by ruben on 30/10/16.
  */
class StrategyPersistIT extends FlatSpec with Matchers with BeforeAndAfterAll with PostgresSpec {

  "db conn" should "work" in {
    // the wait strategy doesnt seem to work correctly, so ill just do it with this for now
    Thread.sleep(6000)

    val createTable: Update0 = sql"CREATE TABLE IF NOT EXISTS strategy (id integer NOT NULL, name varchar NOT NULL, func bytea NOT NULL)".update

    def insert1(id: Int, name: String, func: Int => Int): Update0 =
      sql"insert into strategy (id, name, func) values ($id, $name, ${Serialization.serialise(func)})".update

    val insert: Update0 = insert1(1, "strat_1", x => x + 1)

    val select: Query0[(Int, String, Array[Byte])] = sql"select id, name, func from strategy".query[(Int, String, Array[Byte])]

    val xa = DriverManagerTransactor[IOLite]("org.postgresql.Driver", postgresJdbcConnStr, PostgresContainer.postgresUser, PostgresContainer.postgresPw)

    val resultCreate = createTable.run.transact(xa)
    println(resultCreate.unsafePerformIO)

    val result = insert.run.transact(xa)
    println(result.unsafePerformIO)

    val resultSel = select.list.transact(xa)
    val resultTuple = resultSel.unsafePerformIO
    println(resultTuple)
    val func = Serialization.deserialise(resultTuple.head._3).asInstanceOf[Int => Int]
    println(func(5))
  }
}


// use this for now, should also investigate kryo serialization
// http://stackoverflow.com/a/39371571/5559685
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

object Serialization extends App {

  def serialise(value: Any): Array[Byte] = {
    val stream: ByteArrayOutputStream = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(stream)
    oos.writeObject(value)
    oos.close
    stream.toByteArray
  }

  def deserialise(bytes: Array[Byte]): Any = {
    val ois = new ObjectInputStream(new ByteArrayInputStream(bytes))
    val value = ois.readObject
    ois.close
    value
  }
}