package springbook.learningtest.jdk;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.util.PatternMatchUtils;

public class NameMatchClassMethodPointcut extends NameMatchMethodPointcut{
	
	public void setMappedClassName(String mappedClassName) {
		
		// 기존의 모든 클래스를 다 받아주었던 클래스필터 대신에, ClassFilter 인터페이스를 구현하여 클래스 이름을 필터링 할 수 있도록 직접 정의한 클래스로, 클래스 필터를 교체해준다.
		this.setClassFilter(new SimpleClassFilter(mappedClassName));	 
	}
	
	class SimpleClassFilter implements ClassFilter {	// 클래스필터 인터페이스를 구현하여 클래스 이름패턴으로 필터링 할 수 있도록 하는 클래스.
		String mappedName;
		
		private SimpleClassFilter(String mappedName) {
			this.mappedName = mappedName;
		}
		
		@Override
		public boolean matches(Class<?> clazz) {
			return PatternMatchUtils.simpleMatch(mappedName, clazz.getSimpleName());	// 와일트카드(*)가 들어있는 문자열 비교를 지원하는 스프링의 유틸리티 메서드.
		}
		
	}
	
}
