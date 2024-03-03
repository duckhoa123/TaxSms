package com.vnnet.kpi.web.model;

public class BaseRoleMenu extends BaseModel {
    private Long groupUserIdCRM;
    private Long objectIdCRM;
    private String rightCodeCRM;
    private int accessTypeCRM;

    public Long getGroupUserIdCRM() {
        return groupUserIdCRM;
    }

    public void setGroupUserIdCRM(Long groupUserIdCRM) {
        this.groupUserIdCRM = groupUserIdCRM;
    }

    public Long getObjectIdCRM() {
        return objectIdCRM;
    }

    public void setObjectIdCRM(Long objectIdCRM) {
        this.objectIdCRM = objectIdCRM;
    }

    public String getRightCodeCRM() {
        return rightCodeCRM;
    }

    public void setRightCodeCRM(String rightCodeCRM) {
        this.rightCodeCRM = rightCodeCRM;
    }

    public int getAccessTypeCRM() {
        return accessTypeCRM;
    }

    public void setAccessTypeCRM(int accessTypeCRM) {
        this.accessTypeCRM = accessTypeCRM;
    }
}
