package be.rubenpieters.containers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.HostPortWaitStrategy;
import org.testcontainers.containers.wait.Wait;

/**
 * Created by ruben on 30/10/16.
 */
public class PostgresContainer extends GenericContainer<PostgresContainer> {
    public static Integer postgresJdbcPort = 5432;
    public static String postgresUser = "root";
    public static String postgresPw = "root";
    public static String postgresDb = "docker";

    public PostgresContainer() {
        super("postgres:9.6");

        withExposedPorts(postgresJdbcPort)
                .withEnv("POSTGRES_PASSWORD",postgresUser)
                .withEnv("POSTGRES_USER",postgresPw)
                .withEnv("POSTGRES_DB", postgresDb)
                .waitingFor(Wait.forListeningPort())
        ;
    }

    @Override
    public void start() {
        super.start();
    }
}
