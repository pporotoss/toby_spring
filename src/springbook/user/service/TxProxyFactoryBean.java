package springbook.user.service;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

public class TxProxyFactoryBean implements FactoryBean<Object>{

	Object target;	// 핸들러가 부가기능을 수행하고나서, 핵심 로직을 위임할 객체.
	public void setTarget(Object target) {
		this.target = target;
	}
	
	PlatformTransactionManager transactionManager;
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	String pattern;	// 트랜잭션을 적용할 메서드 패턴.
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	Class<?> serviceInterface;	// 다이나믹 프록시가 생성할 인터페이스.
	public void setServiceInterface(Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}
	
	
	@Override
	public Object getObject() throws Exception {
		TransactionHandler txHandler = new TransactionHandler();	// 부가기능만을 수행할 핸들러.
		txHandler.setTarget(target);
		txHandler.setTransactionManager(transactionManager);
		txHandler.setPattern(pattern);
		
		return Proxy.newProxyInstance(	// 부가기능을 적용시켜줄 다이나믹 프록시 객체를 생성하여 반환.
				getClass().getClassLoader(),
				new Class[] {serviceInterface},	// 프록시가 부가기능을 적용후 핵심 로직을 처리하는 인터페이스.
				txHandler	// 리플랙션을 이용하여 부가기능을 적용시켜줄 객체.
		);
	}

	@Override
	public Class<?> getObjectType() {
		return serviceInterface;
	}

	@Override
	public boolean isSingleton() {	// 객체의 싱글톤 여부가 아닌, getObject() 메서드가 항상 같은 객체를 리턴할지 여부.
		return false;
	}

}
