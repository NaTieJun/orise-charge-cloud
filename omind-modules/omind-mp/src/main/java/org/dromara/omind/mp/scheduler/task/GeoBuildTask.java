package org.dromara.omind.mp.scheduler.task;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.dromara.omind.mp.service.GeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GeoBuildTask {

    @Autowired
    GeoService geoService;

    @XxlJob("GeoBuildTask")
    public void execute()
    {
        log.info("更新地图");
        geoService.rebuildGeoRedis();
    }

}
