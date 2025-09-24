package com.easychat.entity.enums;

public enum AppUpdateFileTypeEnum {
    LOCAL(0,""),
    OUTER_LINK(1,"");

    private Integer type;
    private String description;

    AppUpdateFileTypeEnum(Integer type, String description) {
        this.type = type;
        this.description = description;
    }

    public Integer getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public static AppUpdateFileTypeEnum getByType(Integer type) {
        for(AppUpdateFileTypeEnum typeEnum : AppUpdateFileTypeEnum.values()) {
            if(typeEnum.getType().equals(type)) {
                return typeEnum;
            }
        }
        return null;
    }
}
