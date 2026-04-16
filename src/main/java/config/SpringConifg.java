package config;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@Aspect
@ComponentScan(basePackages = {"aop","config","dao"})
@EnableAspectJAutoProxy
public class SpringConifg {
}
