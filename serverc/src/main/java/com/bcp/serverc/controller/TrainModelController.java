package com.bcp.serverc.controller;

import com.bcp.general.util.JsonResult;
import com.bcp.serverc.model.TrainModel;
import com.bcp.serverc.service.TrainModelService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/model")
public class TrainModelController {
    @Resource
    private TrainModelService trainModelService;

    /**
     * 添加模型
     * @param file 模型文件
     * @param trainModel 模型信息
     * @return
     */
    @PostMapping("/add")
    public JsonResult add(@RequestBody MultipartFile file, @Valid @RequestBody TrainModel trainModel) {
        TrainModel result = trainModelService.add(file, trainModel);
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
    public JsonResult list(@RequestBody TrainModel trainModel) {
        List<TrainModel> result = trainModelService.list(trainModel);
        return JsonResult.ok(result);
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
