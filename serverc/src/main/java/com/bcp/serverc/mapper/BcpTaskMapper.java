package com.bcp.serverc.mapper;

import com.bcp.serverc.model.BcpTask;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface BcpTaskMapper extends Mapper<BcpTask> {
    List<JSONObject> queryTaskByConditions(@Param("params") JSONObject params);
}