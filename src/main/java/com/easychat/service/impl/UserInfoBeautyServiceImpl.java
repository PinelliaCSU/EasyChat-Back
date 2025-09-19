package com.easychat.service.impl;

import com.easychat.entity.enums.BeautyAccountStatusEnum;
import com.easychat.entity.enums.ResponseCodeEnum;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.UserInfoBeautyQuery;
import com.easychat.entity.query.SimplePage;
import com.easychat.entity.po.UserInfoBeauty;
import com.easychat.entity.query.UserInfoQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.exception.BusinessException;
import com.easychat.mappers.UserInfoBeautyMapper;
import com.easychat.mappers.UserInfoMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import javax.annotation.Resource;
import com.easychat.entity.enums.PageSize;
import com.easychat.service.UserInfoBeautyService;
/**
 * @author 高98
 * @Description: 靓号表对应的ServiceImpl
 * @date: 2025/07/23
 */

@Service("userInfoBeautyService")
public class UserInfoBeautyServiceImpl implements UserInfoBeautyService{

	@Resource
	private UserInfoBeautyMapper<UserInfoBeauty,UserInfoBeautyQuery> userInfoBeautyMapper;
	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserInfoBeauty>findListByParam(UserInfoBeautyQuery query){
		return this.userInfoBeautyMapper.selectList(query);
	 }
	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(UserInfoBeautyQuery query){
		return this.userInfoBeautyMapper.selectCount(query);
	 }
	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<UserInfoBeauty> findListByPage(UserInfoBeautyQuery query ){
		Integer count = this.findCountByParam(query); 
		Integer pageSize=query.getPageSize()==null? PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page=new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<UserInfoBeauty> list = this.findListByParam(query);
		PaginationResultVO<UserInfoBeauty> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	 }
	/**
	 * 新增
	 */
	@Override
	public Integer add(UserInfoBeauty bean){
		return this.userInfoBeautyMapper.insert(bean);
	 }
	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserInfoBeauty> listBean){
		if(listBean==null || listBean.isEmpty()){
			return 0;
		}
		return this.userInfoBeautyMapper.insertBatch(listBean);
	 }
	/**
	 * 新增或者修改
	 */
	@Override
	public Integer addOrUpdate(UserInfoBeauty userInfoBeauty){
		return this.userInfoBeautyMapper.insertOrUpdate(userInfoBeauty);
	 }
	/**
	 * 批量新增或修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserInfoBeauty> listBean){
		if(listBean==null || listBean.isEmpty()){
			return 0;
		}
		return this.userInfoBeautyMapper.insertOrUpdateBatch(listBean);
	 }
	/**
	 * 根据Id查询
	 */
	@Override
	 public UserInfoBeauty getById(String id){
		return this.userInfoBeautyMapper.selectById(id);
	 }
	/**
	 * 根据Id更新
	 */
	@Override
	 public Integer updateById(UserInfoBeauty bean , String id){
		return this.userInfoBeautyMapper.updateById(bean,id);
	 }
	/**
	 * 根据Id删除
	 */
	@Override
	 public Integer deleteById(String id){
		return this.userInfoBeautyMapper.deleteById(id);
	 }
	/**
	 * 根据UserId查询
	 */
	@Override
	 public UserInfoBeauty getByUserId(String userId){
		return this.userInfoBeautyMapper.selectByUserId(userId);
	 }
	/**
	 * 根据UserId更新
	 */
	@Override
	 public Integer updateByUserId(UserInfoBeauty bean , String userId){
		return this.userInfoBeautyMapper.updateByUserId(bean,userId);
	 }
	/**
	 * 根据UserId删除
	 */
	@Override
	 public Integer deleteByUserId(String userId){
		return this.userInfoBeautyMapper.deleteByUserId(userId);
	 }
	/**
	 * 根据Email查询
	 */
	@Override
	 public UserInfoBeauty getByEmail(String email){
		return this.userInfoBeautyMapper.selectByEmail(email);
	 }
	/**
	 * 根据Email更新
	 */
	@Override
	 public Integer updateByEmail(UserInfoBeauty bean , String email){
		return this.userInfoBeautyMapper.updateByEmail(bean,email);
	 }
	/**
	 * 根据Email删除
	 */
	@Override
	 public Integer deleteByEmail(String email){
		return this.userInfoBeautyMapper.deleteByEmail(email);
	 }

	@Override
	public void saveAccount(UserInfoBeauty beauty) throws BusinessException {
		if(beauty.getId() != null){
			UserInfoBeauty dbInfo = this.userInfoBeautyMapper.selectById(beauty.getId());
			if(BeautyAccountStatusEnum.USEED.getStatus().equals(dbInfo.getStatus())){
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}
		}
		UserInfoBeauty dbInfo = this.userInfoBeautyMapper.selectByEmail(beauty.getEmail());
		//新增的时候判断邮箱是否存在
		//beauty.getId() == null 表示当前是新增操作（新增时ID通常为空）。
		if(beauty.getId() == null && dbInfo != null){
			throw new BusinessException("靓号邮箱已经存在");
		}
		//修改时判断邮箱是否存在
		if(beauty.getId() != null && dbInfo != null && dbInfo.getId() != null && !beauty.getId().equals(dbInfo.getId())){
			throw new BusinessException("靓号邮箱已经存在");
		}

		//判断靓号是否存在，则判断逻辑与上述类似
		dbInfo = this.userInfoBeautyMapper.selectByUserId(beauty.getUserId());
		if(beauty.getId() == null && dbInfo != null){
			throw new BusinessException("靓号已经存在");
		}
		if(beauty.getId() != null && dbInfo != null && dbInfo.getId() != null && !beauty.getId().equals(dbInfo.getId())){
			throw new BusinessException("靓号已经存在");
		}

		//判断邮箱是否已经注册
		UserInfo userInfo = this.userInfoMapper.selectByEmail(beauty.getEmail());
		if(userInfo != null){
			throw new BusinessException("靓号邮箱已经杯注册！");
		}

		userInfo = this.userInfoMapper.selectByUserId(beauty.getUserId());
		if(userInfo != null){
			throw new BusinessException("靓号已经杯注册");
		}
		if(beauty.getId() != null){
			this.userInfoBeautyMapper.updateByUserId(beauty,beauty.getId());
		}else{
			this.userInfoBeautyMapper.insert(beauty);
		}
	}

}