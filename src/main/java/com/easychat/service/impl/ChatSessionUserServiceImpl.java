package com.easychat.service.impl;

import com.easychat.entity.query.ChatSessionUserQuery;
import com.easychat.entity.query.SimplePage;
import com.easychat.entity.po.ChatSessionUser;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.mappers.ChatSessionUserMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import javax.annotation.Resource;
import com.easychat.entity.enums.PageSize;
import com.easychat.service.ChatSessionUserService;
/**
 * @author 竝inellia
 * @Description: 会话用户对应的ServiceImpl
 * @date: 2025/10/12
 */

@Service("chatSessionUserService")
public class ChatSessionUserServiceImpl implements ChatSessionUserService{

	@Resource
	private ChatSessionUserMapper<ChatSessionUser,ChatSessionUserQuery> chatSessionUserMapper;
	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<ChatSessionUser>findListByParam(ChatSessionUserQuery query){
		return this.chatSessionUserMapper.selectList(query);
	 }
	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(ChatSessionUserQuery query){
		return this.chatSessionUserMapper.selectCount(query);
	 }
	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<ChatSessionUser> findListByPage(ChatSessionUserQuery query ){
		Integer count = this.findCountByParam(query); 
		Integer pageSize=query.getPageSize()==null? PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page=new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<ChatSessionUser> list = this.findListByParam(query);
		PaginationResultVO<ChatSessionUser> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	 }
	/**
	 * 新增
	 */
	@Override
	public Integer add(ChatSessionUser bean){
		return this.chatSessionUserMapper.insert(bean);
	 }
	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<ChatSessionUser> listBean){
		if(listBean==null || listBean.isEmpty()){
			return 0;
		}
		return this.chatSessionUserMapper.insertBatch(listBean);
	 }
	/**
	 * 新增或者修改
	 */
	@Override
	public Integer addOrUpdate(ChatSessionUser chatSessionUser){
		return this.chatSessionUserMapper.insertOrUpdate(chatSessionUser);
	 }
	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<ChatSessionUser> listBean){
		if(listBean==null || listBean.isEmpty()){
			return 0;
		}
		return this.chatSessionUserMapper.insertOrUpdateBatch(listBean);
	 }
	/**
	 * 根据UserIdAndContactId查询
	 */
	@Override
	 public ChatSessionUser getByUserIdAndContactId(String userId,String contactId){
		return this.chatSessionUserMapper.selectByUserIdAndContactId(userId,contactId);
	 }
	/**
	 * 根据UserIdAndContactId更新
	 */
	@Override
	 public Integer updateByUserIdAndContactId(ChatSessionUser bean , String userId,String contactId){
		return this.chatSessionUserMapper.updateByUserIdAndContactId(bean,userId,contactId);
	 }
	/**
	 * 根据UserIdAndContactId删除
	 */
	@Override
	 public Integer deleteByUserIdAndContactId(String userId,String contactId){
		return this.chatSessionUserMapper.deleteByUserIdAndContactId(userId,contactId);
	 }

}