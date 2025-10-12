package com.easychat.mappers;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
/**
 * @author 竝inellia
 * @Description: 会话信息的Mapper类
 * @date: 2025/10/12
 */

@Mapper
public interface ChatSessionMapper<T,P> extends BaseMapper {
	/**
	 * 根据SessionId查询
	 */
	 T selectBySessionId(@Param("sessionId") String sessionId);

	/**
	 * 根据SessionId更新
	 */
	 Integer updateBySessionId(@Param("bean") T t, @Param("sessionId") String sessionId);

	/**
	 * 根据SessionId删除
	 */
	 Integer deleteBySessionId(@Param("sessionId") String sessionId);

}