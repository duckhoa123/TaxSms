package com.vnnet.kpi.console.controller;


import com.vnnet.kpi.console.security.JwtAuthenticatioToken;
import com.vnnet.kpi.console.utils.JwtTokenUtils;
import com.vnnet.kpi.console.utils.MySecurityUtils;
import com.vnnet.kpi.web.bean.HttpResult;
import com.vnnet.kpi.web.bean.LoginBean;
import com.vnnet.kpi.web.model.SysLog;
import com.vnnet.kpi.web.model.SysLogInfo;
import com.vnnet.kpi.web.model.SysUser;

import com.vnnet.kpi.web.persistence.SysUserMapper;
import com.vnnet.kpi.web.service.SysLogInfoService;
import com.vnnet.kpi.web.service.SysLogService;
import com.vnnet.kpi.web.service.SysUserService;
import com.vnnet.kpi.web.utils.HttpUtils;
import com.vnnet.kpi.web.utils.IPUtils;
import com.vnnet.kpi.web.utils.PasswordUtils;
import com.vnnet.kpi.web.utils.StringUtils;
import org.apache.xmlbeans.impl.store.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Hashtable;


@RestController
public class SysLoginController {

    private static final Logger logger = LoggerFactory.getLogger(SysLoginController.class);

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysLogInfoService sysLogInfoService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping(value = "/login")
    public HttpResult login(@RequestBody LoginBean loginBean, HttpServletRequest request) throws IOException, NamingException {
        try {
            String username = loginBean.getAccount();
            String password = loginBean.getPassword();
            SysUser user = sysUserService.findByNameLogin(username);

            if (user == null) {
                return HttpResult.error("Account does not exist");
            }

            if (!PasswordUtils.matches(user.getSalt(), password, user.getPassword())) {
                return HttpResult.error("Incorrect password!");
            }
            JwtAuthenticatioToken token = MySecurityUtils.login(request, user.getName(), password, authenticationManager);
            user.setToken(token.getToken());
            user.setSalt("");
            user.setPassword("");
//            sysLogInfoService.save("Đăng nhập hệ thống", user.getName());

            return HttpResult.ok(user);
        } catch (Exception ex) {
            ex.printStackTrace();
            return HttpResult.error("An error occurred. Please try again later");
        }
    }

    @PostMapping(value = "/getLoginUser")
    public HttpResult getLoginUser(HttpServletRequest request) throws IOException {
        try {
            String username = MySecurityUtils.getUsername();
            if (username == null || username.isEmpty())
                return HttpResult.error(403, "access denied");

            SysUser user = sysUserService.findByName(username);
            if (user == null) {
                return HttpResult.error("msg.accountNotExist");
            }
            return HttpResult.ok(user);
        } catch (Exception ex) {
            ex.printStackTrace();
            return HttpResult.error("An error occurred. Please try again later");
        }
    }


}
