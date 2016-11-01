package it.be.rubenpieters.model

import be.rubenpieters.containers.PostgresContainer
import be.rubenpieters.storage.{Games, StoredGame}
import be.rubenpieters.util.Serialization
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

//  "stored game" should "work" in {
//    // the wait strategy doesnt seem to work correctly, so ill just do it with this for now
//    Thread.sleep(6000)
//
//    val createTable: Update0 = sql"CREATE TABLE IF NOT EXISTS game (gameId integer NOT NULL, name varchar NOT NULL)".update
//
//    def insert1[I, O, S](storedGame: StoredGame[I, O, S]): Update0 =
//      sql"insert into game (gameId, name, inputType, outputType, stateType) values (${storedGame.gameId}, ${storedGame.name})".update
//
//    val insert: Update0 = insert1(Games.rps)
//
//    val select: Query0[StoredGame] = sql"select gameId, name, inputType, outputType, stateType from game".query[StoredGame]
//
//    val xa = DriverManagerTransactor[IOLite]("org.postgresql.Driver", postgresJdbcConnStr, PostgresContainer.postgresUser, PostgresContainer.postgresPw)
//
//    val resultCreate = createTable.run.transact(xa)
//    println(resultCreate.unsafePerformIO)
//
//    val result = insert.run.transact(xa)
//    println(result.unsafePerformIO)
//
//    val resultSel = select.list.transact(xa)
//    val resultTuple = resultSel.unsafePerformIO
//    println(resultTuple)
//    val storedGame = resultTuple.head
////    val func = Serialization.deserialise(resultTuple.head._3).asInstanceOf[Int => Int]
////    println(func(5))
//  }
}

