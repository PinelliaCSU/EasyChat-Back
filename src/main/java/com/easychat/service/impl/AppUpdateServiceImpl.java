package com.easychat.service.impl;

import com.easychat.entity.enums.PageSize;
import com.easychat.entity.po.AppUpdate;
import com.easychat.entity.query.SimplePage;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.mappers.AppUpdateMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import com.easychat.entity.query.AppUpdateQuery;

import javax.annotation.Resource;/**
 * @Description app发布Service
 * @author 赵默笙
 * @Date 2025-09-24 17:06:29
 **/
@Service("updateService")
public class AppUpdateServiceImpl implements com.easyjava.service.AppUpdateService {
	@Resource
	private AppUpdateMapper<AppUpdate, AppUpdateQuery> appUpdateMapper;
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
	public int deleteUpdateById(Integer id){
		return this.appUpdateMapper.deleteUpdateById(id);
	}

}