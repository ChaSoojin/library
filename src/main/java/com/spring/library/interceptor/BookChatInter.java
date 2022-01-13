package com.spring.library.interceptor;

import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class BookChatInter implements HandlerInterceptor {

	//회원만 토론모집글 조회가 가능하도록 하기 위해서 인터셉터 처리
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		//로그인 세션정보 get
		HashMap<String, Object> sessionInfo = (HashMap<String, Object>) request.getSession().getAttribute("loginSession");

		if (sessionInfo != null) {
			return true; //BookChatController 실행
		} else {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>alert('로그인 후 이용 가능합니다.'); history.go(-1);</script>"); //뒤로가기
			out.flush();

			return false;
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
}
