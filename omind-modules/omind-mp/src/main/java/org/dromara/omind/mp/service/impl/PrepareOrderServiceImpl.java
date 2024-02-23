package org.dromara.omind.mp.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.omind.mp.domain.vo.PrepareChargingVo;
import org.dromara.omind.mp.domain.vo.PriceVo;
import org.dromara.omind.mp.service.PrepareOrderService;
import org.dromara.omind.userplat.api.domain.entity.*;
import org.dromara.omind.userplat.api.service.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class PrepareOrderServiceImpl implements PrepareOrderService {

    @DubboReference
    RemoteOmindStationService stationService;

    @DubboReference
    RemoteOmindEquipmentService equipmentService;

    @DubboReference
    RemoteOmindConnectorService connectorService;

    @DubboReference
    RemoteOmindBillService billService;

    @DubboReference
    RemoteOmindPriceService priceService;

    @Override
    public PrepareChargingVo prepareOrderInfo(Long uid, String connectorId, Short hasPrice) {
        if(TextUtils.isBlank(connectorId)){
            throw new BaseException("充电接口编号错误");
        }

        OmindConnectorEntity connectorEntity = connectorService.get(connectorId);
        if(connectorEntity == null){
            throw new BaseException("充电接口编号错误");
        }
        OmindEquipmentEntity equipmentEntity = equipmentService.get(connectorEntity.getEquipmentId());
        if(equipmentEntity == null){
            throw new BaseException("充电设备编号错误");
        }
        OmindStationEntity stationEntity = stationService.get(equipmentEntity.getStationId());
        if(stationEntity == null){
            throw new BaseException("充电站编号错误");
        }

        PrepareChargingVo prepareChargingVo = new PrepareChargingVo();
        prepareChargingVo.setStationId(stationEntity.getStationId());
        prepareChargingVo.setStationName(stationEntity.getStationName());
        prepareChargingVo.setEquipmentId(equipmentEntity.getEquipmentId());
        prepareChargingVo.setType(equipmentEntity.getEquipmentType());
        prepareChargingVo.setPower(connectorEntity.getPower());

        prepareChargingVo.setStartChargeSeq("");
        prepareChargingVo.setPlateNo("");

        if(connectorEntity != null) {
            prepareChargingVo.setStatus(connectorEntity.getStatus().intValue());
            if(connectorEntity.getStatus() == 3){
                OmindBillEntity lastBill = billService.getLatest(connectorId);
                if(lastBill == null || lastBill.getUserId() == null || lastBill.getUserId() <= 0){
                    throw new BaseException("充电桩被占用");
                }
                else if(lastBill.getUserId().equals(uid) && lastBill.getStartChargeSeqStat() < 4){
                    prepareChargingVo.setStartChargeSeq(lastBill.getStartChargeSeq());
                    String plateNo = lastBill.getPlateNo() != null ? lastBill.getPlateNo() : "";
                    prepareChargingVo.setPlateNo(plateNo);
                }
                else{
                    throw new BaseException("充电桩被占用");
                }
            }
        }
        else{
            prepareChargingVo.setStatus(0);
        }

        if(hasPrice != null && hasPrice == 1){
            List<OmindPriceEntity> priceList = priceService.getPrice(stationEntity.getStationId());
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
            prepareChargingVo.setPriceList(priceVoList);
        }

        return prepareChargingVo;
    }
}
