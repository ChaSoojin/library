package com.spring.library.controller;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.library.service.RissService;
import com.spring.library.util.ResponseHelper;

@Controller
public class RissSearchController {

	@Autowired
	RissService service;

	@RequestMapping(value = "/rissSearch/rissSearch", method = RequestMethod.POST)
	@ResponseBody
	public void rissList(@RequestParam HashMap<String, String> formData, HttpServletResponse response) {
		JSONObject jsonResult = new JSONObject();

		try {
			String query = formData.get("query");
			String URL = "http://www.riss.kr/search/Search.do?isDetailSearch=N&searchGubun=true&viewYn=OP&query="
					+ query
					+ "&queryText=&iStartCount=0&iGroupView=5&icate=bib_t&colName=re_a_kor&exQuery=&exQueryText=&order=%2FDESC&onHanja=false&strSort=RANK&pageScale=10&orderBy=&fsearchMethod=search&isFDetailSearch=N&sflag=1&searchQuery=%EC%BD%94%EB%A1%9C%EB%82%98&fsearchSort=&fsearchOrder=&limiterList=&limiterListText=&facetList=&facetListText=&fsearchDB=&resultKeyword=%EC%BD%94%EB%A1%9C%EB%82%98&pageNumber=1&p_year1=&p_year2=&dorg_storage=&mat_type=&mat_subtype=&fulltext_kind=&t_gubun=&learning_type=&language_code=&ccl_code=&language=&inside_outside=&fric_yn=&image_yn=&regnm=&gubun=&kdc=&ttsUseYn=";
			Connection conn = Jsoup.connect(URL); // Connection생성
			Document html = conn.get(); // HTML파싱

			JSONArray jArray = new JSONArray();
			JSONArray pArray = new JSONArray();
			int cnt = 0;

			Elements cont = html.getElementsByClass("cont"); // riss 사이트 내 html 긁어와서
			for (Element subject : cont) {
				Elements t = subject.getElementsByClass("title"); // 논문제목
				Elements p = subject.getElementsByClass("preAbstract"); // 상세보기

				for (Element title : t) {
					Elements f = title.getElementsByTag("a");
					for (Element e : f) {
						JSONObject obj = new JSONObject();

						// JSON 객체에 각각 넣어주고
						obj.put("title", e.text());
						obj.put("url", e.attr("href"));

						// JSONArray 배열에 객체 add
						jArray.add(obj);
					}

					for (Element e : p) {
						JSONObject obj2 = new JSONObject();
						obj2.put("detail", e.text());
						pArray.add(obj2);
						cnt++;
					}						

					// jsonResult json객체에 전체 json배열 담아주기
					jsonResult.put("data", jArray);
					jsonResult.put("detail", pArray);
				}
			}

			if (cnt > 0) {
				jsonResult.put("proc", "success");
				System.out.println(cnt + "개");
			} else {
				jsonResult.put("proc", "fail");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		ResponseHelper.sendJSON(response, jsonResult);
	}

	// DB에 저장된 핫한 논문 1건 조회(Service) 후 RissSearchController에서 받아 뷰로 전달
	@RequestMapping(value = "/rissSearch/rissSearch", method = RequestMethod.GET)
	public void rissSearch(Model model) {
		HashMap<String, Object> map = service.list();
		model.addAttribute("riss", map);
	}
}
