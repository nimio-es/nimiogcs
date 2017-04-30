package es.nimio.nimiogcs.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan(basePackages = {"es.nimio.nimiogcs"})
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class RootConfig {
}
