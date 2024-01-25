package org.kenndar.runner;

import org.junit.jupiter.api.*;
import org.kendar.Main;
import org.kendar.jpa.HibernateSessionFactory;
import org.kendar.protocol.Sleeper;

import java.nio.file.Path;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainTest extends BasicTest {

    @BeforeAll
    public static void beforeClass() {
        beforeClassBase();

    }

    @AfterAll
    public static void afterClass() throws Exception {
        afterClassBase();
    }



    @Test
    void testMain() throws Exception {
        var runTheServer = new AtomicBoolean(true);

        var timestampForThisRun = ""+new Date().getTime();
        //RECORDING
        var args = new String[]{
                "-p","postgres",
                "-l",""+FAKE_PORT,
                "-xl",postgresContainer.getUserId(),
                "-xw",postgresContainer.getPassword(),
                "-xc",postgresContainer.getJdbcUrl(),
                "-xd", Path.of("target", "tests",timestampForThisRun).toString()
        };

        var serverThread = new Thread(()->{
            Main.execute(args,()->{
                Sleeper.sleep(100);
                return runTheServer.get();
            });
        });
        serverThread.start();
        Sleeper.sleep(1000);


        HibernateSessionFactory.initialize("org.postgresql.Driver",
                //postgresContainer.getJdbcUrl(),
                String.format("jdbc:postgresql://127.0.0.1:%d/test?ssl=false", FAKE_PORT),
                postgresContainer.getUserId(), postgresContainer.getPassword(),
                "org.hibernate.dialect.PostgreSQLDialect",
                CompanyJpa.class);

        HibernateSessionFactory.transactional(em -> {
            var lt = new CompanyJpa();
            lt.setDenomination("Test Ltd");
            lt.setAddress("TEST RD");
            lt.setAge(22);
            lt.setSalary(500.22);
            em.persist(lt);
        });
        var verifyTestRun = new AtomicBoolean(false);
        HibernateSessionFactory.query(em -> {
            var resultset = em.createQuery("SELECT denomination FROM CompanyJpa").getResultList();
            for (var rss : resultset) {
                assertEquals("Test Ltd", rss);
                verifyTestRun.set(true);
            }
        });

        runTheServer.set(false);
        Sleeper.sleep(1000);
        assertTrue(verifyTestRun.get());

        //REPLAYING
        runTheServer.set(true);
        verifyTestRun.set(false);
        var replayArgs = new String[]{
                "-p","postgres",
                "-l",""+FAKE_PORT,
                "-xl",postgresContainer.getUserId(),
                "-xw",postgresContainer.getPassword(),
                "-xc",postgresContainer.getJdbcUrl(),
                "-xd", Path.of("target", "tests",timestampForThisRun).toString(),
                "-pl"
        };

        serverThread = new Thread(()->{
            Main.execute(replayArgs,()->{
                Sleeper.sleep(100);
                return runTheServer.get();
            });
        });
        serverThread.start();
        Sleeper.sleep(1000);


        HibernateSessionFactory.initialize("org.postgresql.Driver",
                //postgresContainer.getJdbcUrl(),
                String.format("jdbc:postgresql://127.0.0.1:%d/test?ssl=false", FAKE_PORT),
                postgresContainer.getUserId(), postgresContainer.getPassword(),
                "org.hibernate.dialect.PostgreSQLDialect",
                CompanyJpa.class);

        HibernateSessionFactory.transactional(em -> {
            var lt = new CompanyJpa();
            lt.setDenomination("Test Ltd");
            lt.setAddress("TEST RD");
            lt.setAge(22);
            lt.setSalary(500.22);
            em.persist(lt);
        });
        verifyTestRun.set(false);
        HibernateSessionFactory.query(em -> {
            var resultset = em.createQuery("SELECT denomination FROM CompanyJpa").getResultList();
            for (var rss : resultset) {
                assertEquals("Test Ltd", rss);
                verifyTestRun.set(true);
            }
        });

        runTheServer.set(false);
        assertTrue(verifyTestRun.get());
    }

}
