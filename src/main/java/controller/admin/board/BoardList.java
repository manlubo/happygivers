package controller.admin.board;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import domain.Board;
import domain.dto.Criteria;
import domain.en.Status;
import lombok.extern.slf4j.Slf4j;
import service.BoardService;
import util.ParamUtil;

@Slf4j
@WebServlet("/admin/board/list")
public class BoardList extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		BoardService service = new BoardService();
		//파라미터 받아오기
		
		Criteria cri = ParamUtil.get(req, Criteria.class);
		log.info("{}", cri);
		
		List<Board> boards = service.list(cri);
		for(Board b : boards) {
			String content = b.getContent();
			b.setContent(service.removeImgContent(content));
			b.setRound(service.findRound(b.getDrno()));
			b.setCname(service.findCname(b.getCno()));
			b.setName(service.findName(b.getMno()));
		}
		
		req.setAttribute("cno", cri.getCno());
		req.setAttribute("boards", boards);
		req.getRequestDispatcher("/WEB-INF/views/admin/board/boardlist.jsp").forward(req, resp);
	}

	
	
}
