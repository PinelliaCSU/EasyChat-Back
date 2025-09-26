package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.config.AppConfig;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.enums.AppUpdateFileTypeEnum;
import com.easychat.entity.po.AppUpdate;
import com.easychat.entity.vo.AppUpdateVO;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.service.AppUpdateService;
import com.easychat.utils.CopyTools;
import com.easychat.utils.StringTools;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;


@RestController("updateController")
@RequestMapping("/update")
public class UpdateController extends ABaseController{
    @Resource
    private AppUpdateService appUpdateService;
    @Resource
    private AppConfig appConfig;


    @RequestMapping("/checkVersion")
    @GlobalInterceptor
    public ResponseVO checkVersion(String appVersion,String uid) {
        if(StringTools.isEmpty(appVersion)) {
            return getSuccessResponseVo(null);
        }
        AppUpdate appUpdate = appUpdateService.getLatestUpdate(appVersion,uid);
        if(appUpdate == null) {
            return getSuccessResponseVo(null);
        }
        AppUpdateVO appUpdateVO = CopyTools.copy(appUpdate, AppUpdateVO.class);
        if(AppUpdateFileTypeEnum.LOCAL.getType().equals(appUpdate.getFileType())){
            File file = new File(appConfig.getProjectFolder() + Constants.APP_UPDATE_FOLDER + appUpdate.getId() + Constants.APP_EXE_SUFFIX);
            appUpdateVO.setSize(file.length());
        }else{
            appUpdateVO.setSize(0L);
        }
        appUpdateVO.setUpdateList(Arrays.asList(appUpdate.getUpdateDescArray()));
        String fileName = Constants.APP_NAME  +appUpdate.getVersion()+Constants.APP_EXE_SUFFIX;
        appUpdateVO.setFileName(fileName);
        return getSuccessResponseVo(appUpdateVO);
    }
}
