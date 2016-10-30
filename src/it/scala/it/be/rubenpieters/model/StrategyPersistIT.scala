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

    val createTable: Update0 = sql"CREATE TABLE IF NOT EXISTS strategy (id integer NOT NULL, name varchar NOT NULL)".update

    def insert1(id: Int, name: String): Update0 =
      sql"insert into strategy (id, name) values ($id, $name)".update

    val insert: Update0 = insert1(1, "strat_1")

    val select: Query0[(Int, String)] = sql"select id, name from strategy".query[(Int, String)]

    val xa = DriverManagerTransactor[IOLite]("org.postgresql.Driver", postgresJdbcConnStr, PostgresContainer.postgresUser, PostgresContainer.postgresPw)

    val resultCreate = createTable.run.transact(xa)
    println(resultCreate.unsafePerformIO)

    val result = insert.run.transact(xa)
    println(result.unsafePerformIO)

    val resultSel = select.list.transact(xa)
    println(resultSel.unsafePerformIO)
  }
}
