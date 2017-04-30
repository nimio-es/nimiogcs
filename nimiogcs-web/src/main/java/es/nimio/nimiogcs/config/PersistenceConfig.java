package es.nimio.nimiogcs.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import es.nimio.nimiogcs.repositorios.RepositorioArtefactos;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
		basePackageClasses = { RepositorioArtefactos.class },
		repositoryImplementationPostfix = "Helper")
public class PersistenceConfig {

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean embf = new LocalContainerEntityManagerFactoryBean();
		embf.setJpaVendorAdapter(new EclipseLinkJpaVendorAdapter());
		embf.setPersistenceUnitName("nimio-gcs");
		return embf;
	}

	@Lazy
	@Bean
	public PlatformTransactionManager transactionManager() {
		JndiObjectFactoryBean jndiObject = new JndiObjectFactoryBean();
		jndiObject.setResourceRef(true);
		jndiObject.setJndiName("TransactionManager");
		jndiObject.setExpectedType(javax.transaction.TransactionManager.class);

		JtaTransactionManager tm = new JtaTransactionManager();
		tm.setTransactionManager((javax.transaction.TransactionManager)jndiObject.getObject());
		tm.setAllowCustomIsolationLevels(true);
		return tm;
	}
	
	@Lazy
	@Bean
	public DataSource dataSource() {
		final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
		dsLookup.setResourceRef(true);
		DataSource dataSource = dsLookup.getDataSource("jdbc/NIMIO_GCS");
		return dataSource;
	}

	@Lazy
	@Bean(name="executor")
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(10);
		taskExecutor.setMaxPoolSize(20);
		taskExecutor.setQueueCapacity(100);
		taskExecutor.setAwaitTerminationSeconds(600);
		taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
		
		return taskExecutor;
	}
	
	@Lazy
	@Bean(name="scheduler")
	public TaskScheduler taskScheduler() {
		
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setDaemon(true);
		scheduler.setWaitForTasksToCompleteOnShutdown(true);
		scheduler.setPoolSize(10);

		return scheduler;
	}
}
