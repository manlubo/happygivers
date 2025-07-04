package mapper;


import java.util.List;

import domain.Board;
import domain.dto.Criteria;

public interface BoardMapper {
	void insert(Board board);
	void delete(Long bno);
	void update(Board board);
	
	
	List<Board> list(Criteria cri);
	long getCount(Criteria cri);
	
	
	Board selectOne(Long bno);

	// 메인 - 마감일이 얼마 남지 않고, 목표 달성전인 기부 게시글  
	Board selectOneDeadline();
	
	// 메인 - 신규 기부 게시글
	List<Board> listNew(); 
	
}
