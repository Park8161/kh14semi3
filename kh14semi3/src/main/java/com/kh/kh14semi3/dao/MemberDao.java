package com.kh.kh14semi3.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.kh.kh14semi3.dto.MemberDto;
import com.kh.kh14semi3.mapper.MemberMapper;
import com.kh.kh14semi3.mapper.MemberTakeOffMapper;
import com.kh.kh14semi3.vo.MemberTakeOffVO;
import com.kh.kh14semi3.vo.PageVO;


@Service
public class MemberDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private MemberTakeOffMapper memberTakeOffMapper;

	//페이징 관련 메소드
	public List<MemberTakeOffVO> selectListByPaging(PageVO pageVO) {
        if(pageVO.isSearch()) { // 검색
            String sql = "select * from ("
                    + "select rownum rn, TMP.* from ("
                    + "select "
                    + "M.*, T.takeOff_no, T.takeOff_memo, T.takeOff_time, "
                    + "T.takeOff_target, nvl(T.takeOff_type, '재학') takeOff_type "
                    + "from member M "
                    + "left outer join takeOff_latest T "
                    + "on M.member_id = T.takeOff_target "
                    + "where instr("+pageVO.getColumn()+", ?) >0 "
                    + "order by "+pageVO.getColumn()+" asc, M.member_id asc"
                    + ") TMP"
                    + ") where rn between ? and ?";
                    Object[] data = {pageVO.getKeyword(), 
                        pageVO.getBeginRow(), pageVO.getEndRow()};
                    return jdbcTemplate.query(sql, memberTakeOffMapper, data);
        }
        else { // 목록
            String sql = "select * from ("
                    + "select rownum rn, TMP.* from ("
                    + "select "
                    + "M.*, T.takeOff_no, T.takeOff_memo, T.takeOff_time, "
                    + "T.takeOff_target, nvl(T.takeOff_type, '재학') takeOff_type "
                    + "from member M "
                    + "left outer join takeOff_latest T "
                    + "on M.member_id = T.takeOff_target "
                    + "order by M.member_id asc"
                    + ") TMP"
                    + ") where rn between ? and ?";
                    Object[] data = {pageVO.getBeginRow(), pageVO.getEndRow()};
                    return jdbcTemplate.query(sql, memberTakeOffMapper, data);
        }
    }

		public int countWithPaging(PageVO pageVO) {
			if(pageVO.isSearch()) {
				String sql = "select count(*) from member where instr("+pageVO.getColumn()+", ?) > 0";
				Object[] data = {pageVO.getKeyword()}; 
				return jdbcTemplate.queryForObject(sql, int.class, data);
			}
			else {
				String sql = "select count(*) from registration";
				return jdbcTemplate.queryForObject(sql, int.class);
			}
		}
		
		

}
