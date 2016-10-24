package springbook.learningtest.spring.pointcut;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

public class PointcutExpressionTest {
	
	@Test
	public void methodSignaturePointcut() throws NoSuchMethodException, SecurityException {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression("execution(public int springbook.learningtest.spring.pointcut.Target.minus(int,int) throws java.lang.RuntimeException)");
		
		//System.out.println(Target.class.getMethod("minus", int.class, int.class));
		
		// minus() 메서드
		assertThat(pointcut.getClassFilter().matches(Target.class) &&   
						pointcut.getMethodMatcher().matches(Target.class.getMethod("minus", int.class, int.class), null)
						, is(true));	// 포인트컷에 설정한 조건식이 클래스와 필터 모두 만족하는지 여부.
		
		// plus() 메서드
		assertThat(pointcut.getClassFilter().matches(Target.class) && 
				pointcut.getMethodMatcher().matches(Target.class.getMethod("plus", int.class, int.class), null)
				, is(false));	// 메서드 이름의 조건이 다르기 때문에 실패.
		
		// Bean.method()
		assertThat(pointcut.getClassFilter().matches(Bean.class) && 
				pointcut.getMethodMatcher().matches(Target.class.getMethod("method"), null)
				, is(false));
	}
	
	public void pointcut() throws Exception {
		tagetClassPointcutMatches("execution(* *(..))", true, true, true, true, true, true);
		tagetClassPointcutMatches("execution(* hello(..))", true, true, false, false, false, false);
		tagetClassPointcutMatches("execution(* hello())", true, false, false, false, false, false);
		tagetClassPointcutMatches("execution(* hello(String))", false, true, false, false, false, false);
		tagetClassPointcutMatches("execution(* meth*(..))", false, false, false, false, true, true);
		
		tagetClassPointcutMatches("execution(* *(int,int))", false, false, true, true, false, false);
		tagetClassPointcutMatches("execution(* *())", true, false, false, false, true, true);
		
		tagetClassPointcutMatches("execution(* springbook.learningtest.spring.pointcut.Target.*(..))", true, true, true, true, true, false);
		tagetClassPointcutMatches("execution(* springbook.learningtest.spring.pointcut.*.*(..))", true, true, true, true, true, true);
		tagetClassPointcutMatches("execution(* springbook.learningtest.spring.pointcut..*.*(..))", true, true, true, true, true, true);
		tagetClassPointcutMatches("execution(* springbook..*.*(..))", true, true, true, true, true, true);
		
		tagetClassPointcutMatches("execution(* com..*.*(..))", false, false, false, false, false, false);
		tagetClassPointcutMatches("execution(* *..Target.*(..))", true, true, true, true, true, false);
		tagetClassPointcutMatches("execution(* *..Tar*.*(..))", true, true, true, true, true, false);
		tagetClassPointcutMatches("execution(* *..*get.*(..))", true, true, true, true, true, false);
		tagetClassPointcutMatches("execution(* *..B*.*(..))", false, false, false, false, false, true);
		
		tagetClassPointcutMatches("execution(* *..TargetInterface.*(..))", true, true, true, true, false, false);

		tagetClassPointcutMatches("execution(* *(..) throws Runtime*)", false, false, false, true, false, true);

		tagetClassPointcutMatches("execution(int *(..))", false, false, true, true, false, false);
		tagetClassPointcutMatches("execution(void *(..))", true, true, false, false, true, true);
	}
	
	public void pointcutMatches(String expression, Boolean expected, Class<?> clazz, String methodName, Class<?>... args) throws Exception {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(expression);
		
		assertThat(pointcut.getClassFilter().matches(clazz) 
				   && pointcut.getMethodMatcher().matches(clazz.getMethod(methodName, args), null),
				   is(expected));
	}
	
	public void tagetClassPointcutMatches(String expression, boolean... expected) throws Exception {
		pointcutMatches(expression, expected[0], Target.class, "hello");	// Target클래스의 파라미터 없는 hello메서드와 매치되는지 여부.
		pointcutMatches(expression, expected[1], Target.class, "hello", String.class);	// Target클래스의 String파라미터를 갖는 hello메서드와 매치되는지 여부
		pointcutMatches(expression, expected[2], Target.class, "plus", int.class, int.class);	// Target클래스의 int형 매개변수를 두개 갖는 plus 매서드와 매치하는지 여부.
		pointcutMatches(expression, expected[3], Target.class, "minus", int.class, int.class);	// Target클래스의 int형 매개변수를 두개 갖는 minus 매서드와 매치하는지 여부.
		pointcutMatches(expression, expected[4], Target.class, "method");	// 아무런 매개변수를 갖지않는 Target클래스의 method 메서드와 매치하는지 여부.
		pointcutMatches(expression, expected[5], Bean.class, "method");	// 아무런 매개변수를 갖지않는 Bean클래스의 method 메서드와 매치하는지 여부.
	}
}
