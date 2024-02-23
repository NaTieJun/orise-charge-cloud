package org.dromara.omind.userplat.scheduler.task;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.userplat.api.domain.entity.OmindOperatorEntity;
import org.dromara.omind.userplat.api.domain.request.QueryStationsInfoData;
import org.dromara.omind.userplat.service.OmindOperatorService;
import org.dromara.omind.userplat.service.hlht.HlhtInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class HlhtStaionInfoSyncTask {

    @Autowired
    @Lazy
    HlhtInfoService hlhtInfoService;

    @Autowired
    @Lazy
    OmindOperatorService omindOperatorService;

    @XxlJob("stationSyncTask")
    public void taskHlhtStationInfoSyscHandle(){
        List<OmindOperatorEntity> operatorList = omindOperatorService.selectOperatorList();
        if (operatorList.size() > 0) {
            int pageNo = 1;
            int pageSize = 10;
            int pageCount = 0;
            for (OmindOperatorEntity odOperator : operatorList) {
                try {
                    if (odOperator != null) {
                        if (!TextUtils.isBlank(odOperator.getApiUrl()) && odOperator.getApiUrl().startsWith("http")) {//apiurl不为空的进行处理
                            QueryStationsInfoData queryStationsInfoData = new QueryStationsInfoData();
                            queryStationsInfoData.setLastQueryTime("");
                            queryStationsInfoData.setPageSize(pageSize);
                            queryStationsInfoData.setPageNo(pageNo);
                            pageCount = hlhtInfoService.stationsInfo(queryStationsInfoData, odOperator);
                            if (pageCount > pageNo) {//分页请求剩余数据
                                for (int i = 2; i <= pageCount; i++) {
                                    QueryStationsInfoData newqueryStationsInfoData = new QueryStationsInfoData();
                                    newqueryStationsInfoData.setLastQueryTime("");
                                    newqueryStationsInfoData.setPageSize(pageSize);
                                    newqueryStationsInfoData.setPageNo(i);
                                    hlhtInfoService.stationsInfo(newqueryStationsInfoData, odOperator);
                                }
                            }
                        }
                    }
                } catch (BaseException ube) {
                    log.error("task-taskHlhtStationInfoSyscHandle-error", ube);
                } catch (Exception e) {
                    log.error("task-taskHlhtStationInfoSyscHandle-error", e);
                }
            }
        }
    }
}
