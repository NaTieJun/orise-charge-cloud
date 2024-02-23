package org.dromara.omind.userplat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.userplat.api.domain.datas.StationFeeDetailData;
import org.dromara.omind.userplat.api.domain.entity.OmindOperatorEntity;
import org.dromara.omind.userplat.api.domain.entity.OmindPriceEntity;
import org.dromara.omind.userplat.api.domain.entity.OmindStationEntity;
import org.dromara.omind.userplat.api.domain.notifications.NotificationStationFeeData;
import org.dromara.omind.userplat.api.domain.request.StationPageRequest;
import org.dromara.omind.userplat.constant.PlatRedisKey;
import org.dromara.omind.userplat.domain.service.OmindStationEntityIService;
import org.dromara.omind.userplat.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class OmindStationServiceImpl implements OmindStationService {

    @Autowired
    @Lazy
    OmindStationEntityIService iService;

    @Autowired
    @Lazy
    OmindOperatorService omindOperatorService;

    @Autowired
    @Lazy
    OmindEquipmentService omindEquipmentService;

    @Autowired
    @Lazy
    OmindConnectorService omindConnectorService;

    @Autowired
    @Lazy
    OmindPriceService omindPriceService;

    @Override
    public OmindStationEntity get(String stationId) {
        String key = PlatRedisKey.STATION_INFO + stationId;
        if (PlatRedisKey.REDIS_FLAG) {
            if (RedisUtils.hasKey(key)) {
                OmindStationEntity odStationInfoData = RedisUtils.getCacheObject(key);
                if (odStationInfoData != null) {
                    return odStationInfoData;
                }
            }
        }

        LambdaQueryWrapper<OmindStationEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OmindStationEntity::getStationId, stationId).last("limit 1");
        OmindStationEntity odStationInfo = iService.getOne(lambdaQueryWrapper);

        //如果判断是亚马逊S3的链接 自动去掉授权部分
        try {
            if (odStationInfo != null && !TextUtils.isBlank(odStationInfo.getPictures())) {
                JSONArray jsonArray = JSON.parseArray(odStationInfo.getPictures());
                if (jsonArray != null && jsonArray.size() > 0) {
                    JSONArray newArray = new JSONArray();
                    int size = jsonArray.size();
                    for (int i = 0; i < size; i++) {
                        String jsonStr = jsonArray.getString(i);
                        if (jsonStr.contains("s3.cn-northwest-1.amazonaws.com.cn") && jsonStr.contains("?")) {
                            int end = jsonStr.indexOf("?");
                            String newStr = jsonStr.substring(0, end);
                            newArray.add(newStr);
                        }
                    }
                    odStationInfo.setPictures(newArray.toJSONString());
                }
            }
        } catch (Exception ex) {
            log.error("station-selectStationById-error", ex);
        }

        //添加到redis
        RedisUtils.setCacheObject(key, odStationInfo, Duration.ofSeconds(300));
        return odStationInfo;
    }

    @Override
    public List<OmindStationEntity> getAllGeoData() {
        LambdaQueryWrapper<OmindStationEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByDesc(OmindStationEntity::getId);
        return iService.list(lambdaQueryWrapper);
    }

    @Override
    public void stationFeeDeal(NotificationStationFeeData notificationStationFeeData) throws BaseException {
        try {
            String stationId = notificationStationFeeData.getStationID();
            List<StationFeeDetailData> stationFeeList = notificationStationFeeData.getChargeFeeDetail();
            if (stationFeeList != null && stationFeeList.size() > 0) {
                //删除原有数据
                List<OmindPriceEntity> priceList = omindPriceService.getPrice(stationId);
                if (priceList != null && priceList.size() > 0) {
                    for (OmindPriceEntity odPriceInfo : priceList) {
                        omindPriceService.deletePriceById(odPriceInfo.getId());
                    }
                }

                //删除该站点的所有枪的缓存
                omindConnectorService.connectorPriceCacheDeal(stationId);
                List<OmindPriceEntity> addPriceList = new ArrayList<>();
                for (StationFeeDetailData stationFeeDetailData : stationFeeList) {
                    OmindPriceEntity omindPrice = new OmindPriceEntity();
                    omindPrice.setStationId(stationId);
                    String startTime = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, new Date()) +" "+ stationFeeDetailData.getStartTime() + ":00";
                    omindPrice.setStartTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, startTime));
                    omindPrice.setElecPrice(stationFeeDetailData.getElectricityFee().setScale(6, RoundingMode.HALF_EVEN));
                    omindPrice.setServicePrice(stationFeeDetailData.getServiceFee().setScale(6, RoundingMode.HALF_EVEN));
                    omindPrice.setUpdateTime(new Date());
                    addPriceList.add(omindPrice);
                }
                if (addPriceList.size() > 0) {
                    omindPriceService.batchInsertPrice(addPriceList);
                }
            }
        } catch (BaseException ube) {
            log.error("station-stationFeeDeal-error", ube);
            throw ube;
        } catch (Exception e) {
            log.error("station-stationFeeDeal-error", e);
            throw new BaseException("未知错误");
        }
    }

    @Override
    public void stationCacheDel(String stationId) {
        String key = PlatRedisKey.STATION_INFO + stationId;
        RedisUtils.deleteObject(key);
        //清除站点信息缓存
        String stationKey = PlatRedisKey.REALTIME_STATION_DATA + stationId;
        if (RedisUtils.hasKey(stationKey)) {
            RedisUtils.deleteObject(stationKey);
        }
        //清除站点充电桩列表
        String stationEquipmentListKey = PlatRedisKey.STATION_EQUIPMENT_LIST + stationId;
        RedisUtils.deleteObject(stationEquipmentListKey);

        //清除站点停车计费数据列表
        String stationParkListKey = PlatRedisKey.TAG_STATION_PARK_INFO_LIST + stationId;
        RedisUtils.deleteObject(stationParkListKey);
    }

    @Override
    public int batchInsertStation(List<OmindStationEntity> stationList) throws BaseException {
        if (stationList == null || stationList.size() == 0) {
            return 10;
        }
        if (!iService.saveBatch(stationList)) {
            throw new BaseException("数据保存失败");
        }
        return 0;
    }

    @Override
    public int batchUpdateStation(List<OmindStationEntity> stationList) throws BaseException {
        if (stationList == null || stationList.size() == 0) {
            return 10;
        }
        if (!iService.updateBatchById(stationList)) {
            throw new BaseException("数据更新失败");
        }

        //批量更新redis
        for (OmindStationEntity omindStationEntity : stationList) {
            String key = PlatRedisKey.STATION_INFO + omindStationEntity.getStationId();
            RedisUtils.deleteObject(key);
        }
        return 0;
    }

    @Override
    public TableDataInfo<OmindStationEntity> selectStationList(StationPageRequest stationPageRequest, PageQuery pageQuery) {
        LambdaQueryWrapper<OmindStationEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (stationPageRequest.getStationName() != null && !TextUtils.isBlank(stationPageRequest.getStationName())) {
            lambdaQueryWrapper.like(OmindStationEntity::getStationName, stationPageRequest.getStationName().replace("%", "\\%"));
        }
        if (stationPageRequest.getAreaCode() != null && !TextUtils.isBlank(stationPageRequest.getAreaCode())) {
            lambdaQueryWrapper.likeRight(OmindStationEntity::getAreaCode, stationPageRequest.getAreaCode());
        }
        if (stationPageRequest.getOperatorId() != null && !TextUtils.isBlank(stationPageRequest.getOperatorId())) {
            lambdaQueryWrapper.eq(OmindStationEntity::getBaseOperatorId, stationPageRequest.getOperatorId());
        }

        //站点状态:0、未知;1、建设中;5、关闭下线;6、维护中;50、正常使用
        if (stationPageRequest.getStationStatus() != null && stationPageRequest.getStationStatus() >= 0) {
            lambdaQueryWrapper.eq(OmindStationEntity::getStationStatus, stationPageRequest.getStationStatus());
        }

        lambdaQueryWrapper.orderByDesc(OmindStationEntity::getId);

        Page<OmindStationEntity> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize(), true);
        IPage<OmindStationEntity> iPage = iService.page(page, lambdaQueryWrapper);

        if (iPage.getRecords() != null) {
            for (OmindStationEntity omindStation : iPage.getRecords()) {
                omindStation.setEquipmentCount(omindEquipmentService.equipmentCount(omindStation.getStationId()));
                omindStation.setConnectorCount(omindConnectorService.connectorCount(omindStation.getStationId(), ""));

                OmindOperatorEntity omindOperatorEntity = omindOperatorService.selectOperatorInfoById(omindStation.getBaseOperatorId());
                String operatorName = "";
                if (omindOperatorEntity != null) {
                    operatorName = omindOperatorEntity.getOperatorName();
                }
                omindStation.setOperatorName(operatorName);

            }
        }
        TableDataInfo<OmindStationEntity> tableDataInfo = TableDataInfo.build(iPage);
        return tableDataInfo;
    }
}
