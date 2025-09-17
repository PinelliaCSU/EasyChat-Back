package com.easychat.mappers;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
/**
 * @author 高98
 * @Description: 联系人的Mapper类
 * @date: 2025/08/25
 */

@Mapper
public interface UserContactMapper<T,P> extends BaseMapper {
	/**
	 * 根据UserIdAndContactId查询
	 */
	 T selectByUserIdAndContactId(@Param("userId") String userId,@Param("contactId") String contactId);

	/**
	 * 根据UserIdAndContactId更新
	 */
	 Integer updateByUserIdAndContactId(@Param("bean") T t, @Param("userId") String userId,@Param("contactId") String contactId);

	/**
	 * 根据UserIdAndContactId删除
	 */
	 Integer deleteByUserIdAndContactId(@Param("userId") String userId,@Param("contactId") String contactId);

}