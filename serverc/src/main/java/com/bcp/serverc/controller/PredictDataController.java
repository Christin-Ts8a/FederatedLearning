package com.bcp.serverc.controller;

import com.bcp.general.util.JsonResult;
import com.bcp.serverc.model.PredictData;
import com.bcp.serverc.model.User;
import com.bcp.serverc.service.PredictDataService;
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
@RequestMapping("/predict")
public class PredictDataController {

    @Resource
    private PredictDataService predictDataService;

    @PostMapping("/add")
    public JsonResult add(HttpServletRequest request, @RequestBody PredictData param) {
        boolean result = predictDataService.add(request, param);
        return result ? JsonResult.ok() : JsonResult.errorMsg("添加失败");
    }

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
        List<Map<String, String>> list = predictDataService.list(param);
        PageInfo<Map<String, String>> pageInfo = new PageInfo<>(list);
        return JsonResult.ok(pageInfo);
    }

    @PostMapping("/delete")
    public JsonResult delete(HttpServletRequest request, @RequestBody PredictData param) {
        User loginUser = (User) request.getSession().getAttribute("SYSTEM_USER_SESSION");
        param.setCreateUser(loginUser.getUsername());
        boolean result = predictDataService.delete(request, param);
        return result ? JsonResult.ok() : JsonResult.errorMsg("删除失败");
    }
}
