package com.nrwall.flightlog.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.nrwall.flightlog.users.repository",
    entityManagerFactoryRef = "usersEntityManagerFactory",
    transactionManagerRef = "usersTransactionManager"
)
public class UsersDataSourceConfig {

  @Bean
  @Primary
  @ConfigurationProperties("spring.datasource.users")
  public DataSourceProperties usersDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @Primary
  public DataSource usersDataSource() {
    return usersDataSourceProperties().initializeDataSourceBuilder().build();
  }

  @Bean(name = "usersEntityManagerFactory")
  @Primary
  public LocalContainerEntityManagerFactoryBean usersEntityManagerFactory() {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(usersDataSource());
    em.setPackagesToScan("com.nrwall.flightlog.users.entity");
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);
    Properties props = new Properties();
    props.put("hibernate.hbm2ddl.auto", "update");
    props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
    em.setJpaProperties(props);
    return em;
  }

  @Bean
  @Primary
  public PlatformTransactionManager usersTransactionManager(
      @Qualifier("usersEntityManagerFactory") LocalContainerEntityManagerFactoryBean usersEmf) {
    JpaTransactionManager tm = new JpaTransactionManager();
    tm.setEntityManagerFactory(usersEmf.getObject());
    return tm;
  }
}
