<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ include file="../include/header.jsp"%>
<script>
$(function(){
	// login("mem", "1234");
});
function login(userid, pwd){
	var param = "userid=" + userid + "&passwd=" + pwd; 
				// {"userid":userid, "passwd": pwd}
	
	$.ajax({
		type: "post",
		url: "${path}/webview_servlet/login.do",
		data: param,
		success: function(result){ // 백그라운드 실행 결과
			$("#result").html(result);
			window.android.setMessage(result);
		}
	});
}
</script>
</head>
<body>
<h2>웹뷰와의 통신</h2>
<div id="result"></div>
</body>
</html>