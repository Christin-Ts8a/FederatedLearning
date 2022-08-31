package com.bcp.serverc.controller;

import com.bcp.general.crypto.BcpCiphertext;
import com.bcp.general.crypto.BcpKeyPair;
import com.bcp.general.crypto.PP;
import com.bcp.general.util.JsonResult;
import com.bcp.serverc.crypto.BCP;
import com.bcp.serverc.crypto.MD5;
import com.bcp.serverc.model.Org;
import com.bcp.serverc.model.User;
import com.bcp.serverc.service.OrgService;
import com.bcp.serverc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	@Resource
	OrgService orgService;

	/**
	 * 注册之前调用，检查用户名是否已经存在
	 * @param username 用户名
	 * @return
	 */
	@GetMapping("/userExist")
	public JsonResult userExist(@RequestParam("username") String username) {
		int result = userService.userExist(username);
		return result == 1 ? JsonResult.errorMsg("用户名已存在") : JsonResult.ok();
	}

	@PostMapping("/isLogin")
	public JsonResult isLogin() {
		return JsonResult.ok();
	}

	/**
	 * 用户注册
	 * 注册之后自动登录（携带 cookie 返回）
	 * @param user 用户信息，用户名密码必填
	 * @return
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public JsonResult register(HttpSession session, HttpServletResponse response, @RequestBody @Valid User user) throws UnsupportedEncodingException {
		user.setPassword(MD5.md5PasswordEncoder(user.getPassword()));
		if (user.getNickname() == null) {
			// 若昵称为空，则初始化为userId
			user.setNickname(user.getUsername());
		}
		int result = userService.register(user);
		if (result == 1) {
			user.setPassword(null);
			session.setAttribute("SYSTEM_USER_SESSION", user);
			Cookie cookie = new Cookie("username",user.getUsername());
			cookie.setMaxAge(24 * 60 * 60);
			cookie.setPath("/");
			cookie.setDomain(null);
			response.addCookie(cookie);
			if (user.getRoleType() < 2) {
				Org org = orgService.getOrgById(user.getOrgCode());
				WebSocketServer.loginOrg.put(user.getOrgCode().toString(), org.getServerAddress());
			}
			Map<String, String> res = new HashMap<>();
			res.put("nickname", user.getNickname());
			res.put("role", user.getRoleType().toString());
			res.put("orgCode", user.getOrgCode().toString());
			return JsonResult.ok(res);
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
			result.setPassword(null);
			session.setAttribute("SYSTEM_USER_SESSION", result);
			Cookie cookie = new Cookie("username", user.getUsername());
			cookie.setMaxAge(24 * 60 * 60);
			cookie.setPath("/");
			response.addCookie(cookie);
			if (result.getRoleType() < 2) {
				Org org = orgService.getOrgById(result.getOrgCode());
				WebSocketServer.loginOrg.put(result.getOrgCode().toString(), org.getServerAddress());
			}
			Map<String, String> res = new HashMap<>();
			res.put("nickname", result.getNickname());
			res.put("role", result.getRoleType().toString());
			res.put("orgCode", result.getOrgCode().toString());
			return JsonResult.ok(res);
		}
		return JsonResult.errorMsg("用户名/密码错误");
	}

	@PostMapping("/logout")
	public JsonResult logout(HttpSession session, HttpServletResponse response) {
		User logoutUser = (User) session.getAttribute("SYSTEM_USER_SESSION");
		session.removeAttribute("SYSTEM_USER_SESSION");
		Cookie cookie = new Cookie("username", "");
		cookie.setMaxAge(0);
		cookie.setPath(null);
		response.addCookie(cookie);
		if (logoutUser.getRoleType() < 2) {
			WebSocketServer.loginOrg.remove(logoutUser.getOrgCode().toString());
		}
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

	// 临时客户端用，生成公钥及加解密接口
	@RequestMapping(value = "/keyGen", method = RequestMethod.POST)
	public JsonResult keyGen(@RequestBody PP pp) {
		BcpKeyPair keyGen = BCP.keyGen(pp.getN(), pp.getG());
		return JsonResult.ok(keyGen);
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
