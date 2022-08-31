package com.bcp.serverc.service;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface TrainHistoryService {
    List<Map<String, String>> list(HttpServletRequest request, JSONObject param);
}
