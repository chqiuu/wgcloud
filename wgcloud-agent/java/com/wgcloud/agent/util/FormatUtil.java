package com.wgcloud.agent.util;

import cn.hutool.core.io.FileUtil;
import com.wgcloud.agent.ScheduledTask;
import com.wgcloud.agent.entity.AppInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class FormatUtil {

    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 格式化double数据，截取小数点后数字
     *
     * @param str
     * @param num
     * @return
     */
    public static double formatDouble(double str, int num) {
        java.math.BigDecimal b = new java.math.BigDecimal(str);
        return b.setScale(num, java.math.BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String delChar(String str) {
        if (StringUtils.isEmpty(str)) {
            return "";
        }
        str = str.replace("%", "");
        return str;
    }

    /**
     * 获取当前时间
     *
     * @return 当前日期
     */
    public static Timestamp getNowTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATETIME_PATTERN);
        return Timestamp.valueOf(dateFormat.format(new Date()));
    }

    public static Date getDateBefore(Date datetimes, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(datetimes);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }

    /**
     * m转为g
     *
     * @param str
     * @return
     */
    public static double mToG(String str) {
        double result = 0;
        double mod = 1024;
        if (str.contains("M")) {
            double f = Double.parseDouble(str.replace("M", ""));
            result = f / mod;
        } else if (str.contains("K")) {
            double f = Double.parseDouble(str.replace("K", ""));
            result = (f / mod) / mod;
        } else if (str.contains("T")) {
            double f = Double.parseDouble(str.replace("T", ""));
            result = f * 1024;
        } else if (str.contains("G")) {
            result = Double.parseDouble(str.replace("G", ""));
        }
        return formatDouble(result, 2);
    }

    public static String getPidByFile(AppInfo appInfo) {
        if (appInfo.getAppType() == 1) {
            return appInfo.getAppPid();
        } else {
            try {
                String pid = FileUtil.readString(appInfo.getAppPid(), "UTF-8");
                if (!StringUtils.isEmpty(pid)) {
                    return pid.trim();
                }
            } catch (Exception e) {
                log.error("获取PID文件错误", e);
            }
        }
        return "";
    }
}
