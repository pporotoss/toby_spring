package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import springbook.user.domain.User;

public class AddStatement implements StatementStrategy{

	private User user;
	
	public AddStatement(User user) {	// Add를 수행하기 위해서는 User 객체가 필요하기 때문에 객체 생성시 생성자로 주입 받는다.
		this.user = user;
	}
	
	@Override
	public PreparedStatement makePreparedStatement(Connection con) throws SQLException {
		
		String sql = "insert into users(id, name, password) values(?, ?, ?)";
		PreparedStatement pstmt = con.prepareStatement(sql);
		
		pstmt.setString(1, user.getId());
		pstmt.setString(2, user.getName());
		pstmt.setString(3, user.getPassword());
		
		return pstmt;
	}

}
