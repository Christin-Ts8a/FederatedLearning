package com.bcp.serverc.service;

import com.bcp.serverc.model.TrainModel;
import net.sf.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface TrainModelService {

    /**
     * 添加训练模型（初始模型）
     *
     *
     * @param file
     * @param trainModel
     * @return
     */
    TrainModel addModelFile(MultipartFile file, TrainModel trainModel);

    /**
     * 更新模型信息
     * @param trainModel 模型信息
     * @return
     */
    TrainModel update(TrainModel trainModel);

    /**
     * 根据条件查询模型信息
     * @param trainModel 筛选条件
     * @param request
     * @return
     */
    List<Map<String, String>> list(JSONObject trainModel, HttpServletRequest request);

    /**
     * 删除模型
     * @param trainModel 要删除的模型信息
     * @return
     */
    boolean delete(TrainModel trainModel);

    /**
     * 添加模型信息（用户传到本地的模型，在云端存一份信息）
     *
     * @param request
     * @param trainModel
     * @return
     */
    TrainModel addModel(HttpServletRequest request, TrainModel trainModel);
}
