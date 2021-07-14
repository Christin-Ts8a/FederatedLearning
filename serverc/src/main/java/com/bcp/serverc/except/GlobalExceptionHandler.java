package com.bcp.serverc.except;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bcp.general.model.RetModel;


@ControllerAdvice()
@ResponseBody
public class GlobalExceptionHandler {

//	RetModel errorModel = new RetModel(-1, "请求错误", null);
	
	@ExceptionHandler(value = Exception.class) // 拦截所有异常, 这里只是为了演示，一般情况下一个方法特定处理一种异常
	public ResponseEntity<RetModel> exceptionHandler(Exception e) {
		if (e instanceof Exception) {
			e.printStackTrace();
			RetModel errorModel = new RetModel(-1, "请求错误：" + e.getMessage(), null);
			return ResponseEntity.status(400).body(errorModel);
		}
		return null;
	}
}
