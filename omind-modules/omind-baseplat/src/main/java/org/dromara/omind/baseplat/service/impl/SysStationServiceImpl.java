package org.dromara.omind.baseplat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.baseplat.api.constant.HlhtRedisKey;
import org.dromara.omind.baseplat.api.domain.*;
import org.dromara.omind.baseplat.api.domain.dto.query.QuerySysChargeOrderDto;
import org.dromara.omind.baseplat.api.domain.dto.query.QuerySysStationDto;
import org.dromara.omind.baseplat.api.domain.entity.*;
import org.dromara.omind.baseplat.api.domain.request.LinkStationOperatorRequest;
import org.dromara.omind.baseplat.api.domain.request.QueryStationStatsData;
import org.dromara.omind.baseplat.api.domain.request.QueryStationStatusData;
import org.dromara.omind.baseplat.api.domain.request.QueryStationsInfoData;
import org.dromara.omind.baseplat.api.domain.response.QueryStationStatsResponseData;
import org.dromara.omind.baseplat.api.domain.response.QueryStationStatusResponseData;
import org.dromara.omind.baseplat.api.domain.response.QueryStationsInfoResponseData;
import org.dromara.omind.baseplat.domain.service.SysStationIService;
import org.dromara.omind.baseplat.domain.vo.StationVo;
import org.dromara.omind.baseplat.domain.vo.Stats4ConnectorDataVo;
import org.dromara.omind.baseplat.domain.vo.Stats4ConnectorVo;
import org.dromara.omind.baseplat.domain.vo.TreeNodeVo;
import org.dromara.omind.baseplat.service.*;
import org.dromara.system.api.RemoteAreaService;
import org.dromara.system.api.domain.vo.RemoteAreaVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class SysStationServiceImpl implements SysStationService {

    @Autowired
    @Lazy
    SysStationIService iService;

    @Autowired
    @Lazy
    SysStationService selfService;

    @Autowired
    @Lazy
    SysEquipmentService remoteSysEquipmentService;

    @Autowired
    @Lazy
    SysConnectorService connectorService;

    @Autowired
    @Lazy
    StationOperatorLinkService stationOperatorLinkService;

    @Autowired
    @Lazy
    SysChargeOrderService chargeOrderService;

    @Autowired
    @Lazy
    SysOperatorService operatorService;

    @DubboReference
    RemoteAreaService remoteAreaService;

    @Override
    public SysStation getStationById(String stationId) {
        if(TextUtils.isBlank(stationId)){
            return null;
        }
        String key = HlhtRedisKey.SYS_STATION + stationId;
        if(RedisUtils.hasKey(key)){
            SysStation sysStation = RedisUtils.getCacheObject(key);
            if(sysStation != null){
                return sysStation;
            }
        }
        LambdaQueryWrapper<SysStation> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysStation::getStationId, stationId);
        queryWrapper.orderByDesc(SysStation::getId);
        queryWrapper.last("limit 1");
        List<SysStation> list = iService.list(queryWrapper);
        if(list != null && list.size() > 0){
            SysStation sysStation =  list.get(0);
            int gunCount = 0;
            List<String> equipmentIdList = remoteSysEquipmentService.getAllEquipmentIdByStationId(stationId);
            if(equipmentIdList != null && equipmentIdList.size() > 0){
                sysStation.setPileNum(equipmentIdList.size());
                for(String equipmentId : equipmentIdList){
                    gunCount += connectorService.countByEquipmentId(equipmentId);
                }
            }
            sysStation.setGunNum(gunCount);
            RedisUtils.setCacheObject(key, sysStation);
            return sysStation;
        }
        return null;
    }

    @Override
    public Boolean isStationIdValid(String stationId) {
        if(TextUtils.isBlank(stationId)){
            return false;
        }
        if(selfService.getStationById(stationId) != null){
            return false;
        }
        return true;
    }

    @Override
    public TableDataInfo<SysStation> getStationPageList(QuerySysStationDto querySysStationDto, PageQuery pageQuery) {
        LambdaQueryWrapper<SysStation> lambdaQuery = Wrappers.lambdaQuery();
        if(!TextUtils.isBlank(querySysStationDto.getStationId())){
            lambdaQuery.eq(SysStation::getStationId, querySysStationDto.getStationId());
        }
        if(!TextUtils.isBlank(querySysStationDto.getStationName())){
            lambdaQuery.like(SysStation::getStationName, querySysStationDto.getStationName());
        }

        if(!TextUtils.isBlank(querySysStationDto.getOperatorId())){
            lambdaQuery.eq(SysStation::getOperatorId, querySysStationDto.getOperatorId());
        }

        if(!TextUtils.isBlank(querySysStationDto.getEquipmentOwnerId())){
            lambdaQuery.eq(SysStation::getEquipmentOwnerId, querySysStationDto.getEquipmentOwnerId());
        }

        if(!TextUtils.isBlank(querySysStationDto.getCountryCode())){
            lambdaQuery.eq(SysStation::getCountryCode, querySysStationDto.getCountryCode());
        }
        if(!TextUtils.isBlank(querySysStationDto.getAreaCode())){
            lambdaQuery.eq(SysStation::getAreaCode, querySysStationDto.getAreaCode());
        }
        if(!TextUtils.isBlank(querySysStationDto.getAddress())){
            lambdaQuery.like(SysStation::getAddress, querySysStationDto.getAddress());
        }
        if(querySysStationDto.getStationType() != null){
            lambdaQuery.eq(SysStation::getStationType, querySysStationDto.getStationType());
        }
        if(querySysStationDto.getStationStatus() != null){
            lambdaQuery.eq(SysStation::getStationStatus, querySysStationDto.getStationStatus());
        }
        lambdaQuery.orderByDesc(SysStation::getId);

        Page<SysStation> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize(), true);
        IPage<SysStation> iPage = iService.page(page, lambdaQuery);

        if(iPage != null && iPage.getRecords() != null){
            for(SysStation sysStation : iPage.getRecords()){
                if(!TextUtils.isBlank(sysStation.getStationId())){
                    String stationId = sysStation.getStationId();
                    int gunCount = 0;
                    List<String> equipmentIdList = remoteSysEquipmentService.getAllEquipmentIdByStationId(stationId);
                    if(equipmentIdList != null && equipmentIdList.size() > 0){
                        sysStation.setPileNum(equipmentIdList.size());
                        for(String equipmentId : equipmentIdList){
                            gunCount += connectorService.countByEquipmentId(equipmentId);
                        }
                    }
                    sysStation.setGunNum(gunCount);
                    String key = HlhtRedisKey.SYS_STATION + sysStation.getStationId();
                    RedisUtils.setCacheObject(key, sysStation);
                }
            }
        }

        TableDataInfo<SysStation> tableDataInfo = TableDataInfo.build(iPage);
        return tableDataInfo;
    }

    @Override
    public QueryStationsInfoResponseData getStationPageList(SysOperator sysOperator, QueryStationsInfoData queryStationsInfoData) {
        PageQuery pageQuery = new PageQuery();
        pageQuery.setPageNum(queryStationsInfoData.getPageNo());
        pageQuery.setPageSize(queryStationsInfoData.getPageSize());
        TableDataInfo<SysStation> tableDataInfo = stationOperatorLinkService.page4OperatorId(sysOperator.getOperatorId(), queryStationsInfoData, pageQuery);

        List<StationInfoData> stationInfoDataList = new ArrayList<>();
        if(tableDataInfo != null && tableDataInfo.getRows() != null) {
            for (SysStation sysStation : tableDataInfo.getRows()) {
                StationInfoData stationInfoData = StationInfoData.build(sysStation);
                stationInfoData.setOperatorID(sysOperator.getMyOperatorId());
                stationInfoData.setEquipmentInfos(remoteSysEquipmentService.getAllByStationId(stationInfoData.getStationID()));
                stationInfoDataList.add(stationInfoData);

                if(!TextUtils.isBlank(sysStation.getStationId())){
                    String key = HlhtRedisKey.SYS_STATION + sysStation.getStationId();
                    RedisUtils.setCacheObject(key, sysStation);
                }
            }
        }

        int pageCount = (int)(tableDataInfo.getTotal() / pageQuery.getPageSize() + ((tableDataInfo.getTotal() % pageQuery.getPageSize() > 0)?1:0));
        QueryStationsInfoResponseData responseData = new QueryStationsInfoResponseData();
        responseData.setPageCount(pageCount);
        responseData.setPageNo(queryStationsInfoData.getPageNo());
        responseData.setStationInfos(stationInfoDataList);
        responseData.setItemSize((int)tableDataInfo.getTotal());

        return responseData;
    }

    @Override
    public QueryStationStatusResponseData getStationStatusInfos(QueryStationStatusData queryStationStatusData) {
        List<String> stationIdList = null;
        try{
            stationIdList = queryStationStatusData.getStationIDs();//JSON.parseArray(queryStationStatusData.getStationIDs(), String.class);
        }
        catch (Exception ex){
            stationIdList = new ArrayList<>();
        }

        if(stationIdList != null && stationIdList.size() > 0){
            QueryStationStatusResponseData responseData = new QueryStationStatusResponseData();
            List<StationStatusInfoData>  statusInfoDataList = new ArrayList<>();
            for(String stationId : stationIdList){
                StationStatusInfoData statusInfoData = new StationStatusInfoData();
                statusInfoData.setStationID(stationId);
                statusInfoData.setConnectorStatusInfos(new ArrayList<>());
                List<String> equipmentIdList = remoteSysEquipmentService.getAllEquipmentIdByStationId(stationId);
                for(String equipmentId : equipmentIdList){
                    List<ConnectorStatusInfoData> connectorStatusInfoDataList = connectorService.getAllStatusByEquipmentId(equipmentId);
                    if(connectorStatusInfoDataList != null) {
                        statusInfoData.getConnectorStatusInfos().addAll(connectorStatusInfoDataList);
                    }
                }
                statusInfoDataList.add(statusInfoData);
            }
            responseData.setTotal(statusInfoDataList.size());
            responseData.setStationStatusInfos(statusInfoDataList);
            return responseData;
        }
        else{
            QueryStationStatusResponseData responseData = new QueryStationStatusResponseData();
            responseData.setStationStatusInfos(new ArrayList<>());
            responseData.setTotal(0);
            return responseData;
        }
    }

    @Override
    public QueryStationStatsResponseData getStationStatsInfos(QueryStationStatsData queryStationStatsData) {
        long start = 0;
        long end = 0;
        try{
            start = DateUtils.getMillionSceondsBydate(queryStationStatsData.getStartTime());
            end = DateUtils.getMillionSceondsBydate(queryStationStatsData.getEndTime());
            if(start == end){
                end = end +  24*3600*1000 - 1;
            }
        }
        catch (Exception ex){
            start = System.currentTimeMillis() - 24*3600*1000;
            end = System.currentTimeMillis();
        }

        QueryStationStatsResponseData responseData = new QueryStationStatsResponseData();
        StationStatsInfoData stationStatsInfoData = new StationStatsInfoData();
        responseData.setStationStats(stationStatsInfoData);

        //获取下面所有从点桩
        List<String> equipmentIdList = remoteSysEquipmentService.getAllEquipmentIdByStationId(queryStationStatsData.getStationID());
        stationStatsInfoData.setStationID(queryStationStatsData.getStationID());
        stationStatsInfoData.setStartTime(queryStationStatsData.getStartTime());
        stationStatsInfoData.setEndTime(queryStationStatsData.getEndTime());
        stationStatsInfoData.setEquipmentStatsInfos(new ArrayList<>());
        stationStatsInfoData.setStationElectricity(new BigDecimal("0.0"));
        BigDecimal stationTotalPower = new BigDecimal("0.00");
        for(String equipmentId : equipmentIdList){
            BigDecimal equipmentTotalPower = new BigDecimal("0.0");
            EquipmentStatsInfoData equipmentStatsInfoData = new EquipmentStatsInfoData();
            equipmentStatsInfoData.setEquipmentID(equipmentId);
            equipmentStatsInfoData.setConnectorStatsInfos(new ArrayList<>());
            equipmentStatsInfoData.setEquipmentElectricity(new BigDecimal("0.0"));
            List<ConnectorInfoData> connectorInfoList = connectorService.getAllByEquipmentId(equipmentId);

            for(ConnectorInfoData connectorInfoData : connectorInfoList){
                ConnectorStatsInfoData connectorStatsInfoData = chargeOrderService.getStatsInfo(connectorInfoData.getConnectorID(), start, end);
                equipmentStatsInfoData.getConnectorStatsInfos().add(connectorStatsInfoData);
                equipmentTotalPower = equipmentTotalPower.add(connectorStatsInfoData.getConnectorElectricity());
            }
            equipmentStatsInfoData.setEquipmentElectricity(equipmentTotalPower.setScale(1, RoundingMode.HALF_EVEN));
            stationTotalPower = stationTotalPower.add(equipmentTotalPower);
            stationStatsInfoData.getEquipmentStatsInfos().add(equipmentStatsInfoData);
        }
        stationStatsInfoData.setStationElectricity(stationTotalPower.setScale(1, RoundingMode.HALF_EVEN));
        return responseData;
    }

    @Override
    public List<SysStation> getAllByOperatorId(String operatorId) {
        if(TextUtils.isBlank(operatorId)){
            return new ArrayList<>();
        }
        LambdaQueryWrapper<SysStation> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysStation::getOperatorId, operatorId);
        List<SysStation> list = iService.list(queryWrapper);
        if(list == null){
            list = new ArrayList<>();
        }
        return list;
    }

    @Override
    public List<String> getAllOpenStationId() {
        List<String> stationIdList = new ArrayList<>();
        LambdaQueryWrapper<SysStation> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.select(SysStation::getStationId, SysStation::getStationStatus);
        queryWrapper.eq(SysStation::getStationStatus, 50);
        List<SysStation> list = iService.list(queryWrapper);
        if(list != null){
            for(SysStation sysStation : list){
                if(!TextUtils.isBlank(sysStation.getStationId())) {
                    stationIdList.add(sysStation.getStationId());
                }
            }
        }
        return stationIdList;
    }

    @Override
    public Boolean save(SysStation sysStation) {
        if(sysStation != null) {
            if(sysStation.getOperatorId() != null){
                String key = HlhtRedisKey.SYS_LINK_STATION_OPERATOR_LIST_BY_OPERATOR_ID + sysStation.getOperatorId();
                RedisUtils.deleteObject(key);
            }

            if(iService.save(sysStation)){
                //自动添加一个关联
                LinkStationOperatorRequest linkStationOperatorRequest = new LinkStationOperatorRequest();
                linkStationOperatorRequest.setStationId(sysStation.getStationId());
                linkStationOperatorRequest.setOperatorId(sysStation.getOperatorId());
                linkStationOperatorRequest.setIsSyncTrade((short)0);
                linkStationOperatorRequest.setIsEnable((short)1);
                linkStationOperatorRequest.setRemark("");
                stationOperatorLinkService.linkOperatorAndStation(linkStationOperatorRequest);
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }

    @Override
    public Boolean updateById(SysStation sysStation) {
        if(sysStation.getId() == null || sysStation.getId() <= 0){
            throw new BaseException("充电站ID错误");
        }
        SysStation oldData = iService.getById(sysStation.getId());
        if(oldData == null){
            throw new BaseException("充电站ID错误");
        }

        if(sysStation.getStationId() != null) {
            if (!oldData.getStationId().equals(sysStation.getStationId())) {
                throw new BaseException("充电站ID不可修改");
            }
        }

        if(sysStation.getOperatorId() != null){
            SysOperator sysOperator = operatorService.getOperatorById(sysStation.getOperatorId());
            if(sysOperator == null){
                throw new BaseException("未找到运营商");
            }

        }

        if(sysStation == null || sysStation.getId() == null || sysStation.getId() <= 0){
            throw new BaseException("充电站数据错误");
        }
        if(iService.updateById(sysStation)){
            String key = HlhtRedisKey.SYS_STATION + sysStation.getStationId();
            RedisUtils.deleteObject(key);
            return true;
        }
        else {
            throw new BaseException("充电站更新失败");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean remove(SysStation sysStation) {
        if(sysStation == null || sysStation.getId() == null || sysStation.getId() <= 0){
            return false;
        }
        else
        {
            RedisUtils.deleteObject(HlhtRedisKey.SYS_STATION + sysStation.getStationId());
            if(sysStation.getOperatorId() != null){
                String key = HlhtRedisKey.SYS_LINK_STATION_OPERATOR_LIST_BY_OPERATOR_ID + sysStation.getOperatorId();
                RedisUtils.deleteObject(key);
            }
            if(!iService.removeById(sysStation.getId())){
                throw new BaseException("删除站点失败");
            }
            if(!stationOperatorLinkService.delLink4Station(sysStation.getStationId()))
            {
                throw new BaseException("删除站点失败-删除关联运营商失败");
            }
            return true;
        }
    }

    @Override
    public List<TreeNodeVo> allTree() {
        List<SysStation> stationList = iService.list();

        List<TreeNodeVo> nodeList = new ArrayList<>();
        Map<String, TreeNodeVo> nodeMap = new LinkedHashMap<>();

        for(SysStation station : stationList){
            if(TextUtils.isBlank(station.getAreaCode()) || station.getAreaCode().length() < 2){
                continue;
            }
            StationVo stationVo = StationVo.build(station);
            String areaKey = station.getAreaCode().substring(0, 2);
            if(nodeMap.containsKey(areaKey)){
                TreeNodeVo node = nodeMap.get(areaKey);
                node.getStationList().add(stationVo);
            }
            else{
                TreeNodeVo node = new TreeNodeVo();
                RemoteAreaVo areaVo = remoteAreaService.getByAreaCode(Integer.valueOf(areaKey));
                node.setAreaVo(areaVo);
                node.setAreaCode(Integer.valueOf(areaKey));

                nodeMap.put(areaKey, node);
                node.getStationList().add(stationVo);
                nodeList.add(node);
            }
        }

        nodeList.sort(new Comparator<TreeNodeVo>() {
            @Override
            public int compare(TreeNodeVo o1, TreeNodeVo o2) {
                if(o1.getAreaCode() > o2.getAreaCode()) {
                    return 1;
                }
                return 0;
            }
        });

        return nodeList;
    }

    @Override
    public Stats4ConnectorVo getGunsShow(String stationId) {

        SysStation sysStation = selfService.getStationById(stationId);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startTm = calendar.getTime();

        QuerySysChargeOrderDto querySysChargeOrderDto = new QuerySysChargeOrderDto();
        querySysChargeOrderDto.setStationId(stationId);
        querySysChargeOrderDto.setStartTm(startTm);
        querySysChargeOrderDto.setEndTm(new Date());

        List<SysChargeOrder> chargeOrderList = chargeOrderService.getChargeOrderList(querySysChargeOrderDto);

        List<EquipmentInfoData> equipmentList = remoteSysEquipmentService.getAllByStationId(stationId);
        List<SysConnector> connectorList = new ArrayList<>();
        if(equipmentList != null){
            for(EquipmentInfoData equipmentInfoData : equipmentList) {
                List<SysConnector> cList = connectorService.getAllConnectorByEquipmentId(equipmentInfoData.getEquipmentID(), true);
                if(cList != null && cList.size() > 0){
                    connectorList.addAll(cList);
                }
            }
        }

        Stats4ConnectorVo stats4ConnectorVo = new Stats4ConnectorVo();
        if(sysStation != null) {
            stats4ConnectorVo.setStationName(sysStation.getStationName());
        }
        stats4ConnectorVo.setConnectorCount(connectorList.size());
        for(SysConnector sysConnector : connectorList){
            //0、离网;1、空闲;2、占用(未充电);3、占用(充电中);4、占用(预约锁定);255、故障
            if(sysConnector.getStatus() == 0) {
                stats4ConnectorVo.setOfflineCount(stats4ConnectorVo.getOfflineCount() + 1);
            }
            else if(sysConnector.getStatus() == 1){
                stats4ConnectorVo.setFreeCount(stats4ConnectorVo.getFreeCount() + 1);
            }
            else if(sysConnector.getStatus() == 2){
                stats4ConnectorVo.setUnChargeCount(stats4ConnectorVo.getUnChargeCount() + 1);
            }
            else if(sysConnector.getStatus() == 3){
                stats4ConnectorVo.setChargingCount(stats4ConnectorVo.getChargingCount() + 1);
            }
            else if(sysConnector.getStatus() == 255){
                stats4ConnectorVo.setBreakDownCount(stats4ConnectorVo.getBreakDownCount() + 1);
            }
            else{
                stats4ConnectorVo.setUnkownCount(stats4ConnectorVo.getUnkownCount() + 1);
            }

            Stats4ConnectorDataVo dataVo = new Stats4ConnectorDataVo();
            dataVo.setConnectorId(sysConnector.getConnectorId());
            dataVo.setStationId(stationId);
            dataVo.setConnectorId(sysConnector.getConnectorId());
            dataVo.setConnectorStatus(sysConnector.getStatus());
            if(sysConnector != null && sysConnector.getStatus() != null && sysConnector.getStatus() == 3){
                SysChargeOrder chargeOrder = chargeOrderService.getChargingOrderByConnectorId(sysConnector.getConnectorId());
                if(chargeOrder != null){
                    dataVo.setCurrentA(chargeOrder.getCurrentA());
                    dataVo.setVoltageA(chargeOrder.getVoltageA());
                    dataVo.setSoc(chargeOrder.getSoc());
                    dataVo.setChargePower(chargeOrder.getTotalPower());
                    dataVo.setStartChargeSeq(chargeOrder.getStartChargeSeq());
                }

                for(SysChargeOrder sysChargeOrder : chargeOrderList){
                    if(!TextUtils.isBlank(sysChargeOrder.getConnectorId())
                            && sysChargeOrder.getConnectorId().equals(sysConnector.getConnectorId())){
                        dataVo.chargeCount++;
                        dataVo.setTotalPower(dataVo.getTotalPower().add(sysChargeOrder.getTotalPower()));
                        long dur = sysChargeOrder.getEndTime().getTime() - sysChargeOrder.getStartTime().getTime();
                        if(dur > 0){
                            dur = dur / 1000 / 60;
                            dataVo.setTotalChargeDura(dataVo.getTotalChargeDura() + (int)dur);
                        }
                    }
                }
            }
            stats4ConnectorVo.getConnectorList().add(dataVo);
        }
        return stats4ConnectorVo;
    }
}
