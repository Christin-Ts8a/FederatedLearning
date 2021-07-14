package com.bcp.serverc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.bcp.serverc.service.impl.UserServiceImpl;

@Controller
@RequestMapping
public class WebSocketController {

	// @Autowired
	// private WebSocketServer webSocketServer;

	/**
	 * 当没有传递connect_message参数时，默认将userId作为参数转发至ws服务
	 * 
	 * @param userId
	 * @return
	 */
	@GetMapping("/webSocket")
	public ModelAndView socket() {
		String userId = UserServiceImpl.getCurrentLoginUserId();
		if (userId == null) {
			// 若未登录则返回空
			return null;
		}
		ModelAndView mav = new ModelAndView("/webSocket/" + userId);
		// mav.addObject("userId", userId);// 这种方式无法传递路径参数
		return mav;
	}

}
