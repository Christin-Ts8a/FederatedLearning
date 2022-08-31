package com.bcp.serverc.mapper;

import com.bcp.serverc.model.TrainModel;
import net.sf.json.JSONObject;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface TrainModelMapper extends Mapper<TrainModel> {

    /**
     * 根据条件获取模型
     * @param temp 模型信息
     * @return
     */
    List<Map<String, String>> selectByConditions(Map<String, String> temp);
}
