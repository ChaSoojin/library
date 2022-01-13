package com.spring.library.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@RequestMapping("/echo")
public class EchoHandler extends TextWebSocketHandler{
	private static Logger logger = LoggerFactory.getLogger(EchoHandler.class);
    private List<WebSocketSession> sessionList = new ArrayList<WebSocketSession>(); //세션 모두 저장을 하기위해 어레이리스트 선언

    //pc가 채팅방에 연결되면 동작하는 메소드
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionList.add(session); //세션저장(session: 현재 접속한 클라이언트 정보를 가진 객체)
        logger.info("{} Connected!", session.getId()); //연결되었다고 콘솔창에 띄어주기 -> 소켓 session ID가 고유로 생김
    }

    //클라이언트가 웹소켓 서버로 메시지 전송하면 동작하는 메서드
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    	//메시지 전송한 클라이언트 아이디, 전송한 메시지 차례대로 콘솔출력
    	logger.info("{} 으로 부터 {} 메시지", session.getId(), message.getPayload()); 
       
        for(WebSocketSession s : sessionList){
            s.sendMessage(new TextMessage(message.getPayload())); //메시지를 클라이언트에게 전송
        }
    }

    //클라이언트가 연결 끊으면 동작하는 메소드
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionList.remove(session); //세션삭제 
        logger.info("{} Disconnected..", session.getId()); //접속이 끊긴 클라이언트 아이디랑 Disconnected 콘솔출력
    }
}