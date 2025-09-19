package com.easychat.service.impl;

import com.easychat.entity.config.AppConfig;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.enums.*;
import com.easychat.entity.po.UserInfoBeauty;
import com.easychat.entity.query.UserInfoQuery;
import com.easychat.entity.query.SimplePage;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.entity.vo.UserInfoVO;
import com.easychat.exception.BusinessException;
import com.easychat.mappers.UserInfoBeautyMapper;
import com.easychat.mappers.UserInfoMapper;
import com.easychat.redis.RedisComponent;
import com.easychat.utils.CopyTools;
import com.easychat.utils.StringTools;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.annotation.Resource;

import com.easychat.service.UserInfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 高98
 * @Description: 用户信息对应的ServiceImpl
 * @date: 2025/07/23
 */

@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService{

	@Resource
	private UserInfoMapper<UserInfo,UserInfoQuery> userInfoMapper;
	@Resource
	private UserInfoBeautyMapper<UserInfoBeauty,UserInfoQuery> userInfoBeautyMapper;
	@Resource
	private AppConfig appConfig;
    @Autowired
    private RedisComponent redisComponent;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserInfo>findListByParam(UserInfoQuery query){
		return this.userInfoMapper.selectList(query);
	 }
	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(UserInfoQuery query){
		return this.userInfoMapper.selectCount(query);
	 }
	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<UserInfo> findListByPage(UserInfoQuery query ){
		Integer count = this.findCountByParam(query); 
		Integer pageSize=query.getPageSize()==null? PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page=new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<UserInfo> list = this.findListByParam(query);
		PaginationResultVO<UserInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	 }
	/**
	 * 新增
	 */
	@Override
	public Integer add(UserInfo bean){
		return this.userInfoMapper.insert(bean);
	 }
	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserInfo> listBean){
		if(listBean==null || listBean.isEmpty()){
			return 0;
		}
		return this.userInfoMapper.insertBatch(listBean);
	 }
	/**
	 * 新增或者修改
	 */
	@Override
	public Integer addOrUpdate(UserInfo userInfo){
		return this.userInfoMapper.insertOrUpdate(userInfo);
	 }
	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserInfo> listBean){
		if(listBean==null || listBean.isEmpty()){
			return 0;
		}
		return this.userInfoMapper.insertOrUpdateBatch(listBean);
	 }
	/**
	 * 根据UserId查询
	 */
	@Override
	 public UserInfo getByUserId(String userId){
		return this.userInfoMapper.selectByUserId(userId);
	 }
	/**
	 * 根据UserId更新
	 */
	@Override
	 public Integer updateByUserId(UserInfo bean , String userId){
		return this.userInfoMapper.updateByUserId(bean,userId);
	 }
	/**
	 * 根据UserId删除
	 */
	@Override
	 public Integer deleteByUserId(String userId){
		return this.userInfoMapper.deleteByUserId(userId);
	 }
	/**
	 * 根据Email查询
	 */
	@Override
	 public UserInfo getByEmail(String email){
		return this.userInfoMapper.selectByEmail(email);
	 }
	/**
	 * 根据Email更新
	 */
	@Override
	 public Integer updateByEmail(UserInfo bean , String email){
		return this.userInfoMapper.updateByEmail(bean,email);
	 }
	/**
	 * 根据Email删除
	 */
	@Override
	 public Integer deleteByEmail(String email){
		return this.userInfoMapper.deleteByEmail(email);
	 }

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void Register(String email, String nickname, String password) throws BusinessException {

		Map<String,Object> result=new HashMap<String,Object>();
		UserInfo userInfo  = this.userInfoMapper.selectByEmail(email);
		if(userInfo != null){
			throw new BusinessException("邮箱账号已经存在");
		}

		if(userInfo==null){
			String userId = StringTools.getUserId();

			UserInfoBeauty beautyAccount = this.userInfoBeautyMapper.selectByEmail(email);
			boolean useBeautyAccount = null != beautyAccount && BeautyAccountStatusEnum.NO_USE.getStatus().equals(beautyAccount.getStatus());
			if (useBeautyAccount) {
				userId = UserContactTypeEnum.USER.getPrefix() + beautyAccount.getUserId();
			}
			//数据库

			Date curDate = new Date();

			userInfo = new UserInfo();
			userInfo.setUserId(userId);
			userInfo.setEmail(email);
			userInfo.setNickName(nickname);
			userInfo.setPassword(StringTools.encodeMd5(password));
			userInfo.setCreateTime(curDate);
			userInfo.setStatus(UserStatusEnum.ENABLED.getStatus());
			userInfo.setLastOffTime(curDate);
			userInfo.setJoinType(JoinTypeEnum.APPLY.getType());

			this.userInfoMapper.insert(userInfo);

			if(useBeautyAccount){
				UserInfoBeauty beauty = new UserInfoBeauty();
				beauty.setStatus(BeautyAccountStatusEnum.USEED.getStatus());
				this.userInfoBeautyMapper.updateById(beauty,beauty.getId());
			}

			//TODO 创建机器人好友
		}


	}

	@Override
	public UserInfoVO Login(String email, String password) throws BusinessException {

		UserInfo userInfo = this.userInfoMapper.selectByEmail(email);

		if(userInfo == null || !userInfo.getPassword().equals(password)){
			throw new BusinessException("账号或密码不存在");
		}

		if(UserStatusEnum.DISABLED.getStatus().equals(userInfo.getStatus())){
			throw new BusinessException("账号已经禁用");
		}

		//TODO 查询我的组群
		//TODO 查询我的联系人
		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(userInfo);
		Long lastHeartBeat = redisComponent.getUerHeartBeat(userInfo.getUserId());
		if(lastHeartBeat!=null){
			throw new BusinessException("");
		}

		//保存登录信息到redis中
		String token = StringTools.encodeMd5(tokenUserInfoDto.getToken() + StringTools.getRandomString(20));
		tokenUserInfoDto.setToken(token);
		redisComponent.saveTokenUserInfoDto(tokenUserInfoDto);


		UserInfoVO userInfoVO = CopyTools.copy(userInfo,UserInfoVO.class);
		userInfoVO.setToken(tokenUserInfoDto.getToken());
		userInfoVO.setAdmin(tokenUserInfoDto.getAdmin());


		return userInfoVO;

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateUserInfo(UserInfo userInfo, MultipartFile avatarFile, MultipartFile avatarCover) throws IOException {
		if(avatarFile != null){
			String baseFolder = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
			File targetFile = new File(baseFolder + Constants.FILE_FOLDER_AVATAR_NAME);
			if(!targetFile.exists()){
				targetFile.mkdirs();
			}
			String filePath = targetFile.getPath() + "/" + userInfo.getUserId() + Constants.IMAGE_SUFFIX;
			avatarFile.transferTo(new File(filePath));
			avatarCover.transferTo(new File(filePath + Constants.COVER_IMAGE_SUFFIX));
		}

		UserInfo dbInfo = this.userInfoMapper.selectByUserId(userInfo.getUserId());
		this.userInfoMapper.updateByUserId(userInfo,userInfo.getUserId());
		String contactNameUpdate = null;
		if(dbInfo.getNickName().equals(userInfo.getNickName())){
			contactNameUpdate = userInfo.getNickName();
		}

		//TODO 更新会话信息中的昵称问题
	}

	@Override
	public void updateUserStatus(Integer status, String userId) throws BusinessException {
		UserStatusEnum userStatusEnum  = UserStatusEnum.getByStatus(status);
		if(userStatusEnum == null){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}

		UserInfo userInfo =  new UserInfo();
		userInfo.setStatus(userStatusEnum.getStatus());
		this.userInfoMapper.updateByUserId(userInfo,userId);
	}

	@Override
	public void forceOffLine(String userId) {
		//TODO 强制下线
	}


	private TokenUserInfoDto getTokenUserInfoDto(UserInfo userInfo){
		TokenUserInfoDto tokenUserInfoDto = new TokenUserInfoDto();
		tokenUserInfoDto.setUserId(userInfo.getUserId());
		tokenUserInfoDto.setNickName(userInfo.getNickName());

		String adminEmail = appConfig.getAdminEmail();//可能不只有一个，所以进行下面的处理

        tokenUserInfoDto.setAdmin(!StringTools.isEmpty(adminEmail) && ArrayUtils.contains(adminEmail.split(","), userInfo.getEmail()));
		return tokenUserInfoDto;
	}

//	//测试函数
//	public static void main(String [] args){
//		for (int i = 0;i < 10;i++){
//			System.out.println(System.currentTimeMillis() + " -> " + StringTools.getUserId());
//		}
//	}

}