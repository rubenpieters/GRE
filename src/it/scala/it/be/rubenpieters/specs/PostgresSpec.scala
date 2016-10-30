package it.be.rubenpieters.specs

import be.rubenpieters.containers.PostgresContainer
import org.scalatest.{BeforeAndAfterAll, Suite}

/**
  * Created by ruben on 30/10/16.
  */
trait PostgresSpec extends BeforeAndAfterAll { self: Suite =>
  val postgresDockerContainer = new PostgresContainer

  lazy val postgresIp = postgresDockerContainer.getContainerIpAddress
  lazy val postgresPort = postgresDockerContainer.getMappedPort(PostgresContainer.postgresJdbcPort)
  lazy val postgresJdbcConnStr = s"jdbc:postgresql://$postgresIp:$postgresPort/${PostgresContainer.postgresDb}"

  override def beforeAll(): Unit = {
    postgresDockerContainer.start()
  }

  override def afterAll(): Unit = {
    postgresDockerContainer.stop()
  }
}
