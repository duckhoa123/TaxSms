package com.vnnet.kpi.web.service;

import com.vnnet.kpi.web.config.Constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonService {
    public String getGroupProgramByUserType(long groupUser) {
        switch ((int) groupUser) {
            case (int) Constants.USER_TYPE_ADMIN:
                return "ALL";
            case (int) Constants.USER_TYPE_QUAN_LY_NO:
                return "QUAN_LY_NO";
                //Debt management
            case (int) Constants.USER_TYPE_KE_KHAI:
                return "KE_KHAI";
                //Declaration
            case (int) Constants.USER_TYPE_TUYEN_TRUYEN:
                return "TUYEN_TRUYEN";
                //Propaganda
            default:
                return "";
        }
    }


    public String getGroupProgramWithRole(long roleId) {
        // Key: role id
        // Value: group program
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "ALL");
        map.put(3, "QUAN_LY_NO");
        map.put(4, "QUAN_LY_NO");
        map.put(5, "KE_KHAI");
        map.put(6, "KE_KHAI");
        map.put(7, "TUYEN_TRUYEN");
        map.put(8, "TUYEN_TRUYEN");
        map.put(9, "PHONG_HO");
        //Refuge
        map.put(10, "PHONG_HO");
        map.put(11, "VAN_PHONG");
        //Office
        map.put(12, "VAN_PHONG");

        String group = map.get((int) roleId);

        return (group == null ? "KHAC" : group);
    }

    public String getUerNameWithRole(long roleId, String userName) {
        List<Integer> roleAll = Arrays.asList(1, 3, 5, 7, 9, 11);
        if (roleAll.contains((int) roleId))
            return "";
        else
            return userName;
    }

}
