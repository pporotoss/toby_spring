package springbook.user.service;

import java.util.List;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

public class UserService {
	public static final int MIN_LOGIN_COUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;
	
	
	private MailSender mailSender;
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	private PlatformTransactionManager transactionManager;
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	UserDao userDao;
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	
	
	public void upgradeLevels() throws Exception {
		
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());	// 생성한 트랜젝션 매니저로부터 트랜잭션 가져오기.
		try {
			List<User> users = userDao.getAll();
			for(User user : users) {
				if (canUpgradeLevel(user)) {
					upgradeLevel(user);
				}
			}// for
			transactionManager.commit(status);
		} catch (Exception e) {
			transactionManager.rollback(status);
			throw e;
		}
	}

	protected void upgradeLevel(User user) {
		user.upgreadLevel();
		userDao.update(user);
		sendUpgradeEMail(user);
	}

	private void sendUpgradeEMail(User user) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmail());
		mailMessage.setFrom("useradmin@ksug.org");
		mailMessage.setSubject("Upgrade 안내");
		mailMessage.setText("사용자님의 등급이 "+user.getLevel().name());
		
		mailSender.send(mailMessage);
	}

	private boolean canUpgradeLevel(User user) {
		Level currentLevel = user.getLevel();
		switch(currentLevel) {
			case BASIC:
				return (user.getLogin() >= MIN_LOGIN_COUNT_FOR_SILVER);
			case SILVER:
				return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
			case GOLD:
				return false;
			default :
				throw new IllegalArgumentException("Unkwon Level: "+currentLevel);
		}
	}

	public void add(User user) {
		if (user.getLevel() == null) {
			user.setLevel(Level.BASIC);
		}
		userDao.add(user);
	}
	
}
