package com.easychat.service.impl;

import com.easychat.entity.config.AppConfig;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.enums.AppUpdateFileTypeEnum;
import com.easychat.entity.enums.AppUpdateStatusEnum;
import com.easychat.entity.enums.PageSize;
import com.easychat.entity.enums.ResponseCodeEnum;
import com.easychat.entity.po.AppUpdate;
import com.easychat.entity.query.SimplePage;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.exception.BusinessException;
import com.easychat.mappers.AppUpdateMapper;
import com.easychat.service.AppUpdateService;
import com.easychat.utils.StringTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.easychat.entity.query.AppUpdateQuery;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;


/**
 * @Description app发布Service
 * @author Pinellia
 * @Date 2025-09-24 17:06:29
 **/
@Service("updateService")
public class AppUpdateServiceImpl implements AppUpdateService {
	@Resource
	private AppUpdateMapper<AppUpdate, AppUpdateQuery> appUpdateMapper;
	@Resource
	private AppConfig appConfig;

	//根据条件查询列表
	public List<AppUpdate> findListByParam(AppUpdateQuery param){
		return this.appUpdateMapper.selectList(param);
	}

	//根据条件查询数量
	public int findCountByParam(AppUpdateQuery query){
		return this.appUpdateMapper.selectCount(query);
	}

	//分页查询
	public PaginationResultVO<AppUpdate> findListByPage(AppUpdateQuery param){
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize()==null? PageSize.SIZE15.getSize():param.getPageSize();
		SimplePage page = new SimplePage(param.getPageNo(),count,pageSize);
		param.setSimplePage(page);
		List<AppUpdate> list = this.findListByParam(param);
		PaginationResultVO<AppUpdate> result = new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),page.getPageTotal(),list);
		return result;
	}

	//新增
	public int add(AppUpdate bean){
		return this.appUpdateMapper.insert(bean);
	}

	//批量新增
	public int addBatch(List<AppUpdate> listBean){
		if(listBean == null || listBean.isEmpty()){
			return 0;
		}
		return this.appUpdateMapper.insertBatch(listBean);
	}

	//批量新增或修改
	public int addOrUpdateBatch(List<AppUpdate> listBean){
		if(listBean == null || listBean.isEmpty()){
			return 0;
		}
		return this.appUpdateMapper.insertOrUpdateBatch(listBean);
	}

	//根据Id查询
	public AppUpdate getUpdateById(Integer id){
		return this.appUpdateMapper.selectUpdateById(id);
	}

	//根据Id更新
	public int updateUpdateById(AppUpdate bean, Integer id){
		return this.appUpdateMapper.updateUpdateById(bean,id);
	}
	//根据Id删除
	public int deleteUpdateById(Integer id) throws BusinessException {
		AppUpdate dbInfo = this.getUpdateById(id);
		if(!AppUpdateStatusEnum.INIT.getStatus().equals(dbInfo.getStatus())){
			throw new BusinessException(ResponseCodeEnum.CODE_600);//如果是灰度发布的状态，不让它更改
		}//防止绕开前端
		return this.appUpdateMapper.deleteUpdateById(id);
	}

	//根据Version查询
	public AppUpdate getUpdateByVersion(String version){
		return this.appUpdateMapper.selectUpdateByVersion(version);
	}

	//根据Version更新
	public int updateUpdateByVersion(AppUpdate bean, String version){
		return this.appUpdateMapper.updateUpdateByVersion(bean,version);
	}

	//根据Version删除
	public int deleteUpdateByVersion(String version){
		return this.appUpdateMapper.deleteUpdateByVersion(version);
	}

	@Override
	public void saveUpdate(AppUpdate appUpdate, MultipartFile file) throws BusinessException, IOException {
		AppUpdateFileTypeEnum fileTypeEnum = AppUpdateFileTypeEnum.getByType(appUpdate.getFileType());
		if(fileTypeEnum == null){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		if(appUpdate.getId() != null){
			AppUpdate dbInfo = this.getUpdateById(appUpdate.getId());
			if(!AppUpdateStatusEnum.INIT.getStatus().equals(dbInfo.getStatus())){
				throw new BusinessException(ResponseCodeEnum.CODE_600);//如果是灰度发布的状态，不让它更改
			}
		}

		AppUpdateQuery updateQuery = new AppUpdateQuery();
		updateQuery.setOrderBy("id desc");
		updateQuery.setSimplePage(new SimplePage(0,1));
		List<AppUpdate> appUpdateList = appUpdateMapper.selectList(updateQuery);
		if(!appUpdateList.isEmpty()){
			AppUpdate lastestAppUpdate = appUpdateList.get(0);

			Long dbVersion = Long.parseLong(lastestAppUpdate.getVersion().replace(".",""));
			Long currentVersion = Long.parseLong(appUpdate.getVersion().replace(".",""));

			if(appUpdate.getId() == null && currentVersion <= dbVersion){
				throw new BusinessException("当前版本必须大于历史版本");
			}
			//目的是确保一定不能修改的比当前版本更高
			if(appUpdate.getId() != null && currentVersion >= dbVersion && !appUpdate.getId().equals(lastestAppUpdate.getId())){
				throw  new BusinessException("当前版本必须大于历史版本");
			}

			AppUpdate versionDb  = appUpdateMapper.selectUpdateByVersion(appUpdate.getVersion());
			if(appUpdate.getId() != null && versionDb != null && appUpdate.getId().equals(appUpdate.getId())){
				throw new BusinessException("版本号已经存在");
			}
		}

		if(appUpdate.getId() == null){
			appUpdate.setCreateTime(new Date());
			appUpdate.setStatus(AppUpdateStatusEnum.INIT.getStatus());
			appUpdateMapper.insert(appUpdate);
		}else{
			appUpdateMapper.updateUpdateById(appUpdate,appUpdate.getId());
		}
		if(file != null){
			File folder = new File(appConfig.getProjectFolder() + Constants.APP_UPDATE_FOLDER);
			if(!folder.exists()){
				folder.mkdirs();
			}
			file.transferTo(new File(folder.getAbsoluteFile() + "/" + appUpdate.getId() + Constants.APP_EXE_SUFFIX));
		}
	}

	@Override
	public void postUpdate(Integer id, Integer status, String grayscaleUid) throws BusinessException {
		AppUpdateStatusEnum statusEnum = AppUpdateStatusEnum.getByStatus(status);
		if(statusEnum == null){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		if(AppUpdateStatusEnum.GRAYSCALE == statusEnum && StringTools.isEmpty(grayscaleUid)){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}

		if(AppUpdateStatusEnum.GRAYSCALE != statusEnum){
			grayscaleUid = "";
		}
		AppUpdate appUpdate = new AppUpdate();
		appUpdate.setStatus(status);
		appUpdate.setGrayscaleUid(grayscaleUid);
		appUpdateMapper.updateUpdateById(appUpdate,id);
	}

	@Override
	public AppUpdate getLatestUpdate(String appVersion, String uid) {
		return appUpdateMapper.selectLatestUpdate(appVersion, uid);
	}

}