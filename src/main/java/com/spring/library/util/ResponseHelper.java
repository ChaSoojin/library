package com.spring.library.util;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

public class ResponseHelper {
	//json객체 -> 자바스크립트형식으로 바꿔주기
	public static void sendJSON(HttpServletResponse response, JSONObject jsonObject) {
		try {
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().write(jsonObject.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
