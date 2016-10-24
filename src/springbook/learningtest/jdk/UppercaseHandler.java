package springbook.learningtest.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UppercaseHandler implements InvocationHandler {
	
	Object target;
	
	public UppercaseHandler(Object target) {
		this.target = target;
	}


	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		Object returnValue = method.invoke(target, args);	// target클래스의 method를 실행후 해당 method의 반환값을 반환 받는다.
		
		// 반환값이 String 클래스의 인스턴스이고, 메서드 이름이 say로 시작하면, 
		if (returnValue instanceof String && method.getName().startsWith("say")){	
			return ((String)returnValue).toUpperCase();	// 스트링형으로 형변환 해서 반환.
		}
		return returnValue;
	}

}
