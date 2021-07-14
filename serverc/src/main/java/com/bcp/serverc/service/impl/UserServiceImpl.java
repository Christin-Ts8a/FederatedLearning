package com.bcp.serverc.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bcp.serverc.mapper.UserMapper;
import com.bcp.general.model.RetModel;
import com.bcp.serverc.model.User;

@Transactional(readOnly = false, rollbackFor = Exception.class)
@Service
public class UserServiceImpl implements UserDetailsService {

	@Autowired
	UserMapper userMapper;

	/**
	 * 注入bcrypt加密器
	 */
	// @Resource(name = "passwordEncoder")
	@Autowired
	@Lazy
	PasswordEncoder bCryptPasswordEncoder;

	/**
	 * 注册接口
	 * 
	 * @return
	 */
	public Object register(User user) {
		// 需要的参数
		String userId = user.getUserId();
		String pwd = user.getPassword();

		// 设置密码
		String hashedPwd = bCryptPasswordEncoder.encode(pwd);
		user.setPassword(hashedPwd);

		if (user.getNickname() == null) {
			// 若昵称为空，则初始化为userId
			user.setNickname(userId);
		}

		userMapper.insert(user);
		RetModel retModel = new RetModel();
		retModel.setRetCode(0);
		retModel.setRetMess("register success");
		retModel.setRetValue(user.getUserId());
		return retModel;
	}

	public Object getUserList() {
		List<User> all = userMapper.selectAll();
		List<String> nickNameList = all.stream().map(user -> user.getUserId()).collect(Collectors.toList());
		return nickNameList;
	}

	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		User user = userMapper.selectByPrimaryKey(userId);
		if (user == null) {
			throw new UsernameNotFoundException("账户不存在!");
		}
		// user.setRoles(userMapper.getUserRolesByUid(user.getId()));
		return user;
	}

	/**
	 * 获取当前登录用户Id
	 * 
	 * @return
	 */
	public static String getCurrentLoginUserId() {
		User currentLoginUser = getCurrentLoginUser();
		if (currentLoginUser != null) {
			return currentLoginUser.getUserId();
		}
		return null;
	}

	public static User getCurrentLoginUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof User) {
				// 未登录时principal是字符串的anonymousUser，会抛出ClassCastException
				return (User) principal;
			}
		}
		return null;
	}

}
