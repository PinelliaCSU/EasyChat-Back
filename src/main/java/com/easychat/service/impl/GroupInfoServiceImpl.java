package com.easychat.service.impl;

import com.easychat.entity.config.AppConfig;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.SysSettingDto;
import com.easychat.entity.enums.ResponseCodeEnum;
import com.easychat.entity.enums.UserContactStatusEnum;
import com.easychat.entity.enums.UserContactTypeEnum;
import com.easychat.entity.po.UserContact;
import com.easychat.entity.query.GroupInfoQuery;
import com.easychat.entity.query.SimplePage;
import com.easychat.entity.po.GroupInfo;
import com.easychat.entity.query.UserContactQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.exception.BusinessException;
import com.easychat.mappers.GroupInfoMapper;
import com.easychat.mappers.UserContactMapper;
import com.easychat.redis.RedisComponent;
import com.easychat.utils.StringTools;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import com.easychat.entity.enums.PageSize;
import com.easychat.service.GroupInfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 高98
 * @Description: 对应的ServiceImpl
 * @date: 2025/08/25
 */

@Service("groupInfoService")
public class GroupInfoServiceImpl implements GroupInfoService{

	@Resource
	private GroupInfoMapper<GroupInfo,GroupInfoQuery> groupInfoMapper;

	@Resource
	private RedisComponent redisComponent;
	@Resource
	private UserContactMapper<UserContact, UserContactQuery> userContactMapper;

	@Resource
	private AppConfig appConfig;
	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<GroupInfo>findListByParam(GroupInfoQuery query){
		return this.groupInfoMapper.selectList(query);
	 }
	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(GroupInfoQuery query){
		return this.groupInfoMapper.selectCount(query);
	 }
	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<GroupInfo> findListByPage(GroupInfoQuery query ){
		Integer count = this.findCountByParam(query); 
		Integer pageSize=query.getPageSize()==null? PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page=new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<GroupInfo> list = this.findListByParam(query);
		PaginationResultVO<GroupInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	 }
	/**
	 * 新增
	 */
	@Override
	public Integer add(GroupInfo bean){
		return this.groupInfoMapper.insert(bean);
	 }
	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<GroupInfo> listBean){
		if(listBean==null || listBean.isEmpty()){
			return 0;
		}
		return this.groupInfoMapper.insertBatch(listBean);
	 }
	/**
	 * 新增或者修改
	 */
	@Override
	public Integer addOrUpdate(GroupInfo groupInfo){
		return this.groupInfoMapper.insertOrUpdate(groupInfo);
	 }
	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<GroupInfo> listBean){
		if(listBean==null || listBean.isEmpty()){
			return 0;
		}
		return this.groupInfoMapper.insertOrUpdateBatch(listBean);
	 }
	/**
	 * 根据GroupId查询
	 */
	@Override
	 public GroupInfo getByGroupId(String groupId){
		return this.groupInfoMapper.selectByGroupId(groupId);
	 }
	/**
	 * 根据GroupId更新
	 */
	@Override
	 public Integer updateByGroupId(GroupInfo bean , String groupId){
		return this.groupInfoMapper.updateByGroupId(bean,groupId);
	 }
	/**
	 * 根据GroupId删除
	 */
	@Override
	 public Integer deleteByGroupId(String groupId){
		return this.groupInfoMapper.deleteByGroupId(groupId);
	 }

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveGroup(GroupInfo groupInfo, MultipartFile avatarFile, MultipartFile avatarCover) throws BusinessException, IOException {
		Date curDate = new Date();

		if(StringTools.isEmpty(groupInfo.getGroupId())){
			GroupInfoQuery query=new GroupInfoQuery();
			query.setGroupOwnerId(groupInfo.getGroupId());
			//对于群组的个数进行判断
			Integer count = this.groupInfoMapper.selectCount(query);
			SysSettingDto sysSettingDto = redisComponent.getSysSetting();
			if(count >= sysSettingDto.getMaxGroupCount()){
				throw new BusinessException("最多只能支持创建" + sysSettingDto.getMaxGroupCount() + "个群聊!");
			}
			//头像不能为空
			if(avatarFile == null){
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}

			groupInfo.setCreateTime(curDate);
			groupInfo.setGroupId(StringTools.getGroupId());
			this.groupInfoMapper.insert(groupInfo);
			//将群组添加为联系人
			UserContact userContact = new UserContact();
			userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus());
			userContact.setContactType(UserContactTypeEnum.GROUP.getType());
			userContact.setContactId(groupInfo.getGroupId());
			userContact.setUserId(groupInfo.getGroupOwnerId());
			userContact.setCreateTime(curDate);
			userContact.setLastUpdateTime(curDate);
			this.userContactMapper.insert(userContact);

			//TODO 创建会话
			//TODO 发送消息（欢迎消息）

		}else{
			GroupInfo dbInfo = this.groupInfoMapper.selectByGroupId(groupInfo.getGroupId());
			if(!dbInfo.getGroupOwnerId().equals(groupInfo.getGroupOwnerId())){//必须要判断群组是否属于同一个人，防止有人直接走接口
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}
			this.groupInfoMapper.updateByGroupId(groupInfo,groupInfo.getGroupId());
			//TODO 更新相关表冗余信息


			//TODO 修改群昵称，发送ws消息，为了实时更新


		}
		if(avatarFile == null){
			return;
		}
		String baseFolder = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
		File targetFileFolder = new File(baseFolder + Constants.FILE_FOLDER_AVATAR_NAME);
		if(!targetFileFolder.exists()){
			targetFileFolder.mkdirs();
		}
		String filename = targetFileFolder.getPath() + "/" + groupInfo.getGroupId() + Constants.IMAGE_SUFFIX;
		avatarFile.transferTo(new File(filename));
		avatarCover.transferTo(new File(filename + Constants.COVER_IMAGE_SUFFIX));
	}

}