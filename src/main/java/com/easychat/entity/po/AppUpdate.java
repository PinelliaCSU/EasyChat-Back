package com.easychat.entity.po;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import com.easychat.entity.enums.DateTimePatternEnum;
import com.easychat.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description app发布
 * @author 赵默笙
 * @Date 2025-09-24 17:06:29
 **/
public class AppUpdate implements Serializable{
	//自增加
	private Integer id;
	//版本号
	private String version;
	//更新描述
	private String updateDesc;
	//创建时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createTime;
	//0:未发布 1:灰度发布 2:全网发布
	@JsonIgnore
	private Integer status;
	//灰度uid
	private String grayscaleUid;
	//文件类型0:本地文件 1:外链
	private Integer fileType;
	//外链地址
	private String outerLink;

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
	@Override
	public String toString (){
		return "自增加:"+(id == null ? "空" : id)+",版本号:"+(version == null ? "空" : version)+",更新描述:"+(updateDesc == null ? "空" : updateDesc)+",创建时间:"+(createTime == null ? "空" : DateUtils.format(createTime,DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+",0:未发布 1:灰度发布 2:全网发布:"+(status == null ? "空" : status)+",灰度uid:"+(grayscaleUid == null ? "空" : grayscaleUid)+",文件类型0:本地文件 1:外链:"+(fileType == null ? "空" : fileType)+",外链地址:"+(outerLink == null ? "空" : outerLink);
	}
}