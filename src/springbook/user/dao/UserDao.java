package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;

import springbook.user.domain.User;

public class UserDao {
	
	private DataSource dataSource;
	
	public void setDataSource(DataSource datasource) {
		this.dataSource = datasource;
	}
	
	public void add(User user) throws SQLException {
		Connection con = dataSource.getConnection();
		
		String sql = "insert into users(id, name, password) values(?, ?, ?)";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, user.getId());
		pstmt.setString(2, user.getName());
		pstmt.setString(3, user.getPassword());
		
		pstmt.executeUpdate();
		
		pstmt.close();
		con.close();
	}
	
	public User get(String id) throws SQLException {
		Connection con = dataSource.getConnection();
		
		String sql = "select * from users where id=?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, id);
		
		ResultSet rs = pstmt.executeQuery();
		User user = null;
		if(rs.next()) {
			user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
		}
		rs.close();
		pstmt.close();
		con.close();
		
		if (user == null) throw new EmptyResultDataAccessException(1);
		return user;
	}
	
	public void deleteAll() throws SQLException {
		Connection con = dataSource.getConnection();
		
		String sql = "delete from users";
		PreparedStatement pstmt = con.prepareStatement(sql);
		
		pstmt.executeUpdate();
		
		pstmt.close();
		con.close();
	}
	
	public int getCount() throws SQLException {
		Connection con = dataSource.getConnection();
		String sql = "select count(*) from users";
		PreparedStatement pstmt = con.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		rs.next();
		int count = rs.getInt(1);
		
		rs.close();
		pstmt.close();
		con.close();
		
		return count;
	}
	
}
