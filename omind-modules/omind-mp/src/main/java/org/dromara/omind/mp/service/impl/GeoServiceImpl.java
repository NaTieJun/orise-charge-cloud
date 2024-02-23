package org.dromara.omind.mp.service.impl;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.log4j.Log4j2;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.http.util.TextUtils;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.mp.constant.XcxRedisKey;
import org.dromara.omind.mp.domain.request.GeoStationListRequest;
import org.dromara.omind.mp.domain.request.SignRequest;
import org.dromara.omind.mp.domain.vo.StationVo;
import org.dromara.omind.mp.service.GeoService;
import org.dromara.omind.userplat.api.domain.entity.OmindConnectorEntity;
import org.dromara.omind.userplat.api.domain.entity.OmindPriceEntity;
import org.dromara.omind.userplat.api.domain.entity.OmindStationEntity;
import org.dromara.omind.userplat.api.service.RemoteOmindConnectorService;
import org.dromara.omind.userplat.api.service.RemoteOmindPriceService;
import org.dromara.omind.userplat.api.service.RemoteOmindStationService;
import org.dromara.omind.userplat.api.utils.GeoHashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Metrics;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Log4j2
@Service
public class GeoServiceImpl implements GeoService {

    @DubboReference
    RemoteOmindStationService stationService;

    @DubboReference
    RemoteOmindPriceService priceService;

    @Autowired
    GeoHashUtil geoHashUtil;

    @DubboReference
    RemoteOmindConnectorService connectorService;

    @Override
    public int rebuildGeoRedis() {
        String oldGeoTm = "";
        if (RedisUtils.hasKey(XcxRedisKey.STATION_GEO_TM)) {
            try {
                oldGeoTm = RedisUtils.getCacheObject(XcxRedisKey.STATION_GEO_TM).toString();
            } catch (Exception ex) {
                log.error(ex.toString());
            }
        }
        String newGeoTm = (System.currentTimeMillis() / 1000) + "";

        List<OmindStationEntity> list = stationService.getAllGeoData();
        if (list != null) {
            for (OmindStationEntity station : list) {
                geoHashUtil.geoAdd(XcxRedisKey.STATION_GEO + newGeoTm, station.getStationLng().doubleValue(), station.getStationLat().doubleValue(), station.getStationId());
            }
            RedisUtils.setCacheObject(XcxRedisKey.STATION_GEO_TM, newGeoTm, Duration.ofDays(30));
            if (!TextUtils.isBlank(oldGeoTm)) {
                RedisUtils.expire(XcxRedisKey.STATION_GEO + oldGeoTm, 60 * 30);
            }
            return list.size();
        } else {
            return 0;
        }
    }

