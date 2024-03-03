package com.vnnet.kpi.console.controller;

import com.vnnet.kpi.console.utils.MySecurityUtils;
import com.vnnet.kpi.web.bean.HttpResult;
import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.config.Constants;
import com.vnnet.kpi.web.model.SysMenu;
import com.vnnet.kpi.web.model.SysRole;
import com.vnnet.kpi.web.model.SysRoleMenu;
import com.vnnet.kpi.web.persistence.SysRoleMapper;
import com.vnnet.kpi.web.service.SysRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.awt.peer.PanelPeer;
import java.util.List;


@RestController
@RequestMapping("role")
public class SysRoleController {

    private static final Logger logger = LoggerFactory.getLogger(com.vnnet.kpi.console.controller.SysRoleController.class);

    @Autowired
    private SysRoleService sysRoleService;

    @PreAuthorize("hasAuthority('sys:role:add') AND hasAuthority('sys:role:edit')")
    @PostMapping(value = "/save")
    public HttpResult save(@RequestBody SysRole record) {
        SysRole role = sysRoleService.findById(record.getId());
        if (role != null) {
            if (Constants.ADMIN.equalsIgnoreCase(role.getName())) {
                return HttpResult.error("Super administrator does not allow changes");
            }
        }
        if ((record.getId() == null || record.getId() == 0) && !sysRoleService.findByName(record.getName()).isEmpty()) {
            return HttpResult.error("Role name already exists!");
        }
        return HttpResult.ok(sysRoleService.save(record));
    }

    @PreAuthorize("hasAuthority('sys:role:delete')")
    @PostMapping(value = "/delete")
    public HttpResult delete(@RequestBody List<SysRole> records) {
        return HttpResult.ok(sysRoleService.delete(records));
    }

    @PreAuthorize("hasAuthority('sys:role:view')")
    @PostMapping(value = "/findPage")
    public HttpResult findPage(@RequestBody PageRequest pageRequest) {
        String userName = MySecurityUtils.getUsername();
        return HttpResult.ok(sysRoleService.findPage(pageRequest, userName));
    }

    @GetMapping(value = "/findAll")
    public HttpResult findAll() {
        String userName = MySecurityUtils.getUsername();
        if (userName == null || userName.isEmpty())
            return HttpResult.error(403, "access denied");
        return HttpResult.ok(sysRoleService.findAll(userName));
    }

    @PreAuthorize("hasAuthority('sys:role:view')")
    @GetMapping(value = "/findRoleMenus")
    public HttpResult findRoleMenus(@RequestParam Long roleId) {
        return HttpResult.ok(sysRoleService.findRoleMenus(roleId));
    }

    @PreAuthorize("hasAuthority('sys:role:view')")
    @PostMapping(value = "/saveRoleMenus")
    public HttpResult saveRoleMenus(@RequestBody List<SysRoleMenu> records) {
        return HttpResult.ok(sysRoleService.saveRoleMenus(records));
    }
}
