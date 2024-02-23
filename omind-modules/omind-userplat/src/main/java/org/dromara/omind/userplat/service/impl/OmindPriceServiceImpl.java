package org.dromara.omind.userplat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.userplat.api.domain.datas.PolicyInfoData;
import org.dromara.omind.userplat.api.domain.entity.OmindConnectorEntity;
import org.dromara.omind.userplat.api.domain.entity.OmindPriceEntity;
import org.dromara.omind.userplat.constant.PlatRedisKey;
import org.dromara.omind.userplat.domain.service.OmindPriceEntityIService;
import org.dromara.omind.userplat.service.OmindConnectorService;
import org.dromara.omind.userplat.service.OmindPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class OmindPriceServiceImpl implements OmindPriceService {

    @Autowired
    @Lazy
    OmindPriceService selfService;

    @Autowired
    @Lazy
    OmindPriceEntityIService iService;

    @Autowired
    @Lazy
    OmindConnectorService omindConnectorService;

    @Override
    public List<OmindPriceEntity> getPrice(String stationId) {
        LambdaQueryWrapper<OmindPriceEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OmindPriceEntity::getStationId, stationId);
        lambdaQueryWrapper.orderByAsc(OmindPriceEntity::getStartTime);

        return iService.list(lambdaQueryWrapper);
    }

    @Override
    public OmindPriceEntity getPriceCurrent(String stationId) {
        List<OmindPriceEntity> priceList = selfService.getPrice(stationId);
        OmindPriceEntity odPriceEntity = null;
        if (priceList != null && priceList.size() > 0) {
            int priceCount = priceList.size();

            if (priceCount == 1) {
                odPriceEntity = priceList.get(0);
            } else {
                String currentTimeStr = (DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date())).substring(11, 19).replace(":", "");
                Integer currentTimeInt = Integer.parseInt(currentTimeStr);
                List<Integer> startTimeIntList = new ArrayList<>();
                for (OmindPriceEntity omindPrice : priceList) {
                    String startTimeStr = (DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, omindPrice.getStartTime())).substring(11, 19).replace(":", "");
                    startTimeIntList.add(Integer.parseInt(startTimeStr));
                }
                int i = 0;
                int nowTime = 0;
                for (Integer starTime : startTimeIntList) {
                    if (currentTimeInt > starTime && i == priceCount - 1) {
                        nowTime = startTimeIntList.get(i);
                    }
                    if (currentTimeInt < starTime) {
                        if (i != 0) {
                            nowTime = startTimeIntList.get(i - 1);
                        } else {
                            nowTime = startTimeIntList.get(i);
                        }
                        break;
                    }
                    i++;
                }
                for (OmindPriceEntity omindPrice : priceList) {
                    String startTimeStr = (DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, omindPrice.getStartTime())).substring(11, 19).replace(":", "");
                    if (Integer.parseInt(startTimeStr) == nowTime) {
                        odPriceEntity = omindPrice;
                    }
                }

            }
        }
        return odPriceEntity;
    }

    @Override
    public List<PolicyInfoData> queryEquipPrice(String connectorId) throws BaseException {
        try {
            List<PolicyInfoData> policyInfoList = new ArrayList<>();
            List<OmindPriceEntity> priceList = new ArrayList<>();
            String key = PlatRedisKey.CONNECTOR_PRICE_LIST + connectorId;
            log.info("charge-order-info----price--connector_id=" + connectorId);
            if (PlatRedisKey.REDIS_FLAG) {
                if (RedisUtils.hasKey(key)) {
                    priceList = RedisUtils.getCacheObject(key);
                }
            }

            if (priceList.size() == 0) {
                OmindConnectorEntity odConnectorEntity = omindConnectorService.selectConnectorInfo(connectorId);
                if (odConnectorEntity != null) {
                    priceList = selfService.getPrice(odConnectorEntity.getStationId());
                    log.info("charge-order-info----price---out-priceList=" + priceList);
                    if (priceList.size() > 0) {
                        RedisUtils.setCacheObject(key, priceList, Duration.ofSeconds(300));
                    }
                }
            }

            if (priceList.size() > 0) {
                List<PolicyInfoData> policyInfoDataList = new ArrayList<>();
                for (int i = 0; i < 24; i++) {
                    PolicyInfoData policyInfoData = new PolicyInfoData();
                    policyInfoData.setStartTime(String.format("%02d0000", i));
                    OmindPriceEntity cPrice = null;    //命中价格
                    for (OmindPriceEntity price : priceList) {
                        if (cPrice == null) {
                            cPrice = price;
                            continue;
                        }
                        if (i >= price.getHour() && ((i - price.getHour()) < (i - cPrice.getHour()))) {
                            cPrice = price;
                        }
                    }
                    policyInfoData.setElecPrice(cPrice.getElecPrice());
                    policyInfoData.setServicePrice(cPrice.getServicePrice());
                    policyInfoData.setPriceType(cPrice.getPriceType() == null ? (short) 2 : cPrice.getPriceType());
                    policyInfoDataList.add(policyInfoData);
                }

                policyInfoList = selfService.combine(policyInfoDataList);
            }

            return policyInfoList;
        } catch (BaseException ube) {
            log.error("priceinfo-queryEquipPrice-error", ube);
            throw ube;
        } catch (Exception e) {
            log.error("priceinfo-queryEquipPrice-error", e);
            throw new BaseException("未知错误");
        }
    }

    @Override
    public List<PolicyInfoData> combine(List<PolicyInfoData> priceList) {
        List<PolicyInfoData> newPriceList = new ArrayList();
        PolicyInfoData newData = null;
        for (PolicyInfoData policyInfoData : priceList) {
            if (newData == null) {
                newData = policyInfoData;
                continue;
            }
            if (policyInfoData.getServicePrice().compareTo(newData.getServicePrice()) == 0
                    && policyInfoData.getPriceType().compareTo(newData.getPriceType()) == 0
                    && policyInfoData.getElecPrice().compareTo(newData.getElecPrice()) == 0) {
                //一样的价格
                continue;
            } else {
                newPriceList.add(newData);
                newData = policyInfoData;
            }

        }
        if (newData != null) {
            newPriceList.add(newData);
        }

        if (newPriceList.size() == 0 || newPriceList.size() >= priceList.size()) {
            return priceList;
        }
        return newPriceList;
    }

    @Override
    public int deletePriceById(Long id) throws BaseException {
        OmindPriceEntity odPriceEntity = selfService.selectPriceById(id);
        if (odPriceEntity == null) {
            throw new BaseException("数据不存在");
        }

        boolean delFlag = iService.remove(new LambdaQueryWrapper<OmindPriceEntity>()
                .eq(OmindPriceEntity::getId, id));

        return delFlag ? 1 : 0;
    }

    @Override
    public OmindPriceEntity selectPriceById(Long id) {
        LambdaQueryWrapper<OmindPriceEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OmindPriceEntity::getId, id).last("limit 1");
        return iService.getOne(lambdaQueryWrapper);
    }

    @Override
    public int batchInsertPrice(List<OmindPriceEntity> priceList) throws BaseException {
        if (priceList == null || priceList.size() == 0) {
            return 10;
        }
        if (!iService.saveBatch(priceList)) {
            throw new BaseException("数据保存失败");
        }
        return 0;
    }
}
