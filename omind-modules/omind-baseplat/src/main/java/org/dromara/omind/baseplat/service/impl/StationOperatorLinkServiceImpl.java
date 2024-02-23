package org.dromara.omind.baseplat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.log4j.Log4j2;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.baseplat.api.constant.HlhtRedisKey;
import org.dromara.omind.baseplat.api.domain.entity.SysOperator;
import org.dromara.omind.baseplat.api.domain.entity.SysStation;
import org.dromara.omind.baseplat.api.domain.entity.SysStationOperatorLink;
import org.dromara.omind.baseplat.api.domain.request.LinkStationOperatorRequest;
import org.dromara.omind.baseplat.api.domain.request.LinkStationOperatorUpdateRequest;
import org.dromara.omind.baseplat.api.domain.request.QueryStationsInfoData;
import org.dromara.omind.baseplat.domain.service.SysStationOperatorLinkIService;
import org.dromara.omind.baseplat.service.StationOperatorLinkService;
import org.dromara.omind.baseplat.service.SysOperatorService;
import org.dromara.omind.baseplat.service.SysStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log4j2
@Service
public class StationOperatorLinkServiceImpl implements StationOperatorLinkService {

    @Autowired
    SysStationOperatorLinkIService iService;

    @Autowired
    @Lazy
    StationOperatorLinkService selfService;

    @Autowired
    SysOperatorService operatorService;

    @Autowired
    SysStationService stationService;

