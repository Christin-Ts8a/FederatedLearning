package com.bcp.serverc.mapper;

import com.bcp.serverc.model.Org;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface OrgMapper extends Mapper<Org> {

    /**
     * 根据条件筛选机构
     * @param org 筛选条件
     * @return
     */
    List<Org> selectByConditions(Org org);
}
