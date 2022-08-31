package com.bcp.servers.mapper;

import com.bcp.servers.model.TrainModel;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface TrainModelMapper extends Mapper<TrainModel> {

    /**
     * 根据条件获取模型
     * @param temp 模型信息
     * @return
     */
    List<TrainModel> selectByConditions(TrainModel temp);
}
