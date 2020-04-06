/*******************************************************************************
 * Copyright 2015 Fondazione Bruno Kessler
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 ******************************************************************************/
package it.smartcommunitylab.cartella.asl.config;

import java.beans.PropertyVetoException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration 
@EntityScan({"it.smartcommunitylab.cartella.asl.model"})
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"it.smartcommunitylab.cartella.asl"}, queryLookupStrategy = QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND)
public class DatabaseConfig {

	@Autowired
	private Environment env;
	
	@Autowired
	@Value("${spring.jpa.show-sql}")
	private boolean showSql;
	
	@Autowired
	@Value("${spring.jpa.generate-ddl}")
	private boolean generateDdl;
	 
	
	@Autowired
	@Value("${spring.jpa.hibernate.ddl-auto}")
	private String ddlAuto;
	
	
	@Bean
	public ComboPooledDataSource getDataSource() throws PropertyVetoException {
		ComboPooledDataSource bean = new ComboPooledDataSource();
		
		//bean.setDriverClass(env.getProperty("jdbc.driver"));
		bean.setJdbcUrl(env.getProperty("spring.datasource.url"));
		bean.setUser(env.getProperty("spring.datasource.username"));
		bean.setPassword(env.getProperty("spring.datasource.password"));
		bean.setAcquireIncrement(5);
		bean.setIdleConnectionTestPeriod(60);
		bean.setMaxPoolSize(100);
		bean.setMaxStatements(50);
		bean.setMinPoolSize(10);
		
		return bean;
	}

	@Bean(name="entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean getEntityManagerFactoryBean() throws PropertyVetoException {
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setPersistenceUnitName(env.getProperty("jdbc.name"));
		bean.setDataSource(getDataSource());
		
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabasePlatform(env.getProperty("spring.jpa.properties.hibernate.dialect"));
		adapter.setShowSql(showSql);
		adapter.setGenerateDdl(generateDdl);
		bean.setJpaVendorAdapter(adapter);
		
		bean.setJpaDialect(new IsolationSupportHibernateJpaDialect());
		
		Properties props = new Properties();
		props.setProperty("hibernate.hbm2ddl.auto", ddlAuto);
		bean.setJpaProperties(props);
		
		return bean;
	}
	
	@Bean(name="transactionManager")
	public JpaTransactionManager getTransactionManager() throws PropertyVetoException {
		JpaTransactionManager bean = new JpaTransactionManager();
		bean.setEntityManagerFactory(getEntityManagerFactoryBean().getObject());
		return bean;
	}	
	
}
