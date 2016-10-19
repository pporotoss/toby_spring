package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static springbook.user.service.UserService.MIN_LOGIN_COUNT_FOR_SILVER;
import static springbook.user.service.UserService.MIN_RECCOMEND_FOR_GOLD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)	// 스프링의 기능을 사용하여 테스트하려면 선언해줘야 한다.
@ContextConfiguration(locations="/test-applicationContext.xml")
public class UserServiceTest {
	@Autowired
	UserService userService;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	PlatformTransactionManager transactionManager;
	
	@Autowired
	MailSender mailSender;
	
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
	@DirtiesContext	// 컨텍스트의 DI 설정을 변경하는 테스트임을 선언. 
	public void upgradeLevels() throws Exception {
		userDao.deleteAll();
		
		for(User user : users) {
			userDao.add(user);
		}
		
		MockMailSender mockMailSender = new MockMailSender();
		userService.setMailSender(mockMailSender);	// 목 오브젝트를 주입. 
				
		userService.upgradeLevels();
		
		/* 레벨 업그레이드 여부 확인 */
		checkLevelUpgraded(users.get(0), false);
		checkLevelUpgraded(users.get(1), true);
		checkLevelUpgraded(users.get(2), false);
		checkLevelUpgraded(users.get(3), true);
		checkLevelUpgraded(users.get(4), false);
		
		/* 메일 발송여부 확인 */
		List<String> requests = mockMailSender.getRequests();
		assertThat(requests.size(), is(2));
		assertThat(requests.get(0), is(users.get(1).getEmail()));
		assertThat(requests.get(1), is(users.get(3).getEmail()));
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
	
	@Test
	public void upgradeAllOrNothing() throws Exception {
		UserService testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(userDao);
		testUserService.setTransactionManager(transactionManager);
		testUserService.setMailSender(mailSender);
		
		userDao.deleteAll();
		for (User user : users) {
			userDao.add(user);
		}
		try {
			testUserService.upgradeLevels();
			fail("TestUserServiceException expected");
		} catch (TestUserServiceException e) {
		}
		
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
	
	
	/* 테스트용 객체 생성 */
	class MockMailSender implements MailSender {
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
	
	class TestUserService extends UserService{
		private String id;
		
		private TestUserService(String id) {
			this.id = id;
		}
		
		@Override
		protected void upgradeLevel(User user) {
			if (user.getId().equals(this.id)) throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
	}
	
	class TestUserServiceException extends RuntimeException {
		
	}
}
