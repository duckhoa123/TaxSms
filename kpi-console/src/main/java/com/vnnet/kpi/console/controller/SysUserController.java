package com.vnnet.kpi.console.controller;

import com.vnnet.kpi.console.sevice.StorageService;
import com.vnnet.kpi.console.utils.MySecurityUtils;
import com.vnnet.kpi.web.bean.HttpResult;
import com.vnnet.kpi.web.bean.PageRequest;
import com.vnnet.kpi.web.config.Constants;
import com.vnnet.kpi.web.model.*;
import com.vnnet.kpi.web.persistence.SysUserMapper;
import com.vnnet.kpi.web.service.*;
import com.vnnet.kpi.web.utils.PasswordUtils;
import com.vnnet.kpi.web.utils.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/user")
public class SysUserController {
    private static final Logger logger = LoggerFactory.getLogger(com.vnnet.kpi.console.controller.SysUserController.class);


    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private StorageService storageService;


    @PreAuthorize("hasAuthority('sys:user:add') AND hasAuthority('sys:user:edit')")
    @PostMapping(value = "/save")
    public HttpResult save(@RequestPart(value = "iconFile", required = false) MultipartFile iconFile, @RequestPart("data") SysUser record) {
        SysUser user = sysUserService.findById(record.getId());
        String salt = PasswordUtils.getSalt();
        String iconFileName = "";
        if (user == null) {
            //Thêm mới
            if (sysUserService.findByNameLogin(record.getName()) != null) {
                return HttpResult.error("Username already exists!");
            }
            String password = PasswordUtils.encode(record.getPassword(), salt);
            record.setSalt(salt);
            record.setPassword(password);
            record.setDelFlag((byte) 0);
            record.setStatus((byte) 1);

            int rs = sysUserService.insert(record);

            if (rs <= 0) {
                return HttpResult.error("An error occurred! Please try again.");
            }

            return HttpResult.ok();

        } else {
            //Update
            if (!record.getName().equals(user.getName())) {
                if (sysUserService.findByNameLogin(record.getName()) != null) {
                    return HttpResult.error("Account already exists!");
                }
            }

            if (!record.getPassword().equals(user.getPassword())) {
                String password = PasswordUtils.encode(record.getPassword(), salt);
                record.setSalt(salt);
                record.setPassword(password);
            }

            int rs = sysUserService.update(record);
            if (rs <= 0)
                return HttpResult.error("Account already exists!");
            else
                return HttpResult.ok();
        }
    }


    @PostMapping(value = "/saveChange")
    public HttpResult saveChange(@RequestPart(value = "iconFile", required = false) MultipartFile iconFile, @RequestPart("data") SysUser record) {
        String userName = MySecurityUtils.getUsername();
        if (userName == null || userName.isEmpty())
            return HttpResult.error(403, "access denied");

        SysUser user = sysUserService.findById(record.getId());
        if (record.getPassword() != null) {
            String salt = PasswordUtils.getSalt();

            if (!record.getPassword().equals(user.getPassword())) {
                String password = PasswordUtils.encode(record.getPassword(), salt);
                record.setSalt(salt);
                record.setPassword(password);
            }
        }
        String iconFileName = "";
        try {
            if (iconFile != null) {
                if (record.getIconFile() != null && !record.getIconFile().isEmpty()) {
                    storageService.deleteFile(record.getIconFile());
                }
                iconFileName = "sysuser_avatar_" + record.getId() + "_" + System.currentTimeMillis() + "." + FilenameUtils.getExtension(iconFile.getOriginalFilename());

                storageService.store(iconFile, iconFileName);
            }
            if (!iconFileName.isEmpty()) {
                record.setIconFile(iconFileName);
            }
            if (sysUserService.update(record) <= 0) {
                throw new RuntimeException("Failed to update data");
            }
            return HttpResult.ok();

        } catch (Exception ex) {
            if (!iconFileName.isEmpty()) {
                storageService.deleteFile(iconFileName);
            }
            if (ex.getCause().toString().contains("ORA-00001")) {
                return HttpResult.error("Username already exists!");

            }
            return HttpResult.error("Unable to update user information. Please try again!");
        }
    }

