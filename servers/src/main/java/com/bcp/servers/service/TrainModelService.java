package com.bcp.servers.service;

import com.bcp.servers.model.TrainModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TrainModelService {

    /**
     * 添加训练模型（初始模型）
     * @param file
     * @param trainModel
     * @return
     */
    TrainModel add(MultipartFile file, TrainModel trainModel);

    /**
     * 更新模型信息
     * @param trainModel 模型信息
     * @return
     */
    TrainModel update(TrainModel trainModel);

    /**
     * 根据条件查询模型信息
     * @param trainModel 筛选条件
     * @return
     */
    List<TrainModel> list(List<TrainModel> trainModel);

    /**
     * 删除模型
     * @param trainModel 要删除的模型信息
     * @return
     */
    boolean delete(TrainModel trainModel);
}
