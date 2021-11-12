package com.bcp.serverc.service;

import com.bcp.serverc.model.User;
import org.springframework.stereotype.Service;

public interface UserService {
    User getCurrentLoginUser();

    int register(User uSer);

    Object getUserList();

    User authenticate(User user);

    int userExist(String username);
}
