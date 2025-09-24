package com.easychat.mappers;

import com.easychat.mappers.BaseMapper;
import org.apache.ibatis.annotations.Param;
/**
 * @Description app发布
 * @author Pinellia
 * @Date 2025-09-24 17:06:29
 **/
public interface AppUpdateMapper<T,P> extends BaseMapper {
	//根据Id查询
	T selectUpdateById(@Param("id") Integer id);

	//根据Id更新
	int updateUpdateById(@Param("bean") T t,@Param("id") Integer id);

	//根据Id删除
	int deleteUpdateById(@Param("id") Integer id);

}