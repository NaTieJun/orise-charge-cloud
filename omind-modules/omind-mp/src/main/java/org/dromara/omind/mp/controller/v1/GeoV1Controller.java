package org.dromara.omind.mp.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.constant.HttpStatus;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.mp.domain.request.GeoStationListRequest;
import org.dromara.omind.mp.domain.request.SignRequest;
import org.dromara.omind.mp.service.GeoService;
import org.dromara.omind.mp.token.annotation.TokenCheck;
import org.dromara.omind.mp.utils.SignUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "GeoV1Controller", description = "地理信息")
@RestController
@RequestMapping(value = "/v1/geo/")
public class GeoV1Controller {

    @Autowired
    GeoService geoService;

    @Autowired
    SignUtil signUtil;

    @Autowired
    HttpServletRequest request;

    @TokenCheck
    @Operation(summary = "根据地图坐标，查询充电站列表")
    @GetMapping("geoStationList")
    public R geoStationList(GeoStationListRequest geoStationListRequest, SignRequest signRequest) {
        try {
            if (geoStationListRequest.getLat() == null || geoStationListRequest.getLon() == null) {
                return R.fail(HttpStatus.ERROR, "缺少经纬度参数");
            }
            if (geoStationListRequest.getUserLat() == null || geoStationListRequest.getUserLon() == null) {
                geoStationListRequest.setUserLat(geoStationListRequest.getLat());
                geoStationListRequest.setUserLon(geoStationListRequest.getLon());
            }
            return R.ok(geoService.geoList(geoStationListRequest, signRequest));
        } catch (BaseException ex) {
            log.error(ex.toString());
            return R.fail(ex.getMessage());
        } catch (Exception ex) {
            log.error(ex.toString());
            return R.fail("内部服务错误");
        }
    }


    @Operation(summary = "重新构建Geo缓存")
    @PostMapping("rebuildGeo")
    public R rebuildGeo() {
        try {
            return R.ok(geoService.rebuildGeoRedis());
        } catch (BaseException ex) {
            log.error(ex.toString());
            return R.fail(ex.getMessage());
        } catch (Exception ex) {
            log.error(ex.toString());
            return R.fail("内部服务错误");
        }
    }

}
