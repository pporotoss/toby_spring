package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

/* Connection을 얻어오고 PreparedStatement를 실행하고 해당 자원을 반납하는 부분은 변하지 않기 때문에 해당 부분을 수행한다. 
 * Context 역할(고정된 역할)을 수행하는 클래스. 
 * 변하는 내용(Strategy)은 Client가 사용시에 주입받는다.
 * 
 * */
public class JdbcContext {
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;
				
		try {
			con = dataSource.getConnection();
			pstmt = stmt.makePreparedStatement(con);	// 변하는 부분(Strategy)을 주입받아 수행.
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw e;
		} finally {
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
	
	public void excuteSql(final String query) throws SQLException {	// 쿼리문만 수행할때, 쿼리문만 파라미터로 입력받아 실행.
		workWithStatementStrategy(new StatementStrategy() {
			@Override
			public PreparedStatement makePreparedStatement(Connection con) throws SQLException {
				return con.prepareStatement(query);
			}
		});
	}
	
	
}
