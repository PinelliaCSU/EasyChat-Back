package com.easychat.mappers;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
/**
 * @author Pinellia
 * @Description: 的Mapper类
 * @date: 2025/08/25
 */

@Mapper
public interface GroupInfoMapper<T,P> extends BaseMapper {
	/**
	 * 根据GroupId查询
	 */
	 T selectByGroupId(@Param("groupId") String groupId);

	/**
	 * 根据GroupId更新
	 */
	 Integer updateByGroupId(@Param("bean") T t, @Param("groupId") String groupId);

	/**
	 * 根据GroupId删除
	 */
	 Integer deleteByGroupId(@Param("groupId") String groupId);

}