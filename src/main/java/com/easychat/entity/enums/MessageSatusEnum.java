package com.easychat.entity.enums;

public enum MessageSatusEnum {
    SENDING(0,""),
    SENDED(1,"");

    private Integer status;
    private String desc;

    MessageSatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static MessageSatusEnum getByStatus(Integer status) {
        for(MessageSatusEnum e : MessageSatusEnum.values()) {
            if(e.status.equals(status)) {
                return e;
            }
        }
        return null;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
