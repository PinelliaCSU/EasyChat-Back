package com.easychat.utils;

import com.easychat.entity.constants.Constants;
import com.easychat.entity.enums.UserContactTypeEnum;
import com.easychat.exception.BusinessException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class StringTools {


    public static void checkParam(Object param) throws BusinessException {
        try {
            Field[] fields = param.getClass().getDeclaredFields();
            boolean notEmpty = false;
            for (Field field : fields) {
                String methodName = "get" + StringTools.upperCaseFirstLetter(field.getName());
                Method method = param.getClass().getMethod(methodName);
                Object object = method.invoke(param);
                if (object != null && object instanceof String && !isEmpty(object.toString())) {
                    notEmpty = true;
                    break;
                }
            }
            if (!notEmpty) {
                throw new BusinessException("多参数更新，删除，必须有非空条件");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("校验参数是否为空失败");
        }
    }


    public static String upperCaseFirstLetter(String field) {
        if (isEmpty(field)) {
            return field;
        }
        if (field.length() > 1 && Character.isUpperCase(field.charAt(1))) {
            return field;
        }
        return field.substring(0, 1).toUpperCase() + field.substring(1);
    }

    public static boolean isEmpty(String str) {
        if(null == str || "".equals(str) || "null".equals(str) || "\u0000".equals(str)) {
            return true;
        }else if ("".equals(str.trim())) {
            return true;
        }
        return false;
    }


    public static String getUserId(){
        return UserContactTypeEnum.USER.getPrefix() + getRandomNumber(Constants.USERID_LENGTH);//为了做出区分，UerId前面加上U，USER枚举的前缀就是U
    }

    public static String getGroupId(){
        return UserContactTypeEnum.GROUP.getPrefix() + getRandomNumber(Constants.GROUP_ID_LENGTH);//为了做出区分，UerId前面加上U，USER枚举的前缀就是U
    }


    public static final String getRandomNumber(Integer count){
        return RandomStringUtils.random(count, false, true);
    }


    public static final String getRandomString(Integer count){
        return RandomStringUtils.random(count, true, true);
    }

    //对密码进行md5加密
    public static final String encodeMd5(String originStr){
        return StringTools.isEmpty(originStr) ? "" : DigestUtils.md5Hex(originStr);
    }
}
