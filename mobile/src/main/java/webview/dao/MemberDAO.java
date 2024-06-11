package webview.dao;

import org.apache.ibatis.session.SqlSession;

import sqlmap.MybatisManager;
import webview.dto.MemberDTO;

public class MemberDAO {
	public MemberDTO loginCheck(MemberDTO dto) {
		MemberDTO result = null;
		SqlSession session = null;
		try {
			session = MybatisManager.getInstance().openSession();
			result = session.selectOne("member.checkAccount",dto);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session!=null) session.close();
		}
		return result;
	}
}
