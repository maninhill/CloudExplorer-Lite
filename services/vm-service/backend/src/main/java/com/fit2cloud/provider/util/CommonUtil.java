package com.fit2cloud.provider.util;

import com.fit2cloud.provider.ICloudProvider;
import io.reactivex.rxjava3.functions.BiFunction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * @Author:张少虎
 * @Date: 2022/9/22  4:42 PM
 * @Version 1.0
 * @注释:
 */
public class CommonUtil {
    /**
     * 获取utc时间,根据时间字符串
     *
     * @param dateStr 时间字符串
     * @param format  format表达式
     * @return 时间戳
     */
    public static long getUTCTime(String dateStr, String format) {
        try {
            Calendar cal = Calendar.getInstance();
            int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
            int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date = sdf.parse(dateStr);
            return date.getTime() + (zoneOffset + dstOffset);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 执行函数
     * 有返回值
     * @param providerClass 执行处理器
     * @param req           请求参数画
     * @param exec          执行函数
     * @param <T>           执行函数返回对象
     * @return 执行函数返回对象泛型
     */
    public static <T> T exec(Class<? extends ICloudProvider> providerClass, String req, BiFunction<ICloudProvider, String, T> exec) {
        try {
            ICloudProvider iCloudProvider = providerClass.getConstructor().newInstance();
            return exec.apply(iCloudProvider, req);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * 获取请求参数
     * @param credential
     * @param regionId
     * @return
     */
    public static HashMap<String, String> getParams(String credential,String regionId){
        HashMap<String, String> params = new HashMap<>();
        params.put("credential", credential);
        params.put("regionId", regionId);
        return params;
    }

}
