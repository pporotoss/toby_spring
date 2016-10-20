package springbook.learningtest.jdk;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Proxy;

import org.junit.Test;

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
}
