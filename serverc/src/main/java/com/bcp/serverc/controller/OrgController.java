package com.bcp.serverc.controller;

import com.bcp.general.util.JsonResult;
import com.bcp.serverc.model.Org;
import com.bcp.serverc.service.OrgService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/org")
public class OrgController {
    @Resource
    private OrgService orgService;

    /**
     * 添加机构
     * @param org 机构信息
     * @return
     */
    @PostMapping("/add")
    public JsonResult add(@Valid @RequestBody Org org) {
        boolean result = orgService.add(org);
        return result ? JsonResult.ok() : JsonResult.errorMsg("机构添加失败");
    }

    /**
     * 更新机构信息
     * @param org 机构信息
     * @return
     */
    @PutMapping("/update")
    public JsonResult update(@RequestBody Org org) {
        Org result = orgService.update(org);
        return result == null ? JsonResult.errorMsg("机构信息更新失败") : JsonResult.ok(result);
    }

    @PostMapping("/list")
    public JsonResult list(@RequestBody(required = false) Org org) {
        List<Org> result = orgService.list(org);
        return JsonResult.ok(result);
    }

    @GetMapping("/getOrgById")
    public JsonResult getOrgById(@RequestParam("id") Long id) {
        Org result = orgService.getOrgById(id);
        return JsonResult.ok(result);
    }
}
