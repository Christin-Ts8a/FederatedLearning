// 获取用户昵称
xhr = new XMLHttpRequest;
xhr.onreadystatechange = (event) => {
	let res = event.target;
	if(res.status === 200){
		let oSpan = document.querySelector("h1");
		oSpan.setAttribute("data-before", res.responseText);
	}
}
xhr.open("GET", "/user/nickname", true);
xhr.send();

// 登录后自动连接ws
ws = new WebSocket("ws://" + location.host + "/webSocket");
ws.onmessage = (mess) => {
	console.log(mess);
	window.latestMess = JSON.parse(mess.data);
}
ws.onclose = (mess) => {
	console.log(mess);
	window.latestCloseMess = JSON.parse(mess.data);
}