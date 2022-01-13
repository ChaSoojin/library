package com.spring.library.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.spring.library.service.BookRankService;

@Controller
public class RankController {
	
	@Autowired
	BookRankService service;
	
	@RequestMapping(value="/bookReview/reviewRank", method=RequestMethod.GET)
	public void reviewRank(Model model) {
		
		List<HashMap<String, Object>> review = service.reviewRank(); //리뷰작성수
		List<HashMap<String, Object>> like = service.likeRank(); //추천수
		List<HashMap<String, Object>> bestBook = service.bestBookRank(); //베스트북(이 책을 가지고 몇개의 게시글을 작성했는지 카운트)
		
		model.addAttribute("review", review);
		model.addAttribute("like", like);
		model.addAttribute("bestBook", bestBook);
	}
}
