package com.worth.wind.common.util;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonToken;
import com.worth.wind.blog.service.RedisService;
import com.worth.wind.common.entity.BusinessWorkEntity;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class BusinessWorkUtils {

    private static final String redisKey="BUSINESS_WORK:";
    private static final RedisService redisService = SpringUtil.getBean(RedisService.class);

    public static String startBusiness(Integer workType, String workName) {
        return startBusiness(workType, workName, null, null);
    }
    private static String getRedisKey(String businessId){return redisKey+businessId;}

    private static void saveRedis(BusinessWorkEntity entity){
        redisService.set(getRedisKey(entity.getId()),entity,60*60*24);
    }

    private static BusinessWorkEntity getRedis(String businessId){
        return (BusinessWorkEntity)redisService.get(getRedisKey(businessId));
    }

    public static String startBusiness(Integer workType, String workName, String fileId, String businessId) {
        businessId = StrUtil.isEmpty(businessId) ? IdUtil.fastSimpleUUID() : businessId;
        BusinessWorkEntity businessWorkEntity = new BusinessWorkEntity();
        businessWorkEntity.setId(businessId);
        businessWorkEntity.setFileId(fileId);
        businessWorkEntity.setWorkType(workType);
        businessWorkEntity.setWorkName(workName);
        saveRedis(businessWorkEntity);
        return businessId;
    }

    public static void updateBusinessProgress(String businessId, int progress) {
        BusinessWorkEntity businessWorkEntity = getRedis(businessId);
        businessWorkEntity.setProgress(progress);
        saveRedis(businessWorkEntity);
    }

    public static void finishBusinessWithMsg(String businessId, String msg) {
        BusinessWorkEntity businessWorkEntity = getRedis(businessId);
        businessWorkEntity.setId(businessId);
        businessWorkEntity.setFileUrl(msg);
        businessWorkEntity.setProgress(100);
        saveRedis(businessWorkEntity);
    }

    public static void finishBusiness(String businessId, String fileId) {
        BusinessWorkEntity businessWorkEntity =  getRedis(businessId);
        businessWorkEntity.setId(businessId);
        businessWorkEntity.setFileId(fileId);
        businessWorkEntity.setProgress(100);
        saveRedis(businessWorkEntity);
    }

    public static void finishBusiness(String businessId) {
        finishBusiness(businessId, null);
    }

    public static BusinessWorkEntity queryBusiness(String businessId) {
        return  getRedis(businessId);
    }

    public static Integer queryProgress(String businessId) {
        BusinessWorkEntity itoBusinessWorkVo = queryBusiness(businessId);
        if (itoBusinessWorkVo == null) {
            throw new RuntimeException();
        }
        return itoBusinessWorkVo.getProgress();
    }

    public static void waitFinish(String businessId) {
        Integer progress = 0;
        do {
            progress = Optional.ofNullable(queryProgress(businessId)).orElse(0);
            if (progress != 100) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } while (progress != 100);
    }
}
