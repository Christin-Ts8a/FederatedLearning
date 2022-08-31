package com.bcp.serverc.controller;

import com.bcp.general.util.JsonResult;
import com.bcp.serverc.model.TrainModel;
import com.bcp.serverc.service.TrainModelService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/model")
public class TrainModelController {
    @Resource
    private TrainModelService trainModelService;

    /**
     * 添加模型(机构本地分享的模型)
     * @param file 模型文件
     * @return
     */
    @PostMapping("/addModelFile")
    public JsonResult addModelFile(@RequestParam("file") MultipartFile file) {
        TrainModel trainModel = new TrainModel();
        if (file != null)
            trainModel.setModelName(file.getOriginalFilename().split("\\.")[0]);
        TrainModel result = trainModelService.addModelFile(file, trainModel);
        return result == null ? JsonResult.errorMsg("添加模型失败") : JsonResult.ok(result);
    }


    /**
     * 添加模型
     * @param trainModel 模型信息
     * @return
     */
    @PostMapping("/addModel")
    public JsonResult addModel(HttpServletRequest request, @RequestBody TrainModel trainModel) {
        TrainModel result = trainModelService.addModel(request, trainModel);
        return result == null ? JsonResult.errorMsg("添加模型失败") : JsonResult.ok(result);
    }

    /**
     * 更新模型信息
     * @param trainModel 模型信息
     * @return
     */
    @PutMapping("/update")
    public JsonResult update(@RequestBody TrainModel trainModel) {
        TrainModel result = trainModelService.update(trainModel);
        return result == null ? JsonResult.errorMsg("模型更新失败") : JsonResult.ok(result);
    }

    /**
     * 根据条件查询模型信息
     * @param trainModel 筛选条件
     * @return
     */
    @PostMapping("/list")
    public JsonResult list(HttpServletRequest request, @RequestBody(required = false) JSONObject trainModel) {
        // 分页功能
        int pageSize = 10;
        // 如果传输的参数中包含pageSize
        if (trainModel.containsKey("pageSize")){
            // 取出其中的值
            pageSize = trainModel.getInt("pageSize");
        }
        int pageNum = trainModel.getInt("pageNum");
        PageHelper.startPage(pageNum,pageSize);
        List<Map<String, String>> result = trainModelService.list(trainModel, request);
        PageInfo<Map<String, String>> pageInfo = new PageInfo<>(result);
        return JsonResult.ok(pageInfo);
    }

    /**
     * 删除模型
     * @param trainModel 要删除的模型信息
     * @return
     */
    @DeleteMapping("/delete")
    public JsonResult delete(@RequestBody TrainModel trainModel) {
        boolean result = trainModelService.delete(trainModel);
        return result ? JsonResult.errorMsg("删除失败") : JsonResult.ok();
    }
}