    @Override
    public List<StationVo> geoList(GeoStationListRequest geoStationListRequest, SignRequest signRequest) {
        double radius = geoStationListRequest.getRadius() == null ? 50 : geoStationListRequest.getRadius().doubleValue();
        int limit = geoStationListRequest.getLimit() == null ? 200 : geoStationListRequest.getLimit().intValue();
        boolean isDetail = geoStationListRequest.getIsDetail() == null ? false : geoStationListRequest.getIsDetail() == 1;

        if (!RedisUtils.hasKey(XcxRedisKey.STATION_GEO_TM)) {
            return new ArrayList<>();
        }
        String geoTmKey = RedisUtils.getCacheObject(XcxRedisKey.STATION_GEO_TM).toString();

        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> geoList = geoHashUtil.findRadius(
                XcxRedisKey.STATION_GEO + geoTmKey,
                geoStationListRequest.getLon().doubleValue(),
                geoStationListRequest.getLat().doubleValue(),
                radius,
                Metrics.KILOMETERS,
                limit);

        List<StationVo> stationList = new ArrayList<>();
        if (!isDetail) {
            //简要情况
            for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : geoList) {
                StationVo geoStationVo = new StationVo();
                geoStationVo.setStationId(result.getContent().getName());
                BigDecimal lon = new BigDecimal(result.getContent().getPoint().getX());
                lon = lon.setScale(6, RoundingMode.DOWN);
                BigDecimal lat = new BigDecimal(result.getContent().getPoint().getY());
                lat = lat.setScale(6, RoundingMode.DOWN);
                geoStationVo.setLon(lon);
                geoStationVo.setLat(lat);
                stationList.add(geoStationVo);
            }
        } else {
            //详细数据
            for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : geoList) {
                StationVo stationVo = new StationVo();
                String stationId = result.getContent().getName();
                OmindStationEntity stationInfoEntity = stationService.get(stationId);
                if (stationInfoEntity != null) {
                    stationVo.setStationId(stationId);
                    stationVo.setId(stationVo.getId());
                    stationVo.setStationStatus(stationInfoEntity.getStationStatus());
                    BigDecimal lon = new BigDecimal(result.getContent().getPoint().getX());
                    lon = lon.setScale(6, RoundingMode.DOWN);
                    BigDecimal lat = new BigDecimal(result.getContent().getPoint().getY());
                    lat = lat.setScale(6, RoundingMode.DOWN);
                    stationVo.setLon(lon);
                    stationVo.setLat(lat);
                    double disValue = result.getDistance().getValue();
                    Metric metric = result.getDistance().getMetric();
                    if (metric == Metrics.KILOMETERS) {
                        stationVo.setDistance(new BigDecimal(disValue));
                    } else if (metric == Metrics.MILES) {
                        stationVo.setDistance(new BigDecimal(disValue * 1.61));
                    }
                    stationVo.setInfo(stationInfoEntity);
                    List<String> picUrlList = new ArrayList<>();
                    if (!TextUtils.isBlank(stationInfoEntity.getPictures())) {
                        String picStr = stationInfoEntity.getPictures();
                        try {
                            JSONArray jsonArray = JSONArray.parseArray(picStr);
                            if (jsonArray != null && jsonArray.size() > 0) {
                                int size = jsonArray.size();
                                for (int i = 0; i < size; i++) {
                                    String path = jsonArray.getString(i);
                                    //String url = minioS3Utils.getAccessUrl(path);
                                    //todo images
//                                    if (!TextUtils.isBlank(url)) {
//                                        picUrlList.add(url);
//                                    }
                                }
                            }
                        } catch (Exception ex) {
                            log.error(ex.toString());
                        }
                    }

                    stationVo.setImgs(picUrlList);

                }
                OmindPriceEntity currentPrice = priceService.getPriceCurrent(stationId);
                if(currentPrice == null){
                    currentPrice = new OmindPriceEntity();
                    currentPrice.setElecPrice(new BigDecimal("0.00"));
                    currentPrice.setServicePrice(new BigDecimal("0.00"));
                }
                stationVo.setCurrentPrice(currentPrice.getElecPrice().add(currentPrice.getServicePrice()));
                List<OmindConnectorEntity> allConnectorList = connectorService.allByStationId(stationId);
                int totalGun = allConnectorList.size();
                int freeGun = 0;
                int totalFast = 0;
                int freeFast = 0;
                for(OmindConnectorEntity connectorEntity : allConnectorList){
                    if(4 == connectorEntity.getConnectorType()){
                        totalFast++;
                    }
                    if(1 == connectorEntity.getStatus()){
                        freeGun++;
                        if(4 == connectorEntity.getConnectorType()){
                            freeFast++;
                        }
                    }
                }

                stationVo.setTotalGun(totalGun);
                stationVo.setFreeGun(freeGun);
                stationVo.setTotalFastGun(totalFast);
                stationVo.setFreeFastGun(freeFast);

                stationList.add(stationVo);
            }
        }

        if(geoStationListRequest.getUserLat() != null && geoStationListRequest.getUserLon() != null){
            for(StationVo vo : stationList){
                //计算站点距离
                double distance = geoHashUtil.getDistance(geoStationListRequest.getUserLon().doubleValue(), geoStationListRequest.getUserLat().doubleValue(),
                        vo.getLon().doubleValue(), vo.getLat().doubleValue());
                if(distance <= 0){
                    distance = 0.0f;
                }
                vo.setUserDistance(new BigDecimal(distance));
            }
        }

        stationList.sort(new Comparator<StationVo>() {
            @Override
            public int compare(StationVo o1, StationVo o2) {
                return o1.getUserDistance().compareTo(o2.getUserDistance());
            }
        });

        return stationList;
    }
}
