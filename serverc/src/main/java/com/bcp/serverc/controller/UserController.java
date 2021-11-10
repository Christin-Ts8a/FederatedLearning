package com.bcp.serverc.controller;

import com.bcp.general.crypto.BcpCiphertext;
import com.bcp.general.crypto.BcpKeyPair;
import com.bcp.general.crypto.PP;
import com.bcp.general.util.JsonResult;
import com.bcp.serverc.crypto.BCP;
import com.bcp.serverc.crypto.MD5;
import com.bcp.serverc.model.User;
import com.bcp.serverc.service.UserService;
import org.apache.tomcat.util.bcel.classfile.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理注册，登录，用户信息等接口
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserService userService;

	/**
	 * 注册之前调用，检查用户名是否已经存在
	 * @param username 用户名
	 * @return
	 */
	@GetMapping("userExist")
	public JsonResult userExist(@RequestParam("username") String username) {
		int result = userService.userExist(username);
		return result == 1 ? JsonResult.errorMsg("用户名已存在") : JsonResult.ok();
	}

	/**
	 * 用户注册
	 * 注册之后自动登录（携带 cookie 返回）
	 * @param user 用户信息，用户名密码必填
	 * @return
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public JsonResult register(HttpSession session, HttpServletResponse response, @RequestBody @Valid User user) {
		user.setPassword(MD5.md5PasswordEncoder(user.getPassword()));
		if (user.getNickname() == null) {
			// 若昵称为空，则初始化为userId
			user.setNickname(user.getUsername());
		}
		int result = userService.register(user);
		if (result == 1) {
			session.setAttribute("SYSTEM_USER_SESSION", user);
			Cookie cookie = new Cookie("username", user.getNickname());
			cookie.setHttpOnly(true);
			cookie.setMaxAge(60 * 60);
			response.addCookie(cookie);
			WebSocketServer.loginUsers.put(user.getUsername(), user.getNickname());
			return JsonResult.ok();
		}
		return JsonResult.errorMsg("注册失败");
	}

	/**
	 * 登录接口
	 * 登录成功后cookie 中写入用户信息，并且在 websocket 中写入登录用户名
	 * @param response 用于登录成功后携带 cookie
	 * @param user 用户名密码
	 * @return
	 */
	@PostMapping("/login")
	public JsonResult login(HttpSession session, HttpServletResponse response, @RequestBody User user) {
		user.setPassword(MD5.md5PasswordEncoder(user.getPassword()));
		User result = userService.authenticate(user);
		if (result != null) {
			session.setAttribute("SYSTEM_USER_SESSION", result);
			Cookie cookie = new Cookie("nickname", result.getNickname());
			cookie.setMaxAge(30 * 24 * 60 * 60);
			cookie.setPath("/");
			cookie.setDomain("localhost");
			response.addCookie(cookie);
			WebSocketServer.loginUsers.put(result.getUsername(), result.getNickname());
			return JsonResult.ok(result.getUsername());
		}
		return JsonResult.errorMsg("用户名/密码错误");
	}

	@PostMapping("/logout")
	public JsonResult logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		session.removeAttribute("SYSTEM_USER_SESSION");
		Cookie cookie = new Cookie("nickname", "");
		cookie.setMaxAge(0);
		cookie.setPath("/");
		cookie.setDomain("localhost");
		response.addCookie(cookie);
		return JsonResult.ok();
	}

	/**
	 * 查询用户列表
	 *
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Object getUserList() {
		Object ret = userService.getUserList();
		return JsonResult.ok(ret);
	}

	// 临时客户端用生成公钥及加解密接口
	@RequestMapping(value = "/keyGen", method = RequestMethod.POST)
	public BcpKeyPair keyGen(@RequestBody PP pp) {
		BcpKeyPair keyGen = BCP.keyGen(pp.getN(), pp.getG());
		return keyGen;
	}

	@RequestMapping(value = "/enc", method = RequestMethod.POST)
	public List<BcpCiphertext> enc(BigInteger N, BigInteger g, BigInteger h,
			@Valid @RequestBody List<BigInteger> mList) {
		List<BcpCiphertext> rtn = mList.stream().map((m) -> BCP.enc(N, g, h, m)).collect(Collectors.toList());
		return rtn;
	}

	@RequestMapping(value = "/dec", method = RequestMethod.POST)
	public List<String> dec(BigInteger N, BigInteger a, @Valid @RequestBody List<BcpCiphertext> cList) {
		List<String> rtn = cList.stream().map((c) -> BCP.dec(N, a, c).toString()).collect(Collectors.toList());
		return rtn;
	}
}
