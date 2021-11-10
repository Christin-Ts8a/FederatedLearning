package com.bcp.serverc.controller;

import com.bcp.serverc.service.UserService;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.bcp.serverc.service.impl.UserServiceImpl;

import javax.annotation.Resource;

@Controller
@RequestMapping
public class WebSocketController {

	// @Autowired
	// private WebSocketServer webSocketServer;

	@Resource
	private UserService userService;

	/**
	 * 当没有传递connect_message参数时，默认将userId作为参数转发至ws服务
	 *
	 * @return
	 */
	@GetMapping("/webSocket")
	public ModelAndView socket() {
		Long userId = userService.getCurrentLoginUser().getUserId();
//		System.out.println("userId: " + userId);
		if (userId == null) {
			// 若未登录则返回空
			return null;
		}
		ModelAndView mav = new ModelAndView("/webSocket/" + userId);
		// mav.addObject("userId", userId);// 这种方式无法传递路径参数
		return mav;
	}

}