    @PreAuthorize("hasAuthority('sys:user:add') AND hasAuthority('sys:user:edit')")
    @PostMapping(value = "/blockUser")
    public HttpResult blockUser(@RequestPart("data") SysUser record) {
        if (record.getStatus() == 1) {
            record.setStatus((byte) 0);
            record.setDelFlag((byte) 1);
        } else {
            record.setStatus((byte) 1);
            record.setDelFlag((byte) 0);
        }
        return HttpResult.ok(sysUserService.updateStatus(record));
    }

    @PreAuthorize("hasAuthority('sys:user:edit')")
    @PostMapping(value = "/changePassword")
    public HttpResult changePassword(@RequestBody SysUser record) {
        String userName = MySecurityUtils.getUsername();
        if (userName == null || userName.isEmpty())
            return HttpResult.error(403, "access denied");
        if (record.getId() == null || record.getId() == 0) {
            return HttpResult.error("User does not exist!");
        }
        if (record.getOldPassword() == null || record.getOldPassword().isEmpty()) {
            return HttpResult.error("Old password cannot be empty!");
        }
        if (record.getNewPassword() == null || record.getNewPassword().isEmpty()) {
            return HttpResult.error("New password cannot be empty!");
        }
        if (record.getConfirmPassword() == null || record.getConfirmPassword().isEmpty()) {
            return HttpResult.error("You must confirm the password!");
        }
        if (!record.getNewPassword().equals(record.getConfirmPassword())) {
            return HttpResult.error("The entered passwords do not match!");
        }

        SysUser user = sysUserService.findById(record.getId());
        if (!PasswordUtils.matches(user.getSalt(), record.getOldPassword(), user.getPassword())) {
            return HttpResult.error("Old password is incorrect!");
        }
        String salt = PasswordUtils.getSalt();
        String password = PasswordUtils.encode(record.getNewPassword(), salt);
        user.setSalt(salt);
        user.setPassword(password);
        return HttpResult.ok(sysUserService.update(user));
    }

    @PreAuthorize("hasAuthority('sys:user:delete')")
    @PostMapping(value = "/delete")
    public HttpResult delete(@RequestBody List<SysUser> records) {
        for (SysUser record : records) {
            SysUser sysUser = sysUserService.findById(record.getId());
            if (sysUser != null && Constants.ADMIN.equalsIgnoreCase(sysUser.getName())) {
                return HttpResult.error("Unable to delete Super Admin!");
            }
        }
        return HttpResult.ok(sysUserService.delete(records));
    }

    @PreAuthorize("hasAuthority('sys:user:view')")
    @GetMapping(value = "/findByName")
    public HttpResult findByUserName(@RequestParam String name) {
        return HttpResult.ok(sysUserService.findByName(name));
    }

    @GetMapping(value = "/findPermissions")
    public HttpResult findPermissions(@RequestParam String name) {
        String userName = MySecurityUtils.getUsername();
        if (userName == null || userName.isEmpty())
            return HttpResult.error(403, "access denied");
        return HttpResult.ok(sysUserService.findPermissions(name));
    }

    @PreAuthorize("hasAuthority('sys:user:view')")
    @GetMapping(value = "/findUserRoles")
    public HttpResult findUserRoles(@RequestParam Long userId) {
        return HttpResult.ok(sysUserService.findUserRoles(userId));
    }

    //    @PreAuthorize("hasAuthority('sys:user:view')")
    @PostMapping(value = "/findUserGroup")
    public HttpResult findUserGroup() {
        String userName = MySecurityUtils.getUsername();
        if (userName == null || userName.isEmpty())
            return HttpResult.error(403, "access denied");
        return HttpResult.ok(sysUserService.findUserGroup(userName));
    }

    @PreAuthorize("hasAuthority('sys:user:view')")
    @PostMapping(value = "/findPage")
    public HttpResult findPage(@RequestBody PageRequest pageRequest) {
        return HttpResult.ok(sysUserService.findPage(pageRequest));
    }

}
