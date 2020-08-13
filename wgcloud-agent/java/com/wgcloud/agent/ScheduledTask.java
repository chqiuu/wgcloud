package com.wgcloud.agent;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wgcloud.agent.config.CommonConfig;
import com.wgcloud.agent.entity.*;
import com.wgcloud.agent.util.FormatUtil;
import com.wgcloud.agent.util.RestUtil;
import com.wgcloud.agent.util.SigarUtil;
import org.apache.commons.lang3.StringUtils;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @version V2.3
 * @ClassName:ScheduledTask.java
 * @author: wgcloud
 * @date: 2019年11月16日
 * @Description: ScheduledTask.java
 * @Copyright: 2019 wgcloud. All rights reserved.
 */
@Component
public class ScheduledTask {

    public static List<AppInfo> APP_INFO_LIST = Collections.synchronizedList(new ArrayList<AppInfo>());
    @Autowired
    private RestUtil restUtil;
    @Autowired
    private CommonConfig commonConfig;

    private SystemInfo systemInfo = null;

    /**
     * 线程池
     */
    static ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 2, TimeUnit.MINUTES, new LinkedBlockingDeque<>());

    /**
     * 60秒后执行，每隔60秒执行, 单位：ms。
     */
    @Scheduled(initialDelay = 30 * 1000L, fixedRate = 60 * 1000)
    public void minTask() {
        List<AppInfo> appInfoListCp = new ArrayList<AppInfo>();
        appInfoListCp.addAll(APP_INFO_LIST);
        JSONObject jsonObject = new JSONObject();
        LogInfo logInfo = new LogInfo();
        Timestamp t = FormatUtil.getNowTime();
        logInfo.setHostname(commonConfig.getBindIp() + "：Agent错误");
        logInfo.setCreateTime(t);
        try {
            // 操作系统信息
            systemInfo = SigarUtil.os();
            systemInfo.setHostname(commonConfig.getBindIp());
            systemInfo.setCreateTime(t);
            // 文件系统信息
            List<DeskState> deskStateList = SigarUtil.file(t);
            deskStateList.forEach(deskState -> deskState.setHostname(commonConfig.getBindIp()));
            // cpu信息
            CpuState cpuState = SigarUtil.cpu();
            cpuState.setHostname(commonConfig.getBindIp());
            cpuState.setCreateTime(t);
            // 内存信息
            MemState memState = SigarUtil.memory();
            memState.setHostname(commonConfig.getBindIp());
            memState.setCreateTime(t);
            // 网络流量信息
            NetIoState netIoState = SigarUtil.net();
            netIoState.setHostname(commonConfig.getBindIp());
            netIoState.setCreateTime(t);

            // 系统负载信息
            SysLoadState sysLoadState = SigarUtil.getLoadState(systemInfo);
            if (sysLoadState != null) {
                sysLoadState.setHostname(commonConfig.getBindIp());
                sysLoadState.setCreateTime(t);
                jsonObject.put("sysLoadState", sysLoadState);
            }
            if (cpuState != null) {
                jsonObject.put("cpuState", cpuState);
            }
            if (memState != null) {
                jsonObject.put("memState", memState);
            }
            if (netIoState != null) {
                jsonObject.put("netIoState", netIoState);
            }
            if (systemInfo != null) {
                if (memState != null) {
                    systemInfo.setVersionDetail(systemInfo.getVersion() + "，总内存：" + FormatUtil.mToG(memState.getTotal() + "M") + "G");
                    systemInfo.setMemPer(memState.getUsePer());
                } else {
                    systemInfo.setMemPer(0d);
                }
                if (cpuState != null) {
                    systemInfo.setCpuPer(cpuState.getSys());
                } else {
                    systemInfo.setCpuPer(0d);
                }
                jsonObject.put("systemInfo", systemInfo);
            }
            if (deskStateList != null) {
                jsonObject.put("deskStateList", deskStateList);
            }
            //进程信息
            if (appInfoListCp.size() > 0) {
                List<AppInfo> appInfoResList = new ArrayList<>();
                List<AppState> appStateResList = new ArrayList<>();
                for (AppInfo appInfo : appInfoListCp) {
                    appInfo.setHostname(commonConfig.getBindIp());
                    appInfo.setCreateTime(t);
                    appInfo.setState(1);
                    String pid = FormatUtil.getPidByFile(appInfo);
                    if (StringUtils.isEmpty(pid)) {
                        continue;
                    }
                    AppState appState = SigarUtil.getLoadPid(pid);
                    if (appState != null) {
                        appState.setCreateTime(t);
                        appState.setAppInfoId(appInfo.getId());
                        appInfo.setMemPer(appState.getMemPer());
                        appInfo.setCpuPer(appState.getCpuPer());
                        appInfoResList.add(appInfo);
                        appStateResList.add(appState);
                    }
                }
                jsonObject.put("appInfoList", appInfoResList);
                jsonObject.put("appStateList", appStateResList);
            }

        } catch (Exception e) {
            e.printStackTrace();
            logInfo.setInfoContent(e.toString());
        } finally {
            if (!StringUtils.isEmpty(logInfo.getInfoContent())) {
                jsonObject.put("logInfo", logInfo);
            }
            restUtil.post(commonConfig.getServerUrl() + "/wgcloud/agent/minTask", jsonObject);
        }

    }


    /**
     * 30秒后执行，每隔5分钟执行, 单位：ms。
     * 获取监控进程
     */
    @Scheduled(initialDelay = 28 * 1000L, fixedRate = 300 * 1000)
    public void appInfoListTask() {
        JSONObject jsonObject = new JSONObject();
        LogInfo logInfo = new LogInfo();
        Timestamp t = FormatUtil.getNowTime();
        logInfo.setHostname(commonConfig.getBindIp() + "：Agent获取进程列表错误");
        logInfo.setCreateTime(t);
        try {
            JSONObject paramsJson = new JSONObject();
            paramsJson.put("hostname", commonConfig.getBindIp());
            String resultJson = restUtil.post(commonConfig.getServerUrl() + "/wgcloud/appInfo/agentList", paramsJson);
            if (resultJson != null) {
                JSONArray resultArray = JSONUtil.parseArray(resultJson);
                APP_INFO_LIST.clear();
                if (resultArray.size() > 0) {
                    APP_INFO_LIST = JSONUtil.toList(resultArray, AppInfo.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logInfo.setInfoContent(e.toString());
        } finally {
            if (!StringUtils.isEmpty(logInfo.getInfoContent())) {
                jsonObject.put("logInfo", logInfo);
            }
            restUtil.post(commonConfig.getServerUrl() + "/wgcloud/agent/minTask", jsonObject);
        }
    }
}
