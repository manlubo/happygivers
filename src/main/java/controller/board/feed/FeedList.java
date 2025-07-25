package controller.board.feed;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import domain.Board;
import domain.dto.Criteria;
import domain.dto.PageDto;
import domain.en.Ctype;
import domain.en.Status;
import lombok.extern.slf4j.Slf4j;
import service.BoardService;
import util.ParamUtil;

@Slf4j
@WebServlet("/feed/list")
public class FeedList extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		BoardService boardService = new BoardService();
		Criteria cri = ParamUtil.get(req, Criteria.class);
		if (cri == null) {
		    cri = new Criteria();
		}
		cri.setStatus(Status.ACTIVE);
		cri.setCtype(Ctype.FEED);
		List<Board> feeds = boardService.list(cri);

		req.setAttribute("pageDto", new PageDto(cri, boardService.getCount(cri)));
		req.setAttribute("feeds", feeds);
		log.info("{}", cri);
		log.info("{}", req.getAttribute("pageDto"));
		req.getRequestDispatcher("/WEB-INF/views/board/feed/list.jsp").forward(req, resp);
	}
	
}
