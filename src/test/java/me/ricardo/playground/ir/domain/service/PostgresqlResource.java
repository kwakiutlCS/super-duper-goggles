package me.ricardo.playground.ir.domain.service;

import java.util.Collections;
import java.util.Map;

import org.testcontainers.containers.PostgreSQLContainer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class PostgresqlResource implements
        QuarkusTestResourceLifecycleManager {

  static PostgreSQLContainer<?> db =
      new PostgreSQLContainer<>("postgres:13") 
        .withDatabaseName("ir")
        .withUsername("ir")
        .withPassword("ir");

  @Override
  public Map<String, String> start() { 
    db.start();
    
    return Collections.singletonMap(
        "quarkus.datasource.jdbc.url", db.getJdbcUrl()
    );
  }

  @Override
  public void stop() { 
    db.stop();
  }
}