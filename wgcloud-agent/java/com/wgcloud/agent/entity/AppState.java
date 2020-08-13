package com.wgcloud.agent.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 *
 * @ClassName:AppState.java     
 * @version V2.3
 * @author: wgcloud     
 * @date: 2019年11月16日
 * @Description: app状态监控
 * @Copyright: 2019-2022 wgcloud. All rights reserved.
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AppState extends BaseEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2913111613773445949L;
	/**
	 * 应用信息ID
	 */
	private String appInfoId;
	/**
	 * %CPU
	 */
    private Double cpuPer;

    /**
	 * %MEM
	 */
    private Double memPer;
    
    /**
     * 添加时间
     * MM-dd hh:mm:ss
     */
    private String dateStr;
    
    /**
     * 创建时间
     */
    private Date createTime; 
}