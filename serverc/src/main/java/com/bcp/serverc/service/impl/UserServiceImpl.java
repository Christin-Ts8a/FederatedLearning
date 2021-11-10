package com.bcp.serverc.service.impl;

import com.bcp.general.model.RetModel;
import com.bcp.serverc.crypto.MD5;
import com.bcp.serverc.mapper.UserMapper;
import com.bcp.serverc.model.User;
import com.bcp.serverc.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = false, rollbackFor = Exception.class)
@Service
public class UserServiceImpl implements UserService {

	@Resource
	private UserMapper userMapper;

	/**
	 * 注册接口
	 * 
	 * @return
	 */
	@Override
	public int register(User user) {

		return userMapper.insert(user);
	}

	@Override
	public Object getUserList() {
		List<User> all = userMapper.selectAll();
		List<String> nickNameList = all.stream().map(user -> user.getUserId().toString()).collect(Collectors.toList());
		return nickNameList;
	}

	@Override
	public User getCurrentLoginUser() {
		return null;
	}

	@Override
	public User authenticate(User user) {
		return userMapper.selectOne(user);
	}

	@Override
	public int userExist(String username) {
		User user = new User();
		user.setUsername(username);
		return userMapper.selectOne(user) == null ? 0 : 1;
	}
}
