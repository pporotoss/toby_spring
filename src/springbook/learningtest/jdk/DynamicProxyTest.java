package springbook.learningtest.jdk;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Proxy;

import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

public class DynamicProxyTest {
	
	@Test
	public void simpleProxy() {
		Hello hello = new HelloTarget();
		assertThat(hello.sayHello("Toby"), is("Hello Toby"));
		assertThat(hello.sayHi("Toby"), is("Hi Toby"));
		assertThat(hello.sayThankYou("Toby"), is("Thank You Toby"));
		
		Hello proxiedHello = new HelloUppercase(hello);
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
	}
	
	@Test
	public void dynamicProxy() {
		
		Hello dynamicProxiedHello = (Hello)Proxy.newProxyInstance(
				getClass().getClassLoader()
				, new Class[] {Hello.class}
				, new UppercaseHandler(new HelloTarget())
		);
		
		assertThat(dynamicProxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(dynamicProxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(dynamicProxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
	}
	
	@Test
	public void proxyFactoryBean() {	// 스프링의 ProxyFactoryBean을 이용하여 생성하는 다이나믹 프록시 테스트.
		
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());	// 부가기능을 적용해줄 타겟설정.
		pfBean.addAdvice(new UppercaseAdvice());	// 타겟클래스에 부가기능을 적용시키는 클래스를 설정.
		
		Hello proxiedHello = (Hello) pfBean.getObject();	// 팩토리 빈으로부터 생성된 다이나믹 프록시 객체를 가져온다.
		
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
	}
	
	@Test
	public void pointcutAdvisor() {
		
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());	// 부가기능을 적용해줄 타겟설정.
		
		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();	// 메서드 이름을 비교하여 적용 대상을 선정해주는 클래스.
		pointcut.setMappedName("sayH*");	//  적용할 메서드 이름 패턴 설정.
		
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));	// 포인트컷과 어드바이스를 바탕으로 어드바이저 객체를 생성하여 적용.
		
		Hello proxiedHello = (Hello) pfBean.getObject();	// 팩토리 빈으로부터 생성된 다이나믹 프록시 객체를 가져온다.
		
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is(not("THANK YOU TOBY")));	// sayThankYou() 메서드는 포인트컷과 일치하지 않기 때문에 어드바이스가 적용되지 않았다.
	}
	
	@Test
	public void classNamePointcutAdvisor() {
		// 포인트컷 준비!!
		NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
			@Override
			public ClassFilter getClassFilter() {	// 원래의 NameMatchMethodPointcut의 클래스필터는 모든 클래스를 다 받아주지만,
																// getClassFilter()메서드를 오버라이드 하여서 이름이 HelloT로 시작하는 클래스만을 선정하는 필터로 변경.
				return new ClassFilter() {
					@Override
					public boolean matches(Class<?> clazz) {
						return clazz.getSimpleName().startsWith("HelloT");
					}
				};
			}
		};
		
		classMethodPointcut.setMappedName("sayH*");
		
		// 테스트
		checkAdviced(new HelloTarget(), classMethodPointcut, true);
		
		class HelloWorld extends HelloTarget {};
		checkAdviced(new HelloWorld(), classMethodPointcut, false);
		
		class HelloToby extends HelloTarget {};
		checkAdviced(new HelloToby(), classMethodPointcut, true);
		
	}// test

	private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(target);
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
		Hello proxiedHello = (Hello) pfBean.getObject();
		
		if (adviced) {	// 포인트컷 적용 대상이면,
			assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
			assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
			assertThat(proxiedHello.sayThankYou("Toby"), is(not("THANK YOU TOBY")));
		} else {
			assertThat(proxiedHello.sayHello("Toby"), is("Hello Toby"));
			assertThat(proxiedHello.sayHi("Toby"), is("Hi Toby"));
			assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
		}
		
	}
	
	
	
}// class
