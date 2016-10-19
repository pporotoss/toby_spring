package springbook.user.dao;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.zip.Checksum;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.domain.Level;
import springbook.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-applicationContext.xml")
public class UserDaoTest {
	@Autowired
	private UserDao dao;
	
	@Autowired
	private DataSource dataSource;
	
	private User user1;
	private User user2;
	private User user3;
	
	
	@Before
	public void setup() {
		user1 = new User("gyumee", "박성철", "springno1", Level.BASIC, 1, 0);
		user2 = new User("leegw700", "이길원", "springno2", Level.SILVER, 55, 10);
		user3 = new User("bumjin", "박범진", "springno3", Level.GOLD, 100, 40);
	}
	
	@Test(expected=DuplicateKeyException.class)
	public void duplicateKey() {
		dao.deleteAll();
		
		dao.add(user1);
		dao.add(user1);
	}
	
	@Test
	public void addAndGet() throws SQLException {
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
			
		User userget1 = dao.get(user1.getId());
		checkSameUser(userget1, user1);
		
		User userget2 = dao.get(user2.getId());
		checkSameUser(userget2, user2);
	}
	
	@Test
	public void count() throws SQLException {
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		assertThat(dao.getCount(), is(1));
		
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		dao.add(user3);
		assertThat(dao.getCount(), is(3));
	}
	
	@Test(expected=EmptyResultDataAccessException.class)
	public void getUserFailure() throws SQLException {
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.get("unknown_id");
	}
	
	@Test
	public void getAll() throws SQLException {
		dao.deleteAll();
		
		List<User> users0 = dao.getAll();
		assertThat(users0.size(), is(0));	// 데이터가 없을경우 0이 리턴.
		
		dao.add(user1);
		List<User> users1 = dao.getAll();
		assertThat(users1.size(), is(1));
		checkSameUser(user1, users1.get(0));
		
		dao.add(user2);
		List<User> users2 = dao.getAll();
		assertThat(users2.size(), is(2));
		checkSameUser(user1, users2.get(0));
		checkSameUser(user2, users2.get(1));
		
		dao.add(user3);
		List<User> users3 = dao.getAll();
		assertThat(users3.size(), is(3));
		checkSameUser(user3, users3.get(0));	// id순서대로 정렬. id가 문자열로 삽입되어있음.
		checkSameUser(user1, users3.get(1));
		checkSameUser(user2, users3.get(2));
	}
	
	@Test
	public void update() {
		dao.deleteAll();
		
		dao.add(user1);
		dao.add(user2);
		
		user1.setName("오민규");
		user1.setPassword("srpingno6");
		user1.setLevel(Level.GOLD);
		user1.setLogin(1000);
		user1.setRecommend(999);
		dao.update(user1);
		
		User user1update = dao.get(user1.getId());
		checkSameUser(user1, user1update);
		User user2same = dao.get(user2.getId());
		checkSameUser(user2, user2same);
	}
	
	
	/*@Test
	public void  sqlExceptionTraslate() {
		dao.deleteAll();
		
		try {
			dao.add(user1);
			dao.add(user1);
		} catch (DuplicateKeyException e) {
			SQLException sqlException = (SQLException) e.getRootCause();
			SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(dataSource);
			
			DataAccessException transEx = set.translate(null, null, sqlException);
			
			assertThat(transEx, is(instanceOf(DuplicateKeyException.class)));
		}
	}*/
	
	private void checkSameUser(User addUser, User getUser) {
		assertThat(addUser.getId(), is(getUser.getId()));
		assertThat(addUser.getName(), is(getUser.getName()));
		assertThat(addUser.getPassword(), is(getUser.getPassword()));
		assertThat(addUser.getLevel(), is(getUser.getLevel()));
		assertThat(addUser.getLogin(), is(getUser.getLogin()));
		assertThat(addUser.getRecommend(), is(getUser.getRecommend()));
	}
	
}