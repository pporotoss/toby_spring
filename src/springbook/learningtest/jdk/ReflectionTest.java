package springbook.learningtest.jdk;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;

import org.junit.Test;

public class ReflectionTest {
	
	@Test
	public void invokeMethod() throws Exception {
		String name = "Spring";
		
		// length()
		assertThat(name.length(), is(6));
		
		Method lengthMethod = String.class.getMethod("length");	// String 클래스가 가진 length란 이름의 메서드를 가져온다.
		assertThat((Integer)lengthMethod.invoke(name), is(6));
		// length라는 이름의 메서드를 실행할 대상 객체(해당 메서드를 가지고 있는 객체)인 String형의 name객체를 넘겨준다. length()메서드는 파라미터를 필요로 하지 않기때문에 넘기지 않는다.
		
		// charAt()
		assertThat(name.charAt(0), is('S'));
		
		Method charAtMethod = String.class.getMethod("charAt", int.class);	// int형의 파라미터를 가지는 charAt이라는 이름의 메서드를 가져온다.
		assertThat((Character)charAtMethod.invoke(name, 0), is('S'));	
		// charAt이라는 이름의 메서드를 실행할 대상 객체인 String형의 name과, chatAt이라는 이름의 메서드가 필요로하는 파라미터를 넘겨서 실행한다.
	}
}
