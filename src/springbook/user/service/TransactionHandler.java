package springbook.user.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionHandler implements InvocationHandler{
	
	private Object target;	// 부가기능을 제공해줄 객체.
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
	
	
	private Object invokeInTransaction(Method method, Object[] args) throws Throwable {
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
