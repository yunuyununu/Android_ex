package sqlmap;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

// mybatis => sql 실행
//  SqlSessionFactory => sqlSession 실행
public class MybatisManager {
	private static SqlSessionFactory instance;
	
	private MybatisManager() { // 생성자 private 외부 접속 불가
	}
	
	public static SqlSessionFactory getInstance() {
		Reader reader = null;
		try {
			reader = Resources.getResourceAsReader("sqlmap/sqlMapConfig.xml");
													// mybatis 설정 파일
			instance = new SqlSessionFactoryBuilder().build(reader);
			// SqlSessionFactoryBuilder => SqlSessionFactory => sqlSession
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(reader!=null) reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}
}
