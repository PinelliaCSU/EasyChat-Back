package com.easychat.mappers;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
/**
 * @author 竝inellia
 * @Description: 聊天消息表的Mapper类
 * @date: 2025/10/12
 */

@Mapper
public interface ChatMessageMapper<T,P> extends BaseMapper {
	/**
	 * 根据MessageId查询
	 */
	 T selectByMessageId(@Param("messageId") Long messageId);

	/**
	 * 根据MessageId更新
	 */
	 Integer updateByMessageId(@Param("bean") T t, @Param("messageId") Long messageId);

	/**
	 * 根据MessageId删除
	 */
	 Integer deleteByMessageId(@Param("messageId") Long messageId);

}