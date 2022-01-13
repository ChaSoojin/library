package com.spring.library.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.library.service.BookChatService;
import com.spring.library.service.BookReviewService;
import com.spring.library.service.UserService;
import com.spring.library.util.HashNMacUtil;

@Controller
public class UserController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	BookChatService bookChatService;
	
	@Autowired
	BookReviewService bookReviewService;

	@RequestMapping(value="/user/userReg", method=RequestMethod.GET)
	public void userReg (Model model, HttpServletRequest request) {	
		
		model.addAttribute("login", request.getParameter("btn"));
	}

	// 회원가입 유저 데이터(ID,PW,NAME) insert
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/user/joinSuccess.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> UserJoinSuccess(@RequestParam Map<String, String> formData, ModelMap model,
			HttpServletRequest request) throws Exception {
	
		Map<String, String> map = new HashMap<String, String>();
		String password = (String) formData.get("password");
		formData.put("password", HashNMacUtil.EncBySha256(password));
	
		int insertResult = userService.joinUser(formData);

		if (insertResult == 1) {
			map.put("proc", "success");
		} else {
			throw new Exception();
		}
	
		return map;
	}
	
	//회원가입 -> ID 중복 체크하기
	@RequestMapping(value = "/user/joinIdCheck.do")
	@ResponseBody
	public Map<String, Object> UserJoinIdCheck(ModelMap model, @RequestParam("id") String id,
			HttpServletRequest request) throws Exception {
	
		Map<String, Object> map = new HashMap<String, Object>();
	
		String idResult = userService.userJoinIdCheck(id);
		if (id.equals(idResult)) {
			map.put("data", "EXIST");
		} else if (!id.equals(idResult)) {
			map.put("data", "SUCCESS");
		}
	
		return map;
	}	
	
	
	// 로그인
	@RequestMapping(value = "/user/loginSuccess.do")
	@ResponseBody  
	public Map<String, String> UserLoginSuccess(ModelMap model, HttpServletRequest request,
			@RequestParam Map<String, String> formData) throws Exception {

		String password = (String) formData.get("password");
		
		formData.put("password", HashNMacUtil.EncBySha256(password));
		
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> result = new HashMap<String, String>();
		
		map = userService.userLogin(formData);
		
		HttpSession session = request.getSession(true);
		
		if (!StringUtils.isEmpty(map)) {
			session.setAttribute("loginSession", map);
			result.put("data", "SUCCESS");
		} else if(StringUtils.isEmpty(map)){
			result.put("data", "FAIL");
		}else {
			throw new Exception();
		}

		return result;
	}	
	
	//로그아웃
	@RequestMapping(value = "/user/logout.do", method=RequestMethod.GET)
	public String UserLogOut(ModelMap model, HttpServletRequest request) throws Exception {
		
		HttpSession session = request.getSession(true);
		session.removeAttribute("loginSession");
		
		return "redirect:/";
	}	
	
	//마이페이지
	@RequestMapping(value = "/user/userMyPage", method=RequestMethod.GET)
	public void userMyPage(ModelMap model, HttpServletRequest request, HttpSession session) throws Exception { 
		
		HashMap<String,Object> sessionInfo = (HashMap<String,Object>) session.getAttribute("loginSession");
		String seq = sessionInfo.get("userSeq").toString();
		
		//관리자
		if(sessionInfo.get("userId").toString().equals("adminmaster")) {
			List<Map<String, String>> list = userService.userList(); //유저데이터 전부 들고오기
			model.addAttribute("list", list);
			List<Map<String, String>> bbsList = userService.bbsList(); //토론모집게시글 + 리뷰게시글 전부 들고오기
			model.addAttribute("bbsList", bbsList);
		}
		//일반회원
		else {
			ArrayList<Map<String,Object>> chatList = bookChatService.getMypageChatList(seq); //현재 로그인중인 유저가 참여하고 있는 토론 리스트 get
			model.addAttribute("chatList", chatList);
			
			List<HashMap<String,Object>> reviewList = bookReviewService.getMyPageReviewList(seq); //현재 로그인중인 유저가 작성한 리뷰리스트 get
			model.addAttribute("reviewList", reviewList);
		}
		
		if(request.getParameter("bbs_admin") != null) {
			model.addAttribute("bbs_admin", request.getParameter("bbs_admin"));
		}
	}	
	
	//관리자 - 게시글삭제
	@RequestMapping(value= "/user/bbsRemove", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, String> bbsRemove(@RequestParam Map<String, String> formData,
			ModelMap model, HttpServletRequest request) throws Exception { 
		
		String table = formData.get("table_nm"); //테이블명
		int cnt = 0;
		
		if(table.equals("bookreview")) {
			cnt = userService.bookreviewRemove(formData); //리뷰삭제
		}else {
			cnt = userService.nonfacedebatecollectRemove(formData); //토론모집글 삭제
		}
		
		Map<String, String> map = new HashMap();
		
		if(cnt > 0) {
			map.put("proc", "success");
		}else {
			map.put("proc", "fail");
		}
		
		return map;
	}
	
	
	//일반회원 - 개인정보수정
	@RequestMapping(value= "/user/userUpdate", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, String> userUpdate(@RequestParam Map<String, String> formData,
			ModelMap model, HttpServletRequest request) throws Exception { 
		//userMyPage.jsp에서 직렬화 시켜 넘어온 {key:value}형태의 폼데이터 중 "현재비밀번호" 암호화시켜 다시 put
		String password = (String) formData.get("password_cur"); 
		formData.put("password", HashNMacUtil.EncBySha256(password));
	
		int result = userService.userUpdate(formData); //유저정보 업데이트 처리
		Map<String, String> map = new HashMap();
		
		if(result == 1) {
			map.put("proc", "success");
		}else {
			map.put("proc", "fail");
		}
		
		return map;
	}
	
	//일반회원 - 비밀번호변경
	@RequestMapping(value= "/user/userPassWDupdate", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, String> userPassWDupdate(@RequestParam Map<String, String> formData,
			ModelMap model, HttpServletRequest request) throws Exception { 
		
		String password_update = (String) formData.get("password_update"); //변경할 비밀번호
		String password = (String) formData.get("password"); //현재비밀번호 -> 일치해야 변경이 가능!
		formData.put("password", HashNMacUtil.EncBySha256(password));
		formData.put("password_update", HashNMacUtil.EncBySha256(password_update));

		int result = userService.userPassWDupdate(formData);
		
		Map<String, String> map = new HashMap();
		if(result == 1) {
			map.put("proc", "success");
		}else {
			map.put("proc", "fail");
		}
		return map;
	}
	
	//운영관리 - 회원 비밀번호 수정
	@RequestMapping(value= "/user/adminPasswd", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, String> adminPasswd(@RequestParam Map<String, String> formData,
			ModelMap model, HttpServletRequest request) throws Exception { 
		
		String password_update = (String) formData.get("password_update"); //관리자가 업데이트할 회원의 비밀번호
		formData.put("password_update", HashNMacUtil.EncBySha256(password_update));
		int result = userService.userPassWDupdate(formData);
		
		Map<String, String> map = new HashMap();
		if(result == 1) {
			map.put("proc", "success");
		}else {
			map.put("proc", "fail");
		}
		return map;
	}
	
	//관리자 - 통계차트
	@RequestMapping(value="/user/chartData", method=RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> chartDate() {
		
		HashMap<String,Object> userMap = userService.getUserJoinData(); //오늘날짜부터 가입일자 기준 7일간의 회원가입자 수
		
		HashMap<String,Object> reviewMap = userService.getReviewCnt(); //오늘날짜부터 등록일자 기준 7일간의 리뷰작성 수
		
		Map<String,Object> map = new HashMap<>();
		
		map.put("user", userMap);
		map.put("rev", reviewMap);
		
		return map;
	}
}
