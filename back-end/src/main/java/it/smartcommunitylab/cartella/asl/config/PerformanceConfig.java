package it.smartcommunitylab.cartella.asl.config;

import java.lang.reflect.Method;

import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.JamonPerformanceMonitorInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

//@ConditionalOnProperty(name = "debug", value="true")
//@Profile("dev")
//@Configuration
//@EnableAspectJAutoProxy(proxyTargetClass=true)
//@Aspect
public class PerformanceConfig {

	@Pointcut("execution(public * it.smartcommunitylab.cartella.asl.controller.*.*(..))")
	public void monitor() {
		System.err.println("???");
	}

//	@ConditionalOnProperty(name="dev", havingValue="true")
	@Bean
	public JamonPerformanceMonitorInterceptor jamonPerformanceMonitorInterceptor() {
		return new JamonPerformanceMonitorInterceptor();
	}

	private final StaticMethodMatcherPointcut pointcut = new StaticMethodMatcherPointcut() {
		@Override
		public boolean matches(Method method, Class<?> targetClass) {
			return targetClass.isAnnotationPresent(RestController.class);
		}
	};

	public StaticMethodMatcherPointcut getPointcut() {
		return this.pointcut;
	}

//	@ConditionalOnProperty(name="dev", havingValue="true")
	@Bean
	public Advisor performanceAdvisor() {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		 pointcut.setExpression("it.smartcommunitylab.cartella.asl.config.PerformanceConfig.monitor()");
		// pointcut.setExpression("execution(public * it.smartcommunitylab.*.*(..))");
//		pointcut.setExpression("execution(public * it.smartcommunitylab.cartella.asl.controller..*.*(..))");
		// return new DefaultPointcutAdvisor(pointcut, performanceMonitorInterceptor());
		return new DefaultPointcutAdvisor(pointcut, jamonPerformanceMonitorInterceptor());
	}

	// @Bean
	// public Advisor performanceAdvisor() {
	// AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
	// pointcut.setExpression("execution(public * it.smartcommunitylab.*.*(..))");
	// return new DefaultPointcutAdvisor(pointcut, jamonPerformanceMonitorInterceptor());
	// }

}
