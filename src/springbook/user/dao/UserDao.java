package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;

import springbook.user.domain.User;

public class UserDao {
	
	private JdbcContext jdbcContext;
	private DataSource dataSource;
	
	
	public void setDataSource(DataSource datasource) {
		jdbcContext = new JdbcContext();
		jdbcContext.setDataSource(datasource);

		this.dataSource = datasource;	// 안고친 애들때문에 남겨둠.
	}
	
	
	public void add(final User user) throws SQLException {	// 내부익명 클래스가 매서드의 로컬변수를 공유할때는 final 선언해주는게 좋다. ∵ 스레드 Safe 하기 위해.	
		jdbcContext.workWithStatementStrategy(new StatementStrategy() {	// 변하는 부분을 주입받아 변하지 않는 부분을 포함하여 전체를 수행. 
			@Override
			public PreparedStatement makePreparedStatement(Connection con) throws SQLException {
				String sql = "insert into users(id, name, password) values(?, ?, ?)";
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setString(1, user.getId());
				pstmt.setString(2, user.getName());
				pstmt.setString(3, user.getPassword());
				return pstmt;
			}
		});
		
	}
	
	public User get(String id) throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			
			String sql = "select * from users where id=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			
			rs = pstmt.executeQuery();
			User user = null;
			if(rs.next()) {
				user = new User();
				user.setId(rs.getString("id"));
				user.setName(rs.getString("name"));
				user.setPassword(rs.getString("password"));
			}
			if (user == null) throw new EmptyResultDataAccessException(1);
			return user;
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
		
	}
	
	public void deleteAll() throws SQLException {
		jdbcContext.excuteSql("delete from users");			
	}
	
	public int getCount() throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			String sql = "select count(*) from users";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			rs.next();
			
			return rs.getInt(1);
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if(pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
				}
			}
		}
	}
}
