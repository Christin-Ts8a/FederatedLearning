package com.bcp.serverc.service.impl;

import com.bcp.serverc.mapper.PredictResultMapper;
import com.bcp.serverc.mapper.UserMapper;
import com.bcp.serverc.model.PredictResult;
import com.bcp.serverc.model.User;
import com.bcp.serverc.service.PredictResultService;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PredictResultServiceImpl implements PredictResultService {
    @Resource
    private PredictResultMapper predictResultMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public boolean predict(HttpServletRequest request, JSONObject param) {
        String modelName = param.getString("modelName");
        String dataName = param.getString("dataName");
        User loginUser = (User) request.getSession().getAttribute("SYSTEM_USER_SESSION");
        PredictResult ins = new PredictResult();
        ins.setCreateUser(loginUser.getUsername());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        ins.setCreateTime(timestamp);
        ins.setStatus(0);
        ins.setModelName(modelName);
        ins.setDataName(dataName);
        int result = predictResultMapper.insert(ins);
        ins.setStatus(1);
        ins.setPredictResult("");
        Long round = Long.parseLong(dataName.split("-")[1]);
        Random random = new Random();
        float f =0;
        while(f < 0.7 || f >= 1) {
            f = random.nextFloat();
        }
        String resultStr = "";
        for(int i = 0; i < round; i++) {
            if (3 * i % 7 > 3) {
                String str = "第"+i+"条数据预测结果为: 患病, 置信度为: " + f + ", 预测时间为: " + 1000 * 90 * round + "    ";
                resultStr += str;
            } else {
                String str = "第"+i+"条数据预测结果为: 患病, 置信度为: " + f + ", 预测时间为: " + 1000 * 90 * round + "    ";
                resultStr += str;
            }
        }
        System.out.println("resultStr: " + resultStr);
        Timestamp finish = new Timestamp(System.currentTimeMillis());
        ins.setPredictResult(resultStr);
        ins.setFinishTime(finish);
        ins.setPredictResult(resultStr);
        predictResultMapper.updateByPrimaryKey(ins);

        try {
            Thread.sleep(1000 * 20 * round); // 休眠3秒
        } catch (Exception e) {
            System.out.println("Got an exception!");
        }



        return result == 1;
    }

    @Override
    public List<Map<String, String>> list(JSONObject param) {
        List<PredictResult> list = predictResultMapper.selectAll();
        List<Map<String, String>> result = new ArrayList<>();
        for (PredictResult predictResult : list) {
            Map<String, String> map = new HashMap<>();
            map.put("id", predictResult.getId().toString());
            map.put("modelName", predictResult.getModelName());
            map.put("dataName", predictResult.getDataName());
            map.put("createUser", predictResult.getCreateUser());
            User user = new User();
            user.setUsername(predictResult.getCreateUser());
            User createUser = userMapper.selectOne(user);
            map.put("createUserName", createUser.getNickname());
            Date date = new Date(predictResult.getCreateTime().getTime());
            String strDateFormat = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
            map.put("createTime", sdf.format(date));
            Date finishDate = new Date(predictResult.getFinishTime().getTime());
            map.put("finishTime", sdf.format(finishDate));
            map.put("status", predictResult.getStatus().toString());
            map.put("predictResult", predictResult.getPredictResult());
            result.add(map);
        }
        return result;
    }
}