    @Override
    public List<SysStationOperatorLink> getList4OperatorId(String operatorId) {
        if(TextUtils.isBlank(operatorId)){
            return new ArrayList<>();
        }
        String key = HlhtRedisKey.SYS_LINK_STATION_OPERATOR_LIST_BY_OPERATOR_ID + operatorId;
        List<SysStationOperatorLink> resultList = RedisUtils.getCacheObject(key);
        if(resultList != null){
            return resultList;
        }

        resultList = new ArrayList<>();

        SysOperator operatorInfo = operatorService.getOperatorById(operatorId);
        //再查询关联的
        LambdaQueryWrapper<SysStationOperatorLink> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysStationOperatorLink::getOperatorId, operatorId);
        List<SysStationOperatorLink> linkList = iService.list(queryWrapper);
        if(linkList != null){
            for(SysStationOperatorLink link : linkList){
                SysStation sysStation = stationService.getStationById(link.getStationId());
                if(sysStation != null){
                    link.setOperatorInfo(operatorInfo);
                    link.setStationInfo(sysStation);
                    resultList.add(link);
                }

            }
        }
        if(resultList != null){
            RedisUtils.setCacheObject(key, resultList);
        }
        return resultList;
    }

    @Override
    public List<SysStationOperatorLink> getList4Station(String stationId) {
        if(TextUtils.isBlank(stationId)){
            return new ArrayList<>();
        }
        String key = HlhtRedisKey.SYS_LINK_STATION_OPERATOR_LIST_BY_STATION_ID + stationId;
        List<SysStationOperatorLink> resultList = RedisUtils.getCacheObject(key);
        if(resultList != null) {
            return resultList;
        }

        resultList = new ArrayList<>();

        SysStation sysStation = stationService.getStationById(stationId);
        if(sysStation == null){
            return new ArrayList<>();
        }

        //再查询关联的
        LambdaQueryWrapper<SysStationOperatorLink> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysStationOperatorLink::getStationId, stationId);
        List<SysStationOperatorLink> linkList = iService.list(queryWrapper);
        if(linkList != null){
            for(SysStationOperatorLink link : linkList){
                SysOperator operator = operatorService.getOperatorById(link.getOperatorId());
                if(operator != null){
                    link.setStationInfo(sysStation);
                    link.setOperatorInfo(operator);
                    resultList.add(link);
                }

            }
        }
        if(resultList != null){
            RedisUtils.setCacheObject(key, resultList);
        }
        return resultList;
    }

    @Override
    public Boolean linkOperatorAndStation(LinkStationOperatorRequest linkStationOperatorRequest) {
        if(linkStationOperatorRequest == null){
            throw new BaseException("参数错误");
        }
        if(TextUtils.isBlank(linkStationOperatorRequest.getOperatorId())){
            throw new BaseException("充电站ID无效");
        }
        if(TextUtils.isBlank(linkStationOperatorRequest.getOperatorId())){
            throw new BaseException("运营商ID无效");
        }
        SysStation sysStation = stationService.getStationById(linkStationOperatorRequest.getStationId());
        if(sysStation == null){
            throw new BaseException("充电站不存在");
        }
        SysOperator operator = operatorService.getOperatorById(linkStationOperatorRequest.getOperatorId());
        if(operator == null){
            throw new BaseException("运营商不存在");
        }
        LambdaQueryWrapper<SysStationOperatorLink> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysStationOperatorLink::getOperatorId, linkStationOperatorRequest.getOperatorId());
        queryWrapper.eq(SysStationOperatorLink::getStationId, linkStationOperatorRequest.getStationId());
        long count = iService.count(queryWrapper);
        if(count > 0){
            throw new BaseException("关联已存在");
        }
        SysStationOperatorLink link = new SysStationOperatorLink();
        link.setOperatorId(linkStationOperatorRequest.getOperatorId());
        link.setStationId(linkStationOperatorRequest.getStationId());
        link.setIsSyncTrade(linkStationOperatorRequest.getIsSyncTrade());
        link.setRemark(linkStationOperatorRequest.getRemark());
        link.setIsEnable(linkStationOperatorRequest.getIsEnable());
        link.setIsEnable((short)0);
        if(iService.save(link)){
            String key = HlhtRedisKey.SYS_LINK_STATION_OPERATOR_LIST_BY_OPERATOR_ID + link.getOperatorId();
            String key2 = HlhtRedisKey.SYS_LINK_STATION_OPERATOR_LIST_BY_STATION_ID + link.getStationId();
            RedisUtils.deleteObject(key);
            RedisUtils.deleteObject(key2);
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public Boolean updateLink(LinkStationOperatorUpdateRequest linkStationOperatorUpdateRequest) {
        if(linkStationOperatorUpdateRequest.getId() == null || linkStationOperatorUpdateRequest.getId() <= 0){
            throw new BaseException("id无效");
        }
        SysStationOperatorLink link = iService.getById(linkStationOperatorUpdateRequest.getId());
        if(link == null){
            throw new BaseException("id无效");
        }
        SysStationOperatorLink updateLink = new SysStationOperatorLink();
        updateLink.setId(linkStationOperatorUpdateRequest.getId());
        if(linkStationOperatorUpdateRequest.getIsEnable() != null
                && linkStationOperatorUpdateRequest.getIsEnable() >= 0
                && linkStationOperatorUpdateRequest.getIsEnable() <= 1
        ){
            updateLink.setIsEnable(linkStationOperatorUpdateRequest.getIsEnable());
        }
        if(linkStationOperatorUpdateRequest.getIsSyncTrade() != null
                && linkStationOperatorUpdateRequest.getIsSyncTrade() >= 0
                && linkStationOperatorUpdateRequest.getIsSyncTrade() <= 1
        )
        {
            updateLink.setIsSyncTrade(linkStationOperatorUpdateRequest.getIsSyncTrade());
        }
        if(linkStationOperatorUpdateRequest.getRemark() != null){
            updateLink.setRemark(linkStationOperatorUpdateRequest.getRemark());
        }
        if(iService.updateById(updateLink)){
            String key = HlhtRedisKey.SYS_LINK_STATION_OPERATOR_LIST_BY_OPERATOR_ID + link.getOperatorId();
            String key2 = HlhtRedisKey.SYS_LINK_STATION_OPERATOR_LIST_BY_STATION_ID + link.getStationId();
            RedisUtils.deleteObject(key);
            RedisUtils.deleteObject(key2);
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public Boolean delLink(Long id) {
        if(id == null || id <= 0){
            throw new BaseException("id无效");
        }
        SysStationOperatorLink link = iService.getById(id);
        if(link == null){
            throw new BaseException("id无效");
        }
        if(iService.removeById(id)){
            String key = HlhtRedisKey.SYS_LINK_STATION_OPERATOR_LIST_BY_OPERATOR_ID + link.getOperatorId();
            String key2 = HlhtRedisKey.SYS_LINK_STATION_OPERATOR_LIST_BY_STATION_ID + link.getStationId();
            RedisUtils.deleteObject(key);
            RedisUtils.deleteObject(key2);
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public Boolean delLink4Operator(String operatorId) {
        List<SysStationOperatorLink> links = selfService.getList4OperatorId(operatorId);
        List<String> keyList = new ArrayList<>();
        keyList.add(HlhtRedisKey.SYS_LINK_STATION_OPERATOR_LIST_BY_OPERATOR_ID + operatorId);
        for(SysStationOperatorLink link : links){
            link.setDelFlag((short)1);
            String key = HlhtRedisKey.SYS_LINK_STATION_OPERATOR_LIST_BY_STATION_ID + link.getStationId();
            RedisUtils.deleteObject(key);
        }

        if(links != null && links.size() > 0) {
            return iService.updateBatchById(links);
        }
        else{
            return true;
        }
    }

    @Override
    public Boolean delLink4Station(String stationId) {
        List<SysStationOperatorLink> links = selfService.getList4Station(stationId);
        List<String> keyList = new ArrayList<>();
        keyList.add(HlhtRedisKey.SYS_LINK_STATION_OPERATOR_LIST_BY_STATION_ID + stationId);
        for(SysStationOperatorLink link : links){
            link.setDelFlag((short)1);
            String key = HlhtRedisKey.SYS_LINK_STATION_OPERATOR_LIST_BY_OPERATOR_ID + link.getOperatorId();
            RedisUtils.deleteObject(key);
        }
        if(links != null && links.size() > 0) {
            return iService.updateBatchById(links);
        }
        else{
            return true;
        }
    }

    @Override
    public void stationUpdate(String stationId) {
        List<SysStationOperatorLink> links = selfService.getList4Station(stationId);
        Date now = new Date();
        for(SysStationOperatorLink link : links){
            link.setUpdateTime(now);
        }
        if(links != null && links.size() > 0) {
            iService.updateBatchById(links);
        }
    }

    @Override
    public TableDataInfo<SysStation> page4OperatorId(String operatorId, QueryStationsInfoData queryStationsInfoData, PageQuery pageQuery) {
        LambdaQueryWrapper<SysStationOperatorLink> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(SysStationOperatorLink::getOperatorId, operatorId);
        Date lastTm = null;
        if(!TextUtils.isBlank(queryStationsInfoData.getLastQueryTime())) {
            try {
                lastTm = new Date(DateUtils.getMillionSceondsBydate(queryStationsInfoData.getLastQueryTime()));
            } catch (Exception ex) {

            }
            if (lastTm != null) {
                lambdaQuery.ge(SysStationOperatorLink::getUpdateTime, DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, lastTm));
            }
        }

        Page<SysStationOperatorLink> page = iService.page(pageQuery.build(), lambdaQuery);
        List<SysStation> stationList = new ArrayList<>();
        for(SysStationOperatorLink link : page.getRecords()){
            SysStation sysStation = stationService.getStationById(link.getStationId());
            if(sysStation != null){
                stationList.add(sysStation);
            }
        }
        IPage<SysStation> resultPage = new Page<>();
        resultPage.setPages(page.getPages());
        resultPage.setRecords(stationList);
        resultPage.setTotal(page.getTotal());
        resultPage.setCurrent(page.getCurrent());
        resultPage.setSize(stationList.size());

        return TableDataInfo.build(resultPage);
    }

}
