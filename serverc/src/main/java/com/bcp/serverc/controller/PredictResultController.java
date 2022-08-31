package com.bcp.serverc.controller;

import com.bcp.general.util.JsonResult;
import com.bcp.serverc.model.PredictData;
import com.bcp.serverc.model.PredictResult;
import com.bcp.serverc.service.PredictResultService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/result")
public class PredictResultController {

    @Resource
    private PredictResultService predictResultService;

    @PostMapping("/list")
    public JsonResult list(@RequestBody JSONObject param) {
        // 分页功能
        int pageSize = 10;
        // 如果传输的参数中包含pageSize
        if (param.containsKey("pageSize")){
            // 取出其中的值
            pageSize = param.getInt("pageSize");
        }
        int pageNum = param.getInt("pageNum");
        PageHelper.startPage(pageNum,pageSize);
        List<Map<String, String>> list = predictResultService.list(param);
        PageInfo<Map<String, String>> pageInfo = new PageInfo<>(list);
        return JsonResult.ok(pageInfo);
    }

    @PostMapping("/predict")
    public JsonResult predict(HttpServletRequest request, @RequestBody JSONObject param){
        boolean result = predictResultService.predict(request, param);
        return result ? JsonResult.ok() : JsonResult.errorMsg("预测失败");
    }
}
