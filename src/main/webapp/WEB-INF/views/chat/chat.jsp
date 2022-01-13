<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/layout/header.jsp" %>
<c:set var="path" value="${pageContext.request.contextPath}" />

<script src="http://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.js"></script>
<!-- 자바스크립트에서 쓰기 위한 소켓 붙여주기 -->
<script type="text/javascript"
	src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.1.5/sockjs.min.js"></script>

<style>

.url_link {
	color : blue;
}

</style>

		<section class="breadcrumbs-custom bg-image"
			style="background-image: url(${path}/resources/images/breadcrumbs-bg.jpg);">
			<div class="breadcrumbs-custom-inner">
				<div class="breadcrumbs-custom-container container">
					<div class="breadcrumbs-custom-main">
						<h6 class="breadcrumbs-custom-subtitle title-decorated" style="color:white">
							토론 모임
						</h6>
						<h1 class="breadcrumbs-custom-title" style="color:white">
							도서 채팅			
						</h1>
					</div>
				</div>
			</div>
		</section>
		
<section class="section section-lg">
			<div class="container">
				<div class="row">
					<div class="col-md-12">
	<div style="margin-top: 5px;" class="col-md-12">
						<h6 style="margin-left: 5px;">Discussion [${book.debateTitle}]</h6>	
	<div id="messageArea" class="form-input col-md-12" style="overflow:auto; overflow-x:scroll;overflow-x:hidden; height: 550px;"></div>
	
	<div class="col-md-12" style="display:flex;">
		<input type="text"  class="form-input col-md-8" id="_msg_send" onKeyPress="if( event.keyCode==13 ){ msg_sock_chat(); }" style="margin-left: -12px;" />
		<input type="button" id="sendBtn" value="전송" onclick="msg_sock_chat();" class="button button-sm button-primary col-md-2" style="margin: 0; margin-left: 30px;" />
		<a class="button button-sm button-primary col-md-2" style="margin: 0; margin-left: 10px;" href="<c:url value='/rissSearch/rissSearch'/>">논문검색</a>
	</div>
	</div>
</div>
</div>
</div>
</section>
<script type="text/javascript">

function autoLink(value) {

	  var doc = value; //대화내용
	  var regEmail = new RegExp("([xA1-xFEa-z0-9_-]+@[xA1-xFEa-z0-9-]+\.[a-z0-9-]+)","gi"); //이메일 확인 정규식

	  var array = new Array();
	  array = doc.split(' '); //띄어쓰기 기준으로 split 해주기
	  var result = "";

	  for(let i=0; i < array.length; i++) {
	      doc = array[i]; //어절마다

	      //. 앞에 아무것도 없을때(url 아님)
	      if(doc.split('.')[0] == "") {
	            result += doc;
	            result += i == array.length -1 ? "": " ";
	            continue;
	      }

	      // 점이 단독으로 있는지(url 아님)
	      if(doc.slice(doc.length-1, doc.length) == ".") {
	            result += doc;
	            result += i == array.length -1 ? "": " ";
	            continue;
	      }

	      // 이메일 정규식 비교
	      if(regEmail.test(doc)) {
	           result += doc.replace(regEmail,"<a href='mailto:$1'>$1</a>");
	           result += i == array.length -1 ? "": " ";
	           continue;
	      }else {
	           var engNum = /^[ㄱ-ㅎ|가-힣|a-z|A-Z|0-9|\s]+$/; //한글,영어,숫자,특수문자 정규식
	           if(engNum.test(doc)) { //일반적인 대화(url아님)
	                result += doc;
	                result += i == array.length -1 ? "": " ";
	                continue;
	           }

	           //https:// 혹은 http:// 들어가면 이메일로 인지하고 a태그 감싸주기
	           if(doc.indexOf('https://') != -1 || doc.indexOf('http://') != -1 )  {
	               var regURL = new RegExp("(http|https|ftp|telnet|news|irc)://([-/.a-zA-Z0-9_~#%$?&=:200-377()]+)","gi");
	               result += doc.replace(regURL,"<a href='$1://$2' target='_blank' class='url_link'>$1://$2</a>")
	               result += i == array.length -1 ? "": " ";
	               continue;
	           }else { //그외 이메일(https 나 http가 아닌 url)
	               var regURL = new RegExp("([-/.a-zA-Z0-9_~#%$&=:200-377()]+)","gi");
	               result += doc.replace(regURL,"<a href='http://$1' target='_blank' class='url_link'>$1</a>");
	               result += i == array.length -1 ? "": " ";
	               continue;
	           }
	      }
	  }

	    return result; //다시 원래문장 만든뒤 리턴
	}


	function msg_sock_chat() {
		//입력창
		if($('#_msg_send').val() == "") {
			alert('채팅을 입력해주세요.');
			return false;
		}

		let url = "<c:url value='/bookChat/procDate'/>";
		
		$.ajax({
			type : 'POST',
			url : url,
			contentType : "application/x-www-form-urlencoded; charset=UTF-8",
			dataType : "json",
			data : {'chatSeq':'${book.debatecolSeq}', 'userSeq':'${sessionScope.loginSession.userSeq}', 
				'userId':'${sessionScope.loginSession.userId}', 'chatCon' :$('#_msg_send').val() },
			success : function(data) {
				if(data.proc == "success") {
					let msg = autoLink($('#_msg_send').val()) + '|' + '${sessionScope.loginSession.userId}'; //대화내용 | 유저아이디
					$('#_msg_send').val(msg); 
					sendMsg(); //메시지전송
					$('#_msg_send').val(''); //메시지 전송 후 입력창 안에있던 채팅내용 지워주기
				}
 			},
			error : function(xhr, status, error) {
				alert('예기치 못한 에러 발생');
			}
		});
	};

	//자바에서 소켓 처리 위한 자바소켓 생성(EchoHandler의 /echo 호출)
	let sock = new SockJS("http://localhost:8080/echo"); 
	
	function sendMsg() {
		sock.send($("#_msg_send").val());
	}
	
	sock.onmessage = function onMsg(msg) {
		var data = msg.data.split('|')[0]; //대화내용
		var userId = msg.data.split('|')[1]; //채팅을 친 유저아이디
		let _myId = '${sessionScope.loginSession.userId}' == userId ? "(나)" : "" ; //내가 보냈다면 -> 뒤에 (나)
		
		//대화창에 각각 append
		$("#messageArea").append("<span style='font-weight:bold;'>[ " + userId + _myId + " ]<span>");
		$("#messageArea").append("<p style='margin-top:-5px; margin-bottom:3px;'>" + data + "</p>");
		$('#messageArea').scrollTop($('#messageArea')[0].scrollHeight);
		$('#_msg_send').focus();
	}
	
	sock.onclose = function onClose(evt) {
		$("#messageArea").append("연결이 끊겼습니다..");

	}
</script>
<%@ include file="/WEB-INF/views/layout/footer.jsp" %>