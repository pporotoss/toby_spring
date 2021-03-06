package springbook.user.service;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static springbook.user.service.UserServiceImpl.MIN_LOGIN_COUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import springbook.learningtest.jdk.HelloTarget;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)	// 스프링의 기능을 사용하여 테스트하려면 선언해줘야 한다.
@ContextConfiguration(locations="/test-applicationContext.xml")
@Transactional(propagation=Propagation.REQUIRED)
//@Rollback
public class UserServiceTest {
	@Autowired
	UserService userService;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	PlatformTransactionManager transactionManager;
	
	@Autowired
	MailSender mailSender;
	
	@Autowired
	ApplicationContext context;
	
	@Autowired
	@Qualifier("testUserService")
	UserService testUserService;
	
	List<User> users;
	
	@Before
	public void setup() {
		users = Arrays.asList(
					new User("bumjin", "박범진", "p1", "user1@ksug.org", Level.BASIC, MIN_LOGIN_COUNT_FOR_SILVER-1, 0),
					new User("joytouch", "강명성", "p2", "user2@ksug.org", Level.BASIC, MIN_LOGIN_COUNT_FOR_SILVER, 0),
					new User("erwins", "신승한", "p3", "user3@ksug.org", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1),
					new User("madnite1", "이상호", "p4", "user4@ksug.org", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD),
					new User("green", "오민규", "p5", "user5@ksug.org", Level.GOLD, 100, Integer.MAX_VALUE)
				);
	}
	
	@Test
	public void bean() {
		assertThat(userService, is(notNullValue()));	// 제대로 주입 되었는지 테스트.
		assertThat(userDao, is(notNullValue()));
	}
	
	@Test
	//@DirtiesContext	// 컨텍스트의 DI 설정을 변경하는 테스트임을 선언. 
	public void upgradeLevels() throws Exception {
		UserServiceImpl userServiceImpl = new UserServiceImpl();	// 스프링 빈 객체를 사용 않기 위해 새로 생성.
		
		UserDao mockUserDao = mock(UserDao.class);	// UserDao의 Mock 객체 생성.
		when(mockUserDao.getAll()).thenReturn(users);	// mockUserDao.getAll() 메서드를 호출하면, users 리스트를 반환해줘라.
																			// upgradeLevels() 에서 getAll() 메서드 호출하고 있기때문에.
		userServiceImpl.setUserDao(mockUserDao);
		
		MailSender mockMailSender = mock(MailSender.class);	// MailSender의 Mock객체 생성.
		userServiceImpl.setMailSender(mockMailSender);	// 목 오브젝트를 주입. 
				
		userServiceImpl.upgradeLevels();	// DB와의 접속을 않기 때문에 직접 구현 클래스의 매서드를 사용해줌.
		
		verify(mockUserDao, times(2)).update(any(User.class));	// User객체의 파라미터 내용은 무시하고(any), UserDao의 update() 메서드가 2번 실행되었는지(times) 검증. 
		//verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao).update(users.get(1));		// users의 1번째 객체를 파라미터로 UserDao의 update() 메서드를 실행했는지 검증.
		assertThat(users.get(1).getLevel(), is(Level.SILVER));
		verify(mockUserDao).update(users.get(3));
		assertThat(users.get(3).getLevel(), is(Level.GOLD));
		
		ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);	// SimpleMailMessage객체로 저장되는 모든 값을 저장해두기 위한 클래스.
		verify(mockMailSender, times(2)).send(mailMessageArg.capture());
		List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();	// SimpleMailMessage 객체가 전송한 모든 값을 가져온다.
		assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
		assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));
	}
	
	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
		assertThat(updated.getId(), is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel));
	}

	@Test
	public void add() {
		userDao.deleteAll();
		
		User userWithLevel = users.get(4);
		User userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null);
		
		userService.add(userWithLevel);
		userService.add(userWithoutLevel);
		
		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());
		
		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
		assertThat(userWithoutLevelRead.getLevel(), is(Level.BASIC));
	}
	
	@Test(expected=RuntimeException.class)
	//@DirtiesContext
	public void upgradeAllOrNothing() throws Exception {
		//TestUserServiceImpl testUserService = new TestUserServiceImpl();
		//testUserService.setUserDao(userDao);
		//testUserService.setMailSender(mailSender);
		
		//TxProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", TxProxyFactoryBean.class);	// JDK 제공 프록시 생성.
		//ProxyFactoryBean txProxyFactoryBean = context.getBean("&proxyUserService", ProxyFactoryBean.class);	// 스프링 제공 프록시 생성.
		//txProxyFactoryBean.setTarget(testUserService);
		//UserService txUserService = (UserService) txProxyFactoryBean.getObject();
		
		//UserServiceTx txUserService = new UserServiceTx();
		//txUserService.setTransactionManager(transactionManager);
		//txUserService.setUserService(testUserService);	// 테스트를 위해서는 TestUserService의 메서드를 사용해야 하므로 TestUserService 객체를 주입해준다.
				
		testUserService.deleteAll();
		assertThat(userService.getAll().size(), is(0));
		for (User user : users) {
			testUserService.add(user);
		}
		testUserService.upgradeLevels();	// TestUserService를 주입해 주었기때문에 TestUserService의 메서드를 수행하게 된다.
		//fail("TestUserServiceException expected");
		/*try {
		} catch (TestUserServiceException e) {
			System.out.println(e.getMessage());
		} finally {
		}*/
		checkLevelUpgraded(users.get(1), false);
		
	}
	
	@Test(expected=RuntimeException.class)
	public void proxyUpgradeAllOrNothing() {
		//TestUserServiceImpl testUserService = new TestUserServiceImpl(users.get(3).getId());
		//testUserService.setUserDao(userDao);
		//testUserService.setMailSender(mailSender);
		
		TransactionHandler txHandler = new TransactionHandler();	// 트랜잭션 적용을 위해 InvocationHandler를 직접 구현한 클래스.
		txHandler.setTarget(testUserService);
		txHandler.setTransactionManager(transactionManager);
		txHandler.setPattern("upgradeLevels");
		
		UserService txUserService = (UserService)Proxy.newProxyInstance(
				getClass().getClassLoader()	// 프록시 클래스 로딩에 필요한 클래스 로더
				, new Class[] { UserService.class }	// 프록시 객체를 구현할 클래스
				, txHandler	// 부가기능과 위임코드를 담은 InvocationHandler
		);
		
		userDao.deleteAll();
		for (User user : users) {
			userDao.add(user);
		}
		txUserService.upgradeLevels();	// TestUserService를 주입해 주었기때문에 TestUserService의 메서드를 수행하게 된다.
		fail("TestUserServiceException expected");
		/*try {
		} catch (TestUserServiceException e) {	// try-catch에서 먼저 롤백해버려서 클래스에 선언한 @Transactional과 충돌된다. 
			System.out.println(e.getMessage());
		}*/
		
		checkLevelUpgraded(users.get(1), false);
	}
	
	private void checkLevelUpgraded(User user, boolean upgraded) {
		User updateUser = userDao.get(user.getId());
		
		if (upgraded) {
			assertThat(updateUser.getLevel(), is(user.getLevel().nextLevel()));
		} else {
			assertThat(updateUser.getLevel(), is(user.getLevel()));
		}
	}
	
	@Test
	public void advisorAutoProxyCreator() {
		assertThat(testUserService, is(instanceOf(java.lang.reflect.Proxy.class)));
	}
	
	@Test
	public void readOnlyTransactionAttribute() {
		testUserService.getAll();
		testUserService.get("");
	}
	
	@Test
	@Transactional	// 테스트 메서드에 @Transactional 어노테이션 적용시 테스트 종료시 자동으로 롤백해준다.
	@Rollback(true)	// 테스트 메서드 종료시 @Transactional 어노테이션에 의해 강제로 롤백되지 않도록 해준다. 기본값=true
	public void transactionSync() {
		
		DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
		txDefinition.setReadOnly(true); // 읽기전용으로 트랜잭션을 정의.
		
		TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);	// 트랜잭션매니저에게 직접 트랜잭션을 얻어오면서 트랙잭션이 시작된다.
		
		try {
		
			userService.deleteAll();
			assertThat(userDao.getAll().size(), is(0));	// 지워진거 확인.
			userService.add(users.get(0));
			userService.add(users.get(1));
			assertThat(userDao.getAll().size(), is(2));	// 두명 추가된거 확인.
		
		} finally {
			
			//transactionManager.rollback(txStatus);	// 오류 여부와 상관없이 테스트 끝나면 무조껀 강제로 롤백.
			
			//assertThat(userDao.getAll().size(), is(0));	// 롤백여부 확인
		}
		
		transactionManager.commit(txStatus);
	}
	
	
	
	
	/* 테스트용 객체 생성 */
	static class MockMailSender implements MailSender {
		private List<String> requests = new ArrayList<>();
		
		public List<String> getRequests() {
			return requests;
		}
		
		@Override
		public void send(SimpleMailMessage mailMessage) throws MailException {
			requests.add(mailMessage.getTo()[0]);	// 전송요청받은 이메일 주소만 저장. 주소중에서 첫번째 주소만 저장함.
		}

		@Override
		public void send(SimpleMailMessage... mailMessage) throws MailException {

		}
		
	}
	
	static class MockUserDao implements UserDao {
		
		private List<User> users;
		private List<User> updated = new ArrayList<>();
		
		private MockUserDao(List<User> users) {
			this.users = users;
		}
		
		public List<User> getUpdated() {
			return updated;
		}

		@Override
		public List<User> getAll() {
			return users;
		}

		@Override
		public void update(User user1) {	
			updated.add(user1); 
		}
		
		@Override
		public void add(User user) { throw new UnsupportedOperationException(); }

		@Override
		public User get(String id) { throw new UnsupportedOperationException(); }
		
		@Override
		public void deleteAll() {	throw new UnsupportedOperationException();	}

		@Override
		public int getCount() {	throw new UnsupportedOperationException();	}
		
	}
	
	static class TestUserService extends UserServiceImpl{	 
		private String id = "madnite1";	// 스프링 빈에 등록하기때문에 예외 발생시킬 아이디를 직접 입력.
		
		@Override
		protected void upgradeLevel(User user) {	// 업그레이드 레벨 메서드 수행시에만 예외를 발생시키기위해 오버라이드.
			if (user.getId().equals(this.id)) {
				throw new TestUserServiceException("예외발생!!");
			}
			super.upgradeLevel(user);
		}
		
		@Override
		public List<User> getAll() {
			int count = 0;
			for(User user : super.getAll()) {
				count++;
				System.out.println(user.getEmail());
				user.setEmail("바꿈 "+count);
				super.update(user);
				System.out.println(userDao.get(user.getId()).getEmail());
			}
			return userDao.getAll();
		}
		
		@Override
		public User get(String id) {
			userDao.deleteAll();
			return null;
		}
	}
	
	static class TestUserServiceException extends RuntimeException {

		public TestUserServiceException() {
			super();
		}
		public TestUserServiceException(String message) {
			super(message);
		}
	}
	
}
