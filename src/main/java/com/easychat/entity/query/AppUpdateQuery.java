package com.easychat.entity.query;

import java.util.Date;

/**
 * @Description app发布查询对象
 * @author Pinellia
 * @Date 2025-09-24 17:06:29
 **/
public class AppUpdateQuery extends BaseQuery {
	//自增加
	private Integer id;
	//版本号
	private String version;
	private String versionFuzzy;

	//更新描述
	private String updateDesc;
	private String updateDescFuzzy;

	//创建时间
	private Date createTime;
	private String createTimeStart;

	private String createTimeEnd;

	//0:未发布 1:灰度发布 2:全网发布
	private Integer status;
	//灰度uid
	private String grayscaleUid;
	private String grayscaleUidFuzzy;

	//文件类型0:本地文件 1:外链
	private Integer fileType;
	//外链地址
	private String outerLink;
	private String outerLinkFuzzy;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getUpdateDesc() {
		return updateDesc;
	}
	public void setUpdateDesc(String updateDesc) {
		this.updateDesc = updateDesc;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getGrayscaleUid() {
		return grayscaleUid;
	}
	public void setGrayscaleUid(String grayscaleUid) {
		this.grayscaleUid = grayscaleUid;
	}
	public Integer getFileType() {
		return fileType;
	}
	public void setFileType(Integer fileType) {
		this.fileType = fileType;
	}
	public String getOuterLink() {
		return outerLink;
	}
	public void setOuterLink(String outerLink) {
		this.outerLink = outerLink;
	}
	public String getVersionFuzzy() {
		return versionFuzzy;
	}
	public void setVersionFuzzy(String versionFuzzy) {
		this.versionFuzzy = versionFuzzy;
	}
	public String getUpdateDescFuzzy() {
		return updateDescFuzzy;
	}
	public void setUpdateDescFuzzy(String updateDescFuzzy) {
		this.updateDescFuzzy = updateDescFuzzy;
	}
	public String getCreateTimeStart() {
		return createTimeStart;
	}
	public void setCreateTimeStart(String createTimeStart) {
		this.createTimeStart = createTimeStart;
	}
	public String getCreateTimeEnd() {
		return createTimeEnd;
	}
	public void setCreateTimeEnd(String createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}
	public String getGrayscaleUidFuzzy() {
		return grayscaleUidFuzzy;
	}
	public void setGrayscaleUidFuzzy(String grayscaleUidFuzzy) {
		this.grayscaleUidFuzzy = grayscaleUidFuzzy;
	}
	public String getOuterLinkFuzzy() {
		return outerLinkFuzzy;
	}
	public void setOuterLinkFuzzy(String outerLinkFuzzy) {
		this.outerLinkFuzzy = outerLinkFuzzy;
	}
}