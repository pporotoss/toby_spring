package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.sqlservice.SqlService;

public class UserDaoJdbc implements UserDao{
	
	private JdbcTemplate jdbcTemplate;
	public void setDataSource(DataSource datasource) {
		jdbcTemplate = new JdbcTemplate(datasource);
	}
	
	/*private Map<String, String> sqlMap;
	public void setSqlMap(Map<String, String> sqlMap) {
		this.sqlMap = sqlMap;
	}*/
	
	private SqlService sqlService;
	public void setSqlService(SqlService sqlService) {
		this.sqlService = sqlService;
	}
	
	private RowMapper<User> userMapper = new RowMapper<User>() {
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			user.setLevel(Level.valueOf(rs.getInt("level")));
			user.setLogin(rs.getInt("login"));
			user.setRecommend(rs.getInt("recommend"));
			user.setEmail(rs.getString("email"));
			return user;
		}
	};
	
	public void add(User user) {	// 내부익명 클래스가 매서드의 로컬변수를 공유할때는 final 선언해주는게 좋다. ∵ 스레드 Safe 하기 위해.	
		jdbcTemplate.update(sqlService.getSql("userAdd"), user.getId(), user.getName(), user.getPassword(), user.getEmail(), user.getLevel().intValue(), user.getLogin(), user.getRecommend());
	}
	
	public User get(String id) {
		return jdbcTemplate.queryForObject(sqlService.getSql("userGet"), new Object[] {id}, userMapper);		
	}
	
	public List<User> getAll() {
		return jdbcTemplate.query(sqlService.getSql("userGetAll"), userMapper);
	}
	
	public void deleteAll() {
		jdbcTemplate.update(sqlService.getSql("userDeleteAll"));
	}
	
	public int getCount() {
		return jdbcTemplate.query(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				return con.prepareStatement(sqlService.getSql("userGetCount"));
			}
		}, new ResultSetExtractor<Integer>() {
			@Override
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
				rs.next();
				return rs.getInt(1);
			}
		});
	}

	@Override
	public void update(User user1) {
		jdbcTemplate.update(sqlService.getSql("userUpdate"), user1.getName(), user1.getPassword(), user1.getEmail(), user1.getLevel().intValue(), user1.getLogin(), user1.getRecommend(), user1.getId());
	}
	
}
