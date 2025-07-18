package controller.board;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import domain.Board;
import domain.Member;
import domain.dto.Criteria;

import lombok.extern.slf4j.Slf4j;

import service.BoardService;
import service.DonateService;
import service.MemberService;
import util.AlertUtil;
import util.ParamUtil;


@Slf4j
@WebServlet("/board/view")
public class View extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if(req.getParameter("bno") == null) {
			AlertUtil.alert("잘못된 접근입니다.", "/board/list", req, resp);
			return;
		}
		
		Criteria cri = Criteria.init(req);
		BoardService service = new BoardService();
		
		Board board = service.findByBno(ParamUtil.get(req, Board.class).getBno());

		board.setRound(service.findRound(board.getDrno()));
		board.setCname(service.findCname(board.getCno()));
		board.setName(service.findName(board.getMno()));
		int replyCount = service.getReplyCount(board.getBno());
		Member member = (Member) req.getSession(false).getAttribute("member");
		long myamount = 0;
		if(member != null) {
			DonateService donateService = new DonateService();
			myamount = donateService.findMyAmount(board.getDrno(), member.getMno());
			board.setLiked(service.checkBoardLiked(board.getBno(), member.getMno()));
		}
		List<Board> orgDonates = service.findMnoDonateList(board.getMno());
		Member owner = new MemberService().findByMno(board.getMno());

		req.setAttribute("owner", owner);
		req.setAttribute("orgDonates", orgDonates);
		req.setAttribute("myamount", myamount);
		req.setAttribute("cri", cri);
		req.setAttribute("board", board);
		req.setAttribute("replyCount", replyCount);
		req.getRequestDispatcher("/WEB-INF/views/board/view.jsp").forward(req, resp);
	}
}
