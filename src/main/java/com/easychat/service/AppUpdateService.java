package com.easyjava.service;

import java.util.List;
import com.easychat.entity.query.AppUpdateQuery;
import com.easychat.entity.po.AppUpdate;
import com.easychat.entity.vo.PaginationResultVO;

/**
 * @Description app发布Service
 * @author 赵默笙
 * @Date 2025-09-24 17:06:29
 **/
public interface AppUpdateService {
	//根据条件查询列表
	List<AppUpdate> findListByParam(AppUpdateQuery param);

	//根据条件查询数量
	int findCountByParam(AppUpdateQuery param);

	//分页查询
	PaginationResultVO<AppUpdate> findListByPage(AppUpdateQuery param);

	//新增
	int add(AppUpdate bean);

	//批量新增
	int addBatch(List<AppUpdate> listBean);

	//批量新增或修改
	int addOrUpdateBatch(List<AppUpdate> listBean);

	//根据Id查询
	AppUpdate getUpdateById(Integer id);

	//根据Id更新
	int updateUpdateById(AppUpdate bean, Integer id);

	//根据Id删除
	int deleteUpdateById(Integer id);

}