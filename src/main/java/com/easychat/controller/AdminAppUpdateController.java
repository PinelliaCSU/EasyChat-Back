package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.po.AppUpdate;
import com.easychat.entity.query.AppUpdateQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.exception.BusinessException;
import com.easychat.service.AppUpdateService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@RestController("adminAppUpdateController")
@RequestMapping("/admin")
public class AdminAppUpdateController extends ABaseController{
    @Resource
    private AppUpdateService appUpdateService;

    @RequestMapping("/loadUpdateList")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO loadUpdateList(AppUpdateQuery query){
        query.setOrderBy("id desc");
        PaginationResultVO resultVO = appUpdateService.findListByPage(query);
        return getSuccessResponseVo(resultVO);
    }

    @RequestMapping("/saveUpdate")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO saveUpdate(Integer id, @NotEmpty String version, @NotEmpty String updateDesc, @NotNull Integer fileType , String outerLink, MultipartFile file) throws BusinessException, IOException {

        AppUpdate appUpdate = new AppUpdate();
        appUpdate.setId(id);
        appUpdate.setVersion(version);
        appUpdate.setUpdateDesc(updateDesc);
        appUpdate.setFileType(fileType);
        appUpdate.setOuterLink(outerLink);
        appUpdateService.saveUpdate(appUpdate,file);//文件已经到了服务器
        return getSuccessResponseVo(null);
    }
}
