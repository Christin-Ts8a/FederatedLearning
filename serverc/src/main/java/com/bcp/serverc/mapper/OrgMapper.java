package com.bcp.serverc.mapper;

import com.bcp.serverc.model.Org;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface OrgMapper extends Mapper<Org> {

    /**
     * 根据条件筛选机构
     * @param org 筛选条件
     * @return
     */
    List<Org> selectByConditions(Org org);

    /**
     * 根据任务 ID，查询所有参与此任务的用户所在机构地址
     * @param taskId
     * @return
     */
    List<Org> queryAddressesByTaskId(@Param("taskId") Long taskId);
}
