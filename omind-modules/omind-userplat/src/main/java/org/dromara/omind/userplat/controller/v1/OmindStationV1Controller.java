package org.dromara.omind.userplat.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.HttpStatus;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.omind.userplat.api.domain.datas.PolicyInfoData;
import org.dromara.omind.userplat.api.domain.entity.OmindPriceEntity;
import org.dromara.omind.userplat.api.domain.request.StationPageRequest;
import org.dromara.omind.userplat.service.OmindPriceService;
import org.dromara.omind.userplat.service.OmindStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Slf4j
@Tag(name = "OmindStationV1Controller", description = "站点信息接口")
@RestController
@RequestMapping("/omind/v1/station")
public class OmindStationV1Controller {

    @Autowired
    @Lazy
    OmindStationService omindStationService;

    @Autowired
    @Lazy
    OmindPriceService omindPriceService;

    @Operation(summary = "查询充电站列表")
    @GetMapping("/page")
    public TableDataInfo page(StationPageRequest stationPageRequest, PageQuery pageQuery) {
        try {
            TableDataInfo tableDataInfo = omindStationService.selectStationList(stationPageRequest, pageQuery);
            return tableDataInfo;
        } catch (BaseException ex) {
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg(ex.getMessage());
            return rspData;
        } catch (Exception ex) {
            log.error("operator--list-error", ex);
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(HttpStatus.ERROR);
            rspData.setMsg("内部服务错误");
            return rspData;
        }
    }

    @Operation(description = "根据查询充电站id获取详细信息")
    @GetMapping(value = "/{stationId}")
    public R getInfo(@PathVariable String stationId) {

        try {
            return R.ok(omindStationService.get(stationId));
        }
        catch (BaseException ex){
            log.error(ex.toString());
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error(ex.toString(), ex);
            return R.fail("内部服务错误");
        }
    }

    @Operation(summary = "根据查询充电站id获取详细信息")
    @GetMapping(value = "/price/{stationId}")
    public R getPrice(@PathVariable String stationId) {
        try {
            List<OmindPriceEntity>  priceList = omindPriceService.getPrice(stationId);
            List<PolicyInfoData> data = new ArrayList<>();
            if(priceList != null && priceList.size() > 0){
                for(OmindPriceEntity omindPrice : priceList){
                    PolicyInfoData policyInfoData = new PolicyInfoData();
                    policyInfoData.setPriceType(omindPrice.getPriceType());
                    policyInfoData.setServicePrice(omindPrice.getServicePrice());
                    policyInfoData.setElecPrice(omindPrice.getElecPrice());

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(omindPrice.getStartTime());
                    policyInfoData.setStartTime(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
                    data.add(policyInfoData);
                }
            }
            return R.ok(data);
        }
        catch (BaseException ex){
            log.error(ex.toString());
            return R.fail(ex.getMessage());
        }
        catch (Exception ex){
            log.error(ex.toString(), ex);
            return R.fail("内部服务错误");
        }
    }

}
