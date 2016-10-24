package springbook.user.service;


import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionAdvice implements MethodInterceptor{
	
	private PlatformTransactionManager transactionManager;
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
		
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {

		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		try {
			Object returnValue = invocation.proceed();	// 타겟 오브젝트에 부가기능 적용.
			transactionManager.commit(status);
			return returnValue;
		} catch (RuntimeException e) {	// 스프링의 객체는 예외를 포장하지 않고 타깃에서 보낸 그대로 예외를 전달해준다.
			transactionManager.rollback(status);
			throw e;
		}
	}

}
