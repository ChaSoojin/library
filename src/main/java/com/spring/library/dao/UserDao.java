package com.spring.library.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface UserDao {

	public int joinUser(Map<String, String> args);
	
	public String userJoinIdCheck(String args);
	
	public Map<String, String> userLogin(Map<String, String> args);
	
	public List<Map<String, String>> userList();
	
	public List<Map<String, String>> bbsList();

	public int userUpdate(Map<String, String> args);
	
	public int userPassWDupdate(Map<String, String> args);
	
	public int bookreviewRemove(Map<String, String> args);

	public int nonfacedebatecollectRemove(Map<String, String> args);

	public HashMap<String, Object> selectUserJoinData();

	public HashMap<String, Object> selectReviewCnt();
	
}

