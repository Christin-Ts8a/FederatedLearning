package com.bcp.serverc.controller;

import com.bcp.general.util.JsonResult;
import com.bcp.serverc.service.TrainHistoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/history")
public class TrainHistoryController {
    @Resource
    private TrainHistoryService trainHistoryService;

    @PostMapping("/list")
    public JsonResult list(HttpServletRequest request, @RequestBody JSONObject param) {
        // 分页功能
        int pageSize = 10;
        // 如果传输的参数中包含pageSize
        if (param.containsKey("pageSize")){
            // 取出其中的值
            pageSize = param.getInt("pageSize");
        }
        int pageNum = param.getInt("pageNum");
        PageHelper.startPage(pageNum,pageSize);
        List<Map<String, String>> result = trainHistoryService.list(request, param);
        PageInfo<Map<String, String>> pageInfo = new PageInfo<>(result);
        return JsonResult.ok(pageInfo);
    }
}
