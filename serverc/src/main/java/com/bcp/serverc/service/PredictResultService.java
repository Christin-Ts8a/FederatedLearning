package com.bcp.serverc.service;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface PredictResultService {
    List<Map<String, String>> list(JSONObject param);

    boolean predict(HttpServletRequest request, JSONObject param);
}
