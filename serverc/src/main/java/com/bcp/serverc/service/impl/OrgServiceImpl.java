package com.bcp.serverc.service.impl;

import com.bcp.serverc.mapper.OrgMapper;
import com.bcp.serverc.model.Org;
import com.bcp.serverc.service.OrgService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

@Service
public class OrgServiceImpl implements OrgService {
    @Resource
    private OrgMapper orgMapper;

    @Override
    public boolean add(Org org) {
        return orgMapper.insert(org) == 1;
    }

    @Override
    public Org update(Org org) {
        if (orgMapper.updateByPrimaryKey(org) == 1) {
            return orgMapper.selectByPrimaryKey(org);
        } else {
            return null;
        }
    }

    @Override
    public List<Org> list(Org org) {
        return orgMapper.selectByConditions(org);
    }
}
