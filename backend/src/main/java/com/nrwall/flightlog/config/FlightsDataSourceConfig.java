package com.nrwall.flightlog.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.nrwall.flightlog.flights.repository",
    entityManagerFactoryRef = "flightsEntityManagerFactory",
    transactionManagerRef = "flightsTransactionManager"
)
public class FlightsDataSourceConfig {

  @Bean
  @ConfigurationProperties("spring.datasource.flights")
  public DataSourceProperties flightsDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  public DataSource flightsDataSource() {
    return flightsDataSourceProperties().initializeDataSourceBuilder().build();
  }

  @Bean(name = "flightsEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean flightsEntityManagerFactory() {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(flightsDataSource());
    em.setPackagesToScan("com.nrwall.flightlog.flights.entity");
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);
    Properties props = new Properties();
    props.put("hibernate.hbm2ddl.auto", "update");
    props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
    em.setJpaProperties(props);
    return em;
  }

  @Bean
  public PlatformTransactionManager flightsTransactionManager(
      @Qualifier("flightsEntityManagerFactory") LocalContainerEntityManagerFactoryBean flightsEmf) {
    JpaTransactionManager tm = new JpaTransactionManager();
    tm.setEntityManagerFactory(flightsEmf.getObject());
    return tm;
  }
}
