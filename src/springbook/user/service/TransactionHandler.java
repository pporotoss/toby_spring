package springbook.user.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/* 리플랙션을 이용하여 런타임시 생성되는 부가기능만을 수행하는 다이나믹 프록시가 수행할 부가기능을 정의. 
 * 리플렉션을 이용하기 때문에 부가기능을 적용할 인터페이스의 메서드를 모두 구현하지 않아도, invoke() 메서드 하나로 모든 메서드에 부가기능을 적용가능하다. 
 * */
public class TransactionHandler implements InvocationHandler{
	
	private Object target;	// 핸들러가 부가기능을 수행하고나서, 핵심 로직을 위임할객체.
	public void setTarget(Object target) {
		this.target = target;
	}
	
	private PlatformTransactionManager transactionManager;
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	private String pattern;	// 부가기능을 적용할 메서드 이름의 패턴.
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		if (method.getName().startsWith(pattern)) {	// 메서드의 이름이 해당 패턴으로 시작하면,
			return invokeInTransaction(method, args);	// 트랜잭션 적용을 처리하여 target객체가 해당 메서드를 실행한다.
		}
		
		return method.invoke(target, args);	// 트랜잭션 적용을 하지 않은채로 target객체가 해당 메서드를 실행한다.
	}
	
	
	private Object invokeInTransaction(Method method, Object[] args) throws Throwable {	// 부가기능 적용.
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		try {
			Object returnValue = method.invoke(target, args); // 메서드를 실행할 객체인 target 객체와 해당 메서드를 실행할때 필요로하는 파라미터를 넘겨줘서 해당 메서드를 실행한다.
			transactionManager.commit(status);
			return returnValue;
		} catch (InvocationTargetException e) {
			transactionManager.rollback(status);
			throw e.getTargetException();
		}
	}
	

}
