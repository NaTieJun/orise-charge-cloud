package org.dromara.omind.mp.service.impl;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.log4j.Log4j2;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.omind.mp.domain.vo.GunVo;
import org.dromara.omind.mp.domain.vo.PileVo;
import org.dromara.omind.mp.domain.vo.PriceVo;
import org.dromara.omind.mp.domain.vo.StationInfoVo;
import org.dromara.omind.mp.service.StationService;
import org.dromara.omind.userplat.api.domain.entity.*;
import org.dromara.omind.userplat.api.service.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Log4j2
public class StationServiceImpl implements StationService {

    @DubboReference
    RemoteOmindPriceService priceService;

    @DubboReference
    RemoteOmindStationService stationService;

    @DubboReference
    RemoteOmindEquipmentService equipmentService;

    @DubboReference
    RemoteOmindConnectorService connectorService;

    @DubboReference
    RemoteOmindStationImageService stationImageService;

    @DubboReference
    RemoteOmindBillService omindBillService;

    @Override
    public StationInfoVo getDetailInfo(String stationId) {

        StationInfoVo stationInfoVo = new StationInfoVo();

        OmindStationEntity stationEntity = stationService.get(stationId);
        if (stationEntity == null || stationEntity.getStationStatus() != 50) {
            throw new BaseException("站点未开放");
        }

        stationInfoVo.setId(stationEntity.getId());
        stationInfoVo.setStationId(stationEntity.getStationId());
        stationInfoVo.setStationName(stationEntity.getStationName());
        stationInfoVo.setStationStatus(stationEntity.getStationStatus());
        stationInfoVo.setAddress(stationEntity.getAddress());
        stationInfoVo.setLat(stationEntity.getStationLat());
        stationInfoVo.setLon(stationEntity.getStationLng());
        stationInfoVo.setStationPhone(stationEntity.getStationTel());
        stationInfoVo.setServicePhone(stationEntity.getServiceTel());
        stationInfoVo.setPlatType(stationEntity.getPlatType());

        List<OmindStationImageEntity> imgList = stationImageService.all(stationId);
        JSONArray imgArray = new JSONArray();
        if (imgList != null) {
            for (OmindStationImageEntity imagesEntity : imgList) {
                if (imagesEntity != null && !TextUtils.isBlank(imagesEntity.getImageUrl())) {
//                    String s3Url = minioS3Utils.getAccessUrl(imagesEntity.getImageUrl());
//                    if (!TextUtils.isBlank(s3Url)) {
//                        imgArray.add(s3Url);
//                    }
                    //todo 图片
                }
            }
        }
        stationInfoVo.setImgs(imgArray.toJSONString());

        //--------------价格
        OmindPriceEntity currentPrice = priceService.getPriceCurrent(stationId);
        if(currentPrice != null){
            stationInfoVo.setCurrentPrice(currentPrice.getElecPrice().add(currentPrice.getServicePrice()));
        }
        else {
            stationInfoVo.setCurrentPrice(new BigDecimal("0.00"));
        }

        List<OmindPriceEntity> priceList = priceService.getPrice(stationId);
        List<PriceVo> priceVoList = new ArrayList<>();
        int priceSize = priceList.size();
        for(int i = 0;i < priceSize; i++){
            OmindPriceEntity priceEntity = priceList.get(i);
            PriceVo priceVo = new PriceVo();
            priceVo.setElecPrice(priceEntity.getElecPrice());
            priceVo.setServicePrice(priceEntity.getServicePrice());
            priceVo.setStartTm(DateUtils.parseDateToStr("HHmm", priceEntity.getStartTime()));
            if(i == priceSize - 1){
                priceVo.setEndTm("2359");
            }
            else{
                OmindPriceEntity nextPrice = priceList.get(i + 1);
                Date endTm = nextPrice.getStartTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(endTm);
                calendar.add(Calendar.MINUTE, -1);
                Date endTm59 = calendar.getTime();
                priceVo.setEndTm(DateUtils.parseDateToStr("HHmm", endTm59));
            }
            priceVoList.add(priceVo);
        }

        stationInfoVo.setPriceList(priceVoList);

        //------桩枪
        int totalGun = 0;
        int totalFreeGun = 0;
        int numFastGun = 0;
        int numFreeFastGun = 0;
        List<OmindEquipmentEntity> equipmentList = equipmentService.all(stationId);
        List<PileVo> pileVoList = new ArrayList<>();
        for(OmindEquipmentEntity equipmentEntity : equipmentList){
            PileVo pileVo = new PileVo();
            pileVo.setName(equipmentEntity.getEquipmentName());
            pileVo.setId(equipmentEntity.getId());
            pileVo.setEquipmentId(equipmentEntity.getEquipmentId());
            pileVo.setEquipmentType(equipmentEntity.getEquipmentType());

            List<OmindConnectorEntity> connectorList = connectorService.all(equipmentEntity.getEquipmentId());
            List<GunVo> gunVoList = new ArrayList<>();
            for(OmindConnectorEntity connectorEntity : connectorList){
                GunVo gunVo = new GunVo();
                gunVo.setId(connectorEntity.getId());
                gunVo.setConnectorId(connectorEntity.getConnectorId());
                gunVo.setConnectorName(connectorEntity.getConnectorName());
                gunVo.setStatus(connectorEntity.getStatus());
                gunVo.setConnectorType(connectorEntity.getConnectorType());
                gunVo.setParkStatus(connectorEntity.getParkStatus());
                gunVo.setPower(connectorEntity.getPower());
                gunVo.setChargingPer(new BigDecimal("0.00"));
                gunVo.setRemainMinute(0);
                gunVo.setStartTm(new Date());
                if(3 == connectorEntity.getStatus()){
                    OmindBillEntity omindBillEntity = omindBillService.getLatest(gunVo.getConnectorId());
                    if(omindBillEntity != null){
                        gunVo.setChargingPer(omindBillEntity.getSoc());
                        gunVo.setRemainMinute(0);
                        gunVo.setStartTm(omindBillEntity.getStartTime());
                    }
                }
                totalGun++;
                if(1 == connectorEntity.getStatus()){
                    totalFreeGun++;
                }
                if(4 == connectorEntity.getConnectorType()){
                    numFastGun++;
                    if(1 == connectorEntity.getStatus()){
                        totalFreeGun++;
                    }
                }
                gunVoList.add(gunVo);
            }
            pileVo.setGunList(gunVoList);
            pileVoList.add(pileVo);
        }

        stationInfoVo.setPileList(pileVoList);
        stationInfoVo.setTotalGun(totalGun);
        stationInfoVo.setFreeGun(totalFreeGun);
        stationInfoVo.setTotalFastGun(numFastGun);
        stationInfoVo.setFreeFastGun(numFreeFastGun);
        stationInfoVo.setBusineHours(!TextUtils.isBlank(stationEntity.getBusineHours()) ? stationEntity.getBusineHours() : "");

        return stationInfoVo;


    }
}
