package com.bcp.serverc.service;

import com.bcp.serverc.model.Org;

import java.util.List;

public interface OrgService {

    /**
     * 添加机构
     * @param org 机构信息
     * @return
     */
    boolean add(Org org);

    /**
     * 更新机构信息
     * @param org 机构信息
     * @return
     */
    Org update(Org org);

    /**
     * 根据条件筛选机构
     * @param org 筛选条件
     * @return
     */
    List<Org> list(Org org);

    /**
     * 通过机构 ID 获取机构信息
     * @param id 机构 ID
     * @return
     */
    Org getOrgById(Long id);
}
