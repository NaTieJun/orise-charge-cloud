package org.dromara.omind.baseplat.controller.operation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.baseplat.domain.request.StationStatsFullRequest;
import org.dromara.omind.baseplat.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "StatsV1Controller", description = "统计、监控、分析信息")
@RestController
@RequestMapping(value = "/hlht/v1/stats")
@Slf4j
public class StatsV1Controller {

    @Autowired
    @Lazy
    StatsService statsService;

    @Operation(summary = "单站点运营统计")
    @GetMapping("/stationStats")
    public R stationStats(StationStatsFullRequest request) {
        try {
            if(request == null || TextUtils.isBlank(request.getStationId())){
                return R.fail("充电站ID不能为空");
            }
            if(TextUtils.isBlank(request.getStartTm())){
                return R.fail("时间不能为空");
            }
            return R.ok(statsService.stationStats(request));
        } catch (BaseException ex) {
            log.error(ex.getMessage(), ex);
            return R.fail(ex.getMessage());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return R.fail("服务器内部错误");
        }
    }


}
