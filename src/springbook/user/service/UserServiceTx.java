package springbook.user.service;

import java.util.List;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import springbook.user.domain.User;

public class UserServiceTx implements UserService{
	
	UserService userService;
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	PlatformTransactionManager transactionManager;
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	@Override
	public void add(User user) {
		userService.add(user);
	}

	@Override
	public void upgradeLevels() {
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());	// 생성한 트랜젝션 매니저로부터 트랜잭션 가져오기.
		try {
			
			userService.upgradeLevels();
			
			transactionManager.commit(status);
		} catch (RuntimeException e) {
			transactionManager.rollback(status);
			throw e;
		}
	}

	@Override
	public User get(String id) {
		return null;
	}

	@Override
	public List<User> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(User user1) {
		// TODO Auto-generated method stub
		
	}

}
