package com.easychat.entity.enums;

public enum GroupStatusEnum {
    NORMAL(0,"正常"),
    DISSOLUTION(1,"解散");

    private Integer status;
    private String desc;

    GroupStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public static GroupStatusEnum getByStatus(Integer status) {
        for (GroupStatusEnum groupStatusEnum : GroupStatusEnum.values()) {
            if(groupStatusEnum.getStatus().equals(status)) {
                return groupStatusEnum;
            }
        }
        return null;
    }

}
