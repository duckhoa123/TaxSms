package com.vnnet.kpi.web.model;

import java.util.List;

public class BaseMenu extends BaseModel {
    private Long ab;
    private String parentName;
    private Integer level;
    private List<SysMenu> children;

    private Long objectIdCRM;
    private Long parentIdCRM;
    private String objectTypeCRM;
    private String nameCRM;
    private String descriptionCRM;
    private int mucUuTienCRM;
    private int statusCRM;
    private int typeCRM;

    public Long getObjectIdCRM() {
        return objectIdCRM;
    }

    public void setObjectIdCRM(Long objectIdCRM) {
        this.objectIdCRM = objectIdCRM;
    }

    public Long getParentIdCRM() {
        return parentIdCRM;
    }

    public void setParentIdCRM(Long parentIdCRM) {
        this.parentIdCRM = parentIdCRM;
    }

    public String getObjectTypeCRM() {
        return objectTypeCRM;
    }

    public void setObjectTypeCRM(String objectTypeCRM) {
        this.objectTypeCRM = objectTypeCRM;
    }

    public String getNameCRM() {
        return nameCRM;
    }

    public void setNameCRM(String nameCRM) {
        this.nameCRM = nameCRM;
    }

    public String getDescriptionCRM() {
        return descriptionCRM;
    }

    public void setDescriptionCRM(String descriptionCRM) {
        this.descriptionCRM = descriptionCRM;
    }

    public int getMucUuTienCRM() {
        return mucUuTienCRM;
    }

    public void setMucUuTienCRM(int mucUuTienCRM) {
        this.mucUuTienCRM = mucUuTienCRM;
    }

    public int getStatusCRM() {
        return statusCRM;
    }

    public void setStatusCRM(int statusCRM) {
        this.statusCRM = statusCRM;
    }

    public int getTypeCRM() {
        return typeCRM;
    }

    public void setTypeCRM(int typeCRM) {
        this.typeCRM = typeCRM;
    }

    public Long getAb() {
        return ab;
    }

    public void setAb(Long ab) {
        this.ab = ab;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<SysMenu> getChildren() {
        return children;
    }

    public void setChildren(List<SysMenu> children) {
        this.children = children;
    }
}
