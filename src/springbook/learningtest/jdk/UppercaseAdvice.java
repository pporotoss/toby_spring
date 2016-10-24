package springbook.learningtest.jdk;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class UppercaseAdvice implements MethodInterceptor{

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		
		String returnValue = (String) invocation.proceed();	// 타겟 객체의 메서드를 실행후 메서드의 반환값을 반환받는다.
		
		return returnValue.toUpperCase();	// 메서드 반환값에 부가기능 적용.
	}

}
