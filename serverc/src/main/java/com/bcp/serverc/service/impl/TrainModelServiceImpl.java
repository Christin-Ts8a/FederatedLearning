package com.bcp.serverc.service.impl;

import com.bcp.serverc.controller.WebSocketServer;
import com.bcp.serverc.mapper.TrainModelMapper;
import com.bcp.serverc.mapper.UserMapper;
import com.bcp.serverc.model.TrainModel;
import com.bcp.serverc.model.User;
import com.bcp.serverc.service.TrainModelService;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrainModelServiceImpl implements TrainModelService {
    @Resource
    private TrainModelMapper trainModelMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public TrainModel addModel(HttpServletRequest request, TrainModel trainModel) {
        if (trainModelMapper.select(trainModel).size() >= 1) {
            return null;
        }
        User loginUser = (User) request.getSession().getAttribute("SYSTEM_USER_SESSION");
        trainModel.setCreateUser(loginUser.getUsername());
        trainModel.setCreateTime(new Timestamp(System.currentTimeMillis()));
        trainModel.setStatus(0);
        Integer result = trainModelMapper.insert(trainModel);
        return result == 1 ? trainModel : null;
    }

    @Override
    public TrainModel addModelFile(MultipartFile file, TrainModel trainModel) {
        TrainModel isExist = trainModelMapper.selectOne(trainModel);
        if (isExist == null) {
            return null;
        }
        trainModel.setOrgCode(isExist.getOrgCode());
        trainModel.setStatus(1);
        trainModel.setCreateUser(isExist.getCreateUser());
        trainModel.setDataName(isExist.getDataName());
        trainModel.setCreateTime(isExist.getCreateTime());
        trainModel.setId(isExist.getId());
        int result = trainModelMapper.updateByPrimaryKey(trainModel);
        if (result != 1) {
            return null;
        }
        String path = "";
        File upload = null;
        try {
            path = ResourceUtils.getURL("classpath:").getPath();
            upload = new File(path, "/static/model/" + trainModel.getOrgCode() + "/" + file.getOriginalFilename());
            if (!upload.getParentFile().exists()) {
                upload.getParentFile().mkdirs();
            }
            file.transferTo(upload);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return trainModel;
    }

    @Override
    public TrainModel update(TrainModel trainModel) {
        if (trainModelMapper.updateByPrimaryKey(trainModel) == 1) {
            return trainModelMapper.selectByPrimaryKey(trainModel);
        }
        return null;
    }

    @Override
    public List<Map<String, String>> list(JSONObject trainModel, HttpServletRequest request) {
        Map<String, String> conditions = new HashMap<>();
        if (trainModel.containsKey("id")) {
            conditions.put("id", trainModel.getString("id"));
        }
        if (trainModel.containsKey("byOrg") && trainModel.getString("byOrg").equals("1")) {
            User user = (User) request.getSession().getAttribute("SYSTEM_USER_SESSION");
            conditions.put("username", user.getUsername());
        }
        if (trainModel.containsKey("modelName")) {
            conditions.put("modelName", trainModel.getString("modelName"));
        }
        if (trainModel.containsKey("orgCode")) {
            String orgCode = "";
            for (Object temp : trainModel.getJSONArray("orgCode")) {
                orgCode += temp.toString() + ",";
            }
            conditions.put("orgCode", orgCode);
        }
        List<Map<String, String>> trainModels = trainModelMapper.selectByConditions(conditions);
        for (Map<String, String> temp : trainModels) {
            User user = new User();
            user.setUsername(temp.get("createUserId"));
            User userInfo = userMapper.selectOne(user);
            boolean isLogin = WebSocketServer.loginOrg.containsKey(userInfo.getOrgCode().toString());
            temp.put("isOnline", isLogin ? "1" : "0");
        }
        return trainModels;
    }

    @Override
    public boolean delete(TrainModel trainModel) {
        return trainModelMapper.deleteByPrimaryKey(trainModel) == 1;
    }
}
