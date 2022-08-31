package com.bcp.serverc.service;

import com.bcp.serverc.model.PredictData;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface PredictDataService {
    boolean add(HttpServletRequest request, PredictData param);

    List<Map<String, String>> list(JSONObject param);

    boolean delete(HttpServletRequest request, PredictData param);
}
