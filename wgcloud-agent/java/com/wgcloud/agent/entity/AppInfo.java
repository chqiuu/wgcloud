package com.wgcloud.agent.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 *
 * @ClassName:AppInfo.java     
 * @version V2.3
 * @author: wgcloud     
 * @date: 2019年11月16日
 * @Description: app端口信息
 * @Copyright: 2019-2022 wgcloud. All rights reserved.
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AppInfo extends BaseEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2913111613773445949L;
	/**
	 * host名称
	 */
	private String hostname;
	/**
	 * 应用进程ID
	 */
    private String appPid;
    /**
	 * 应用进程名称
	 */
    private String appName;
	/**
	 *内存使用M
	 */
	private Double memPer;
	/**
	 * cpu使用率
	 */
	private Double cpuPer;
	/**
	 * 进程获取途径，1进程id号，2进程pid文件
	 */
	private Integer appType;
	/**
	 * 进程状态，1正常，2下线
	 */
	private Integer state;
	/**
     * 创建时间
     */
    private Date createTime;
}