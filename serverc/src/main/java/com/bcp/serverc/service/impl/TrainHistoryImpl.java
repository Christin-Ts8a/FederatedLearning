package com.bcp.serverc.service.impl;

import com.bcp.serverc.mapper.OrgMapper;
import com.bcp.serverc.mapper.TrainHistoryMapper;
import com.bcp.serverc.model.TrainHistory;
import com.bcp.serverc.service.TrainHistoryService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TrainHistoryImpl implements TrainHistoryService {
    private Logger logger = LoggerFactory.getLogger(TrainHistoryImpl.class);
    @Resource
    private TrainHistoryMapper trainHistoryMapper;

    @Resource
    private OrgMapper orgMapper;

    @Override
    public List<Map<String, String>> list(HttpServletRequest request, JSONObject param) {
        List<TrainHistory> list = trainHistoryMapper.selectAll();
        List<Map<String, String>> result = new ArrayList<>();
        for (TrainHistory trainHistory : list) {
            Map<String, String> map = new HashMap<>();
            map.put("id", trainHistory.getId().toString());
            map.put("trainName", trainHistory.getTrainName());
            map.put("modelName", trainHistory.getModelName());
            Date date = new Date(trainHistory.getFinishTime().getTime());
            String strDateFormat = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
            map.put("finishTime", sdf.format(date));
            String orgList = trainHistory.getOrgList();
//            String[] split = orgList.split(",");
//            String orgNameList = "";
//            for (int i = 0; i < split.length - 1; i++) {
//                String orgName = orgMapper.selectByPrimaryKey(split[i]).getOrgName();
//                orgNameList += orgName + ",";
//            }
//            orgNameList = orgNameList.substring(0, orgNameList.length() - 1);
            map.put("orgList", orgList);
            result.add(map);
        }
        logger.info("Train History list result: " + result);
        return result;
    }
}
