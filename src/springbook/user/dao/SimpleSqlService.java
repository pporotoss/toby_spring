package springbook.user.dao;

import java.util.Map;

import springbook.user.sqlservice.SqlRetrievalFailureException;
import springbook.user.sqlservice.SqlService;

public class SimpleSqlService implements SqlService{
	
	private Map<String, String> sqlMap;
	public void setSqlMap(Map<String, String> sqlMap) {
		this.sqlMap = sqlMap;
	}
	
	
	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		String sql = sqlMap.get(key);
		if (sql == null) {
			throw new SqlRetrievalFailureException(key+"에 대한 SQL을 찾을 수 없습니다.");
		}
		return sql;
	}

}
