package com.easychat.entity.constants;

import com.easychat.entity.enums.UserContactTypeEnum;

public class Constants {

    public static final  Integer REDIS_KEY_EXPIRES_HEART_BEAT = 6;

    public static final String REDIS_KEY_CHECK_CODE = "easychat:checkCode:";

    public static final Integer REDIS_TIME_1MIN = 60;

    public static final Integer REDIS_KEY_EXPIRES_DAY = REDIS_TIME_1MIN * 24 * 60;//过期时间

    public static final String REDIS_KEY_WS_USER_HEART_BEAT = "easychat:ws:user:heartbeat";

    public static final String REDIS_KEY_WS_TOKEN = "easychat:ws:token";
    public static final String REDIS_KEY_WS_TOKEN_USERID = "easychat:ws:token:user:id";
    public static final String ROBOT_UID = UserContactTypeEnum.USER.getPrefix() + "robot";
    public static final String REDIS_KEY_SYS_SETTING = "easychat:syssetting";

    //用户联系人列表
    public static  final String REDIS_KEY_USER_CONTACT = "easychat:ws:user:contact:";
    //三天以前的毫秒数
    public static final Long MillisSECOND_THREEDAYS = 3 * 24 * 60 * 60 * 1000L;

    public static final Integer USERID_LENGTH = 11;//用户ID的长度
    public static final Integer GROUP_ID_LENGTH = 11;//群组ID的长度

    public static final String FILE_FOLDER_FILE = "/file/";
    public static final String FILE_FOLDER_AVATAR_NAME = "/avatar/";

    public static final String IMAGE_SUFFIX = ".png";
    public static final String COVER_IMAGE_SUFFIX = "_cover.png";

    public static final String APPLY_INFO_TEMPLATE = "我是%s";

    public static final String REGEX_PASSWORD = "^(?=.*\\d) (?=.*[a-zA-Z]) [\\da-zA-z~!@#$%^&*_] {0,10}$";

    public static final String APP_UPDATE_FOLDER = "/app/";

    public static final String APP_EXE_SUFFIX = ".exe";

    public static final String APP_NAME = "EasyChatSetup.";
}
