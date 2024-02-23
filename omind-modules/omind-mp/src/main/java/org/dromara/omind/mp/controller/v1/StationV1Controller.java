package org.dromara.omind.mp.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.constant.HttpStatus;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.mp.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "StationV1Controller", description = "站点信息")
@RestController
@RequestMapping(value = "/v1/station/")
public class StationV1Controller {

    @Autowired
    HttpServletRequest request;

    @Autowired
    StationService stationService;

    @Operation(summary = "获取充电站详情")
    @GetMapping("{stationId}")
    public R stationInfo(@PathVariable String stationId){
        try {
            return R.ok(stationService.getDetailInfo(stationId));
        }
        catch (BaseException ex){
            log.error(ex.toString());
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error("stationInfo-error", ex);
            return R.fail(500, "服务器内部错误");
        }
    }

}
