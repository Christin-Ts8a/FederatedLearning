package com.bcp.serverc.service.impl;

import com.bcp.serverc.mapper.PredictDataMapper;
import com.bcp.serverc.mapper.UserMapper;
import com.bcp.serverc.model.PredictData;
import com.bcp.serverc.model.User;
import com.bcp.serverc.service.PredictDataService;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PredictDataServiceImpl implements PredictDataService {
    @Resource
    private PredictDataMapper predictDataMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public List<Map<String, String>> list(JSONObject param) {
        List<PredictData> predictData = predictDataMapper.selectAll();
        List<Map<String, String>> result = new ArrayList<>();
        for (PredictData temp :
                predictData) {
            Map<String, String> map = new HashMap<>();
            map.put("id", temp.getId().toString());
            map.put("createUser", temp.getCreateUser());
            map.put("predictDataName", temp.getPredictDataName());
            User user = new User();
            user.setUsername(temp.getCreateUser());
            User userInfo = userMapper.selectOne(user);
            map.put("createUsername", userInfo.getNickname());
            Date date = new Date(temp.getCreateTime().getTime());
            String strDateFormat = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
            map.put("createTime", sdf.format(date));
            map.put("description", temp.getDescription());
            result.add(map);
        }
        return result;
    }

    @Override
    public boolean add(HttpServletRequest request, PredictData param) {
        if (predictDataMapper.selectOne(param) != null) {
            return false;
        }
        User loginUser = (User) request.getSession().getAttribute("SYSTEM_USER_SESSION");
        param.setCreateUser(loginUser.getUsername());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        param.setCreateTime(timestamp);
        System.out.println(param);
        return predictDataMapper.insert(param) == 1;
    }

    @Override
    public boolean delete(HttpServletRequest request, PredictData param) {
        String username = param.getCreateUser();
        param.setCreateUser(null);
        PredictData del = predictDataMapper.selectOne(param);
        if (!username.equals(del.getCreateUser())) {
            return false;
        } else {
            int result = predictDataMapper.delete(param);
            return result == 1;
        }
    }
}
