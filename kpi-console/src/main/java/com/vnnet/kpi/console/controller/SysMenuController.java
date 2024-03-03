package com.vnnet.kpi.console.controller;

import com.vnnet.kpi.console.utils.MySecurityUtils;
import com.vnnet.kpi.web.bean.HttpResult;
import com.vnnet.kpi.web.model.SysMenu;
import com.vnnet.kpi.web.service.SysMenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.Mergeable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("menu")
public class SysMenuController {

    private static final Logger logger = LoggerFactory.getLogger(com.vnnet.kpi.console.controller.SysMenuController.class);


    @Autowired
    private SysMenuService sysMenuService;

    @PreAuthorize("hasAuthority('sys:menu:add') AND hasAuthority('sys:menu:edit')")
    @PostMapping(value = "/save")
    public HttpResult save(@RequestBody SysMenu record) {
        return HttpResult.ok(sysMenuService.save(record));
    }

    @PreAuthorize("hasAuthority('sys:menu:delete')")
    @PostMapping(value = "/delete")
    public HttpResult delete(@RequestBody List<SysMenu> records) {
        return HttpResult.ok(sysMenuService.delete(records));
    }

    //	@PreAuthorize("hasAuthority('sys:menu:view')")
    @GetMapping(value = "/findNavTree")
    public HttpResult findNavTree(@RequestParam String userName) {
        String user_name = MySecurityUtils.getUsername();
        if (user_name == null || user_name.isEmpty())
            return HttpResult.error(403, "access denied");
        List<SysMenu> sysMenus = sysMenuService.findTree(userName, 1);
        return HttpResult.ok(sysMenus);
    }

    //	@PreAuthorize("hasAuthority('sys:menu:view')")
    @GetMapping(value = "/findMenuUserTree")
    public HttpResult findMenuUserTree(@RequestParam String userName) {
        String user_name = MySecurityUtils.getUsername();
        if (user_name == null || user_name.isEmpty())
            return HttpResult.error(403, "access denied");
        return HttpResult.ok(sysMenuService.findTree(userName, 0));
    }

    //	@PreAuthorize("hasAuthority('sys:menu:view')")
    @GetMapping(value = "/findMenuTree")
    public HttpResult findMenuTree() {
        String user_name = MySecurityUtils.getUsername();
        if (user_name == null || user_name.isEmpty())
            return HttpResult.error(403, "access denied");
        return HttpResult.ok(sysMenuService.findTree(null, 0));
    }


}
