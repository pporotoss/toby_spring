package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface StatementStrategy {	// 변하는 내용을 인터페이스로 분리.
	
	/* SQL문 마다 변하는 내용인 PreparedStatement를 생성하는 부분을 구현하는 메서드.*/
	PreparedStatement makePreparedStatement(Connection con) throws SQLException;
}
