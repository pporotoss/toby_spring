package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteAllStatement implements StatementStrategy{

	@Override
	public PreparedStatement makePreparedStatement(Connection con) throws SQLException {
		PreparedStatement pstmt = con.prepareStatement("delete from users");
		return pstmt;
	}

}
