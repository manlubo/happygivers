package controller.member;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import domain.Member;
import service.MemberService;

@WebServlet("/mypage/updateinfo")
public class UpdateProfileController extends HttpServlet {
	
    @Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	req.getRequestDispatcher("/WEB-INF/views/member/mypage/updateinfo.jsp").forward(req, resp);
	}

	@Override         //회원정보 수정 기능
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
        //  파라미터 가져오기
    	String name = req.getParameter("name");
        String nickname = req.getParameter("nickname");
        String tel = req.getParameter("tel");
        String location = req.getParameter("location");

        HttpSession session = req.getSession();
        Member member = (Member) session.getAttribute("member");

        // 정보 설정
        member.setName(name);
        member.setNickname(nickname);
        member.setTel(tel);
        member.setLocation(location);

        //  DB 업데이트
        new MemberService().updateProfile(member);

        //  세션 갱신
        session.setAttribute("member", member);

        //  리다이렉트
        resp.sendRedirect(req.getContextPath() + "/mypage");
    }
}
