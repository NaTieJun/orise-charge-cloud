package org.dromara.omind.baseplat.controller.operation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.omind.baseplat.domain.vo.PriceInfoData;
import org.dromara.omind.baseplat.domain.request.PriceAddRequest;
import org.dromara.omind.baseplat.domain.request.PriceEditRequest;
import org.dromara.omind.baseplat.domain.request.PriceLinkStationsRequest;
import org.dromara.omind.baseplat.service.SysPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "PriceController", description = "[运营]基础服务层运营接口——价格接口")
@Slf4j
@RestController
@RequestMapping("/hlht/v1/sys/price")
public class PriceV1Controller {

    @Autowired
    @Lazy
    SysPriceService priceService;

    @GetMapping(value = "/page")
    @Operation(summary = "获取充电价格模版列表，可以根据备注信息搜索")
    public R getPriceList(String keyword, PageQuery pageQuery)
    {
        try{
            return R.ok(priceService.getPricePage(keyword, pageQuery));
        }
        catch (BaseException ex){
            log.error(ex.getMessage(), ex);
            return R.fail("内部服务错误");
        }
        catch (Exception ex){
            log.error(ex.toString(), ex);
            return R.fail("内部服务错误");
        }
    }

    @GetMapping(value = "/{priceCode}")
    @Operation(summary = "根据价格Code，获取价格")
    public R getPrice(@PathVariable Long priceCode)
    {
        try{
            if(priceCode == null || priceCode < 0){
                throw new BaseException("无效的价格编码");
            }
            return R.ok(priceService.getPriceInfo(priceCode));
        }
        catch (BaseException ex){
            log.error(ex.getMessage(), ex);
            return R.fail("内部服务错误");
        }
        catch (Exception ex){
            log.error(ex.toString(), ex);
            return R.fail("内部服务错误");
        }
    }

    @GetMapping(value = "/getStationPrice")
    @Operation(summary = "根据充电站，获取价格")
    public R getStationPrice(String stationId)
    {
        try{
            if(TextUtils.isBlank(stationId)){
                throw new BaseException("无效的站带你ID");
            }
            return R.ok(priceService.getStationPriceInfo(stationId));
        }
        catch (BaseException ex){
            log.error(ex.getMessage(), ex);
            return R.fail("内部服务错误");
        }
        catch (Exception ex){
            log.error(ex.toString(), ex);
            return R.fail("内部服务错误");
        }
    }

    @Operation(summary = "创建新的价格模版")
    @PostMapping
    public R add(@Validated @RequestBody PriceAddRequest priceAddRequest){
        try{
            if(priceAddRequest.getPriceList() == null || priceAddRequest.getPriceList().size() <= 0){
                throw new BaseException("无效的价格");
            }
            if(priceAddRequest.getPriceList().get(0).getStartHour() != 0){
                throw new BaseException("价格必须从0点开始");
            }
            int start = -1;
            for(PriceInfoData priceInfoData : priceAddRequest.getPriceList()){
                if(priceInfoData == null){
                    throw new BaseException("无效的价格对象");
                }
                if(priceInfoData.getPriceType() == null
                        || priceInfoData.getPriceType() < 0
                        || priceInfoData.getPriceType() > 3)
                {
                    throw new BaseException("无效的价格类型");
                }
                if(priceInfoData.getElecPrice() == null || priceInfoData.getElecPrice().floatValue() < 0){
                    throw new BaseException("无效的电价");
                }
                if(priceInfoData.getServicePrice() == null || priceInfoData.getServicePrice().floatValue() < 0){
                    throw new BaseException("无效的服务费价格");
                }
                if(priceInfoData.getStartHour() == null || priceInfoData.getStartHour() < 0){
                    throw new BaseException("无效的起始时间");
                }
                if(start >= priceInfoData.getStartHour()){
                    throw new BaseException("价格必须按小时顺序依次传递");
                }
                start = priceInfoData.getStartHour();
            }

            priceService.addPrice(priceAddRequest);
            return R.ok("ok");
        }
        catch (BaseException ex){
            log.error(ex.getMessage(), ex);
            return R.fail("内部服务错误");
        }
        catch (Exception ex){
            log.error(ex.toString(), ex);
            return R.fail("内部服务错误");
        }
    }

    @Operation(summary = "编辑价格模版，仅支持编辑备注或者编辑默认价格模版")
    @PutMapping("/edit")
    public R edit(@Validated @RequestBody PriceEditRequest priceEditRequest){
        try{
            if(priceEditRequest.getPriceCode() == null || priceEditRequest.getPriceCode() < 0){
                throw new BaseException("无效的价格编码");
            }
//            if(priceEditRequest.getPriceList() != null && priceEditRequest.getPriceCode() == 0){
//                if(priceEditRequest.getPriceList().get(0).getStartHour() != 0){
//                    throw new BaseException("价格必须从0点开始");
//                }
//                int start = -1;
//                for(PriceInfoData priceInfoData : priceEditRequest.getPriceList()){
//                    if(start >= priceInfoData.getStartHour()){
//                        throw new BaseException("价格必须按小时顺序依次传递");
//                    }
//                    start = priceInfoData.getStartHour();
//                }
//            }
            priceService.editPrice(priceEditRequest);
            return R.ok("ok");
        }
        catch (BaseException ex){
            log.error(ex.getMessage(), ex);
            return R.fail("内部服务错误");
        }
        catch (Exception ex){
            log.error(ex.toString(), ex);
            return R.fail("内部服务错误");
        }
    }

    @Operation(summary = "站点批量关联价格模版")
    @PutMapping("/linkStations")
    public R edit(@Validated @RequestBody PriceLinkStationsRequest priceLinkStationsRequest){
        try{
            if(priceLinkStationsRequest.getPriceCode() == null || priceLinkStationsRequest.getPriceCode() < 0){
                throw new BaseException("无效的价格编码");
            }
            if(priceLinkStationsRequest.getStationIds() == null || priceLinkStationsRequest.getStationIds().size() <= 0){
                throw new BaseException("无效的站点ID列表");
            }
            priceService.linkStations(priceLinkStationsRequest);
            return R.ok("ok");
        }
        catch (BaseException ex){
            log.error(ex.getMessage(), ex);
            return R.fail("内部服务错误");
        }
        catch (Exception ex){
            log.error(ex.toString(), ex);
            return R.fail("内部服务错误");
        }
    }

    @DeleteMapping(value = "/{priceCode}")
    @Operation(summary = "删除价格Code")
    public R del(@PathVariable Long priceCode){
        try{
            if(priceCode == null || priceCode < 0){
                throw new BaseException("无效的价格编码");
            }
            else if(priceCode == 0){
                throw new BaseException("默认价格模版不可删除");
            }
            priceService.removePrice(priceCode);
            return R.ok("已删除");
        }
        catch (BaseException ex){
            log.error(ex.getMessage(), ex);
            return R.fail("内部服务错误");
        }
        catch (Exception ex){
            log.error(ex.toString(), ex);
            return R.fail("内部服务错误");
        }
    }
}
