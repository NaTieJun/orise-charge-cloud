package org.dromara.omind.baseplat.controller.hlht;

import com.baomidou.lock.LockTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.omind.api.common.utils.encrypt.AESUtils;
import org.dromara.omind.api.common.utils.encrypt.HMacMD5Util;
import org.dromara.omind.api.common.utils.ip.IpUtils;
import org.dromara.omind.baseplat.api.constant.HlhtRet;
import org.dromara.omind.baseplat.api.domain.ChargeDetailData;
import org.dromara.omind.baseplat.api.domain.PolicyInfoData;
import org.dromara.omind.baseplat.api.domain.dto.HlhtDto;
import org.dromara.omind.baseplat.api.domain.dto.HlhtResult;
import org.dromara.omind.baseplat.api.domain.entity.*;
import org.dromara.omind.baseplat.api.domain.request.*;
import org.dromara.omind.baseplat.api.domain.response.*;
import org.dromara.omind.baseplat.api.service.*;
import org.dromara.omind.baseplat.api.service.notify.RemoteNotifyConnectorStatusService;
import org.dromara.omind.baseplat.api.service.pile.RemoteStartChargingService;
import org.dromara.omind.baseplat.api.service.pile.RemoteStopChargingService;
import org.dromara.omind.baseplat.client.UPlatNotificationV1Client;
import org.dromara.omind.baseplat.client.UTcpInnerV1Client;
import org.dromara.omind.baseplat.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Tag(name = "EvcsBusinessV1Controller", description = "[V1]互联互通——业务信息交换接口V1")
@RestController
@RequestMapping("/evcs/v1/")
public class EvcsBusinessV1Controller {

    /**
     * 运营商Service
     */
    @Autowired
    @Lazy
    SysOperatorService operatorService;

    /**
     * 充电接口价格信息Service
     */
    @Autowired
    @Lazy
    SysPriceService priceService;

    /**
     * 充电接口Service
     */
    @Autowired
    @Lazy
    SysConnectorService connectorService;

    @Autowired
    @Lazy
    SysStationService stationService;

    /**
     * 充电订单Service
     */
    @Autowired
    @Lazy
    SysChargeOrderService chargeOrderService;

    /**
     * 充电订单详情Service
     */
    @Autowired
    @Lazy
    SysChargeOrderItemService chargeOrderItemService;

    /**
     * 远程启动充电服务
     */
    @DubboReference
    RemoteStartChargingService remoteStartChargingService;

    /**
     * 远程停止充电服务
     */
    @DubboReference
    RemoteStopChargingService remoteStopChargingService;

    @Autowired
    @Lazy
    SysEquipmentService equipmentService;

    @Autowired
    @Lazy
    TradeService tradeService;

    @Resource
    UTcpInnerV1Client uTcpInnerV1Client;

    @Autowired
    @Lazy
    StationOperatorLinkService stationOperatorLinkService;

    @Autowired
    @Lazy
    UserPlatApiService userPlatApiService;

    @Resource
    UPlatNotificationV1Client uPlatNotificationV1Client;

    @Autowired
    @Lazy
    RemoteSeqService remoteSeqService;

    @Autowired
    LockTemplate lockTemplate;

    @Autowired
    @Lazy
    RemoteNotifyConnectorStatusService remoteNotifyConnectorStatusService;

    /**
     * 接口名称：查询业务策略信息结果query_equip_business_policy
     * 接口说明：通过流水号和充电设备接口编码查询充电桩业务策略信息
     * 请求格式：json
     * 请求方式：post
     * 该接口符合互联互通标准，需要在消息头中配置Content-Type为application/json、Authorization为Bearer token，
     * 消息体中必须包括OperatorID,Data,TimeStamp,Seq,Sig,并且对Data部分进行加密，详细说明请参照AES128位加密和MD5签名。
     * @return
     */
    @Operation(summary = "[V1]查询业务策略信息")
    @PostMapping("query_equip_business_policy")
    public HlhtResult queryEquipBusinessPolicy(@RequestBody HlhtDto hlhtDto){
        String operatorId = hlhtDto.getOperatorID();
        SysOperator sysOperator = operatorService.getOperatorById(operatorId);
        if(sysOperator == null){
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-运营商ID");
        }
        HlhtResult ret = hlhtDto.checkValid(sysOperator);
        if(ret.getRet() != HlhtRet.OK){
            return ret;
        }

        QueryEquipBusinessPolicyData queryEquipBusinessPolicyData = hlhtDto.getDataObj(sysOperator,
                QueryEquipBusinessPolicyData.class);
        log.info(JsonUtils.toJsonString(queryEquipBusinessPolicyData));
        String connectorId = queryEquipBusinessPolicyData.getConnectorID();
        List<PolicyInfoData> policyInfoList = priceService.getHlhtConnectorPriceList(connectorId);

        QueryEquipBusinessPolicyResponseData responseData = new QueryEquipBusinessPolicyResponseData();
        responseData.setEquipBizSeq(queryEquipBusinessPolicyData.getEquipBizSeq());
        responseData.setConnectorID(connectorId);
        if(policyInfoList == null || policyInfoList.size() == 0) {
            responseData.setPolicyInfos(policyInfoList);
            responseData.setFailReason((short) 1);
            responseData.setSumPeriod((short) 0);
            responseData.setSuccStat((short) 1);
        }
        else {
            responseData.setPolicyInfos(policyInfoList);
            responseData.setFailReason((short) 0);
            responseData.setSumPeriod((short) 24);
            responseData.setSuccStat((short) 0);
        }

        String responseJson = JsonUtils.toJsonString(responseData);
        log.info(JsonUtils.toJsonString(responseJson));
        HlhtResult result = new HlhtResult();
        result.setData(AESUtils.aes128CbcPKCS5Padding(sysOperator.getDataSecret(), sysOperator.getDataSecretIv(), responseJson));
        result.setRet(HlhtRet.OK);
        result.setMsg("");
        result.setSig(HMacMD5Util.getHMacMD5(sysOperator.getSigSecret(), result.getRet() + result.getMsg() + result.getData()));
        return result;
    }

    /**
     * 接口名称：请求设备认证query_equip_auth
     * 接口说明：根据设备认证流水号和设备编码进行设备认证。设备认证之前先获取终端状态为2的终端编号。
     * 请求格式：json
     * 请求方式：post
     * 注意事项：
     * （1）该接口符合互联互通标准，需要在消息头中配置Content-Type为application/json、Authorization为Bearer token，
     * 消息体中必须包括OperatorID,Data,TimeStamp,Seq,Sig,并且对Data部分进行加密，详细说明请参照AES128位加密和MD5签名。
     * @return
     */
    @Operation(summary = "[V1]请求设备认证")
    @PostMapping("query_equip_auth")
    public HlhtResult queryEquipAuth(@RequestBody HlhtDto hlhtDto){
        String operatorId = hlhtDto.getOperatorID();
        SysOperator sysOperator = operatorService.getOperatorById(operatorId);
        if(sysOperator == null){
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-运营商ID");
        }

        HlhtResult ret = hlhtDto.checkValid(sysOperator);
        if(ret.getRet() != HlhtRet.OK){
            return ret;
        }

        QueryEquipAuthData queryEquipAuthData = hlhtDto.getDataObj(sysOperator,
                QueryEquipAuthData.class);

        //增加业务逻辑
        // query_equip_auth接口的connectorID我们会传原二维码内容，贵平台需要解析出枪口号返给我方
        String connectorIdParam = queryEquipAuthData.getConnectorID();
        String connectorId = connectorIdParam+"";
        if(connectorIdParam.startsWith("http")){
            Pattern IdCard = Pattern.compile("\\d{16}");
            Matcher IdCardNumber = IdCard.matcher(connectorIdParam);
            if(IdCardNumber.find()) {
                connectorId = IdCardNumber.group();
            }
        }
        if(TextUtils.isBlank(connectorId)){
            connectorId = connectorIdParam.replaceAll(".*[^\\d](?=(\\d+))","");
        }

        SysConnector sysConnector = connectorService.getConnectorById(connectorId);
        if(sysConnector == null){
            log.error("未找到充电桩 data=" + JsonUtils.toJsonString(queryEquipAuthData) + " connectorId=" + connectorId);
        }

        SysOperator defaultOperator = connectorService.getDefaultOperatorByConnectorId(connectorId);
        if (defaultOperator == null || !defaultOperator.getOperatorId().equals(operatorId)) {
            boolean isOk = false;
            SysStation sysStation = connectorService.getStationInfoByConnectorId(connectorId);
            if (sysStation != null && !TextUtils.isBlank(sysStation.getStationId())) {
                List<SysStationOperatorLink> linkList = stationOperatorLinkService.getList4Station(sysStation.getStationId());
                for (SysStationOperatorLink link : linkList) {
                    if (link.getOperatorId().equals(operatorId)) {
                        isOk = true;
                        break;
                    }
                }
            }
            if (!isOk) {
                return HlhtResult.error(HlhtRet.ERROR_PARAM, "该运营商ID无权限");
            }
        }


        QueryEquipAuthResponseData responseData = new QueryEquipAuthResponseData();
        if(sysConnector != null && sysConnector.getStatus() == 2 && sysConnector.getState() == 0){
            responseData.setEquipAuthSeq(queryEquipAuthData.getEquipAuthSeq());
            responseData.setSuccStat((short)0);
            responseData.setFailReason((short)0);
            responseData.setConnectorID(connectorId);

            //再推送一次枪状态，确保用户端一致
            remoteNotifyConnectorStatusService.realtimeData(connectorId);
        }
        else{
            //状态 0离网 1空闲 2占用（未充电）3占用（充电）4占用（预约锁定）255故障
            responseData.setEquipAuthSeq(queryEquipAuthData.getEquipAuthSeq());
            responseData.setConnectorID(connectorId);
            responseData.setSuccStat((short) 1);
            if (sysConnector == null || sysConnector.getStatus() == 0 || sysConnector.getStatus() == 255 || sysConnector.getState() > 0) {
                responseData.setFailReason((short) 2);
                if (sysConnector == null) {
                    responseData.setFailReasonMsg("设备接口编码错误");
                } else if (sysConnector.getStatus() == 0) {
                    responseData.setFailReasonMsg("设备未联网");
                } else if (sysConnector.getStatus() == 255 || sysConnector.getState() > 0) {
                    responseData.setFailReasonMsg("设备故障");
                } else {
                    responseData.setFailReasonMsg("充电设备不可用");
                }
            } else if (sysConnector.getStatus() == 1) {
                responseData.setFailReason((short) 1);
                responseData.setFailReasonMsg("此设备尚未插枪");
            } else {
                short value = sysConnector.getStatus() > 99 ? 99 : sysConnector.getStatus();
                responseData.setFailReason(value);
                responseData.setFailReasonMsg("充电设备不可用");
            }
        }
        String responseJson = JsonUtils.toJsonString(responseData);
        log.info("【请求设备认证】返回：" + responseJson);
        HlhtResult result = new HlhtResult();
        result.setData(AESUtils.aes128CbcPKCS5Padding(sysOperator.getDataSecret(), sysOperator.getDataSecretIv(), responseJson));
        result.setRet(HlhtRet.OK);
        result.setMsg("");
        result.setSig(HMacMD5Util.getHMacMD5(sysOperator.getSigSecret(), result.getRet() + result.getMsg() + result.getData()));
        return result;
    }

    /**
     * 接口名称：请求启动充电 query_start_charge
     * 接口说明：通过充电订单号、设备接口编码、二维码，请求启动充电信息。
     * 请求格式：json
     * 请求方式：post
     * 注意事项：
     * （1）该接口符合互联互通标准，需要在消息头中配置Content-Type为application/json、Authorization为Bearer token，
     * 消息体中必须包括OperatorID,Data,TimeStamp,Seq,Sig,并且对Data部分进行加密，详细说明请参照AES128位加密和MD5签名。
     * （2）为了记录充电用户的订单，方便后续进行支付和开票，在互联互通标准的基础上扩展了手机号字段，个人支付必填。
     * （3）为了互联互通用户享受充电停车减免，在互联互通标准的基础上扩展了车牌号字段，停车减免必填。
     * @return
     */
    @Operation(summary = "[V1]请求启动充电")
    @PostMapping("query_start_charge")
    public HlhtResult queryStartCharge(@RequestBody HlhtDto hlhtDto){
        String operatorId = hlhtDto.getOperatorID();
        SysOperator sysOperator = operatorService.getOperatorById(operatorId);
        if(sysOperator == null){
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-运营商ID");
        }
        HlhtResult ret = hlhtDto.checkValid(sysOperator);
        if(ret.getRet() != HlhtRet.OK){
            return ret;
        }
        QueryStartChargeData queryStartChargeData = hlhtDto.getDataObj(sysOperator,
            QueryStartChargeData.class);

        log.info("query_start_charge--operator--" + sysOperator.getOperatorId() + "--name--" + sysOperator.getOperatorName());
        log.info("query_start_charge--info--" + JsonUtils.toJsonString(queryStartChargeData));

        //首先判断充电订单序号的有效性
        String startChargeSeq = queryStartChargeData.getStartChargeSeq();
        if(chargeOrderService.getChargeOrderByStartChargeSeq(startChargeSeq) != null)
        {
            //订单号重复
            QueryStartChargeResponseData responseData = new QueryStartChargeResponseData();
            responseData.setConnectorID(queryStartChargeData.getConnectorID());
            responseData.setStartChargeSeq(queryStartChargeData.getStartChargeSeq());
            responseData.setStartChargeSeqStat((short)5);
            responseData.setSuccStat((short)1);
            responseData.setFailReason((short)10);
            String responseJson = JsonUtils.toJsonString(responseData);
            HlhtResult result = new HlhtResult();
            result.setData(AESUtils.aes128CbcPKCS5Padding(sysOperator.getDataSecret(), sysOperator.getDataSecretIv(), responseJson));
            result.setRet(HlhtRet.OK);
            result.setMsg("error: startChargeSeq repeat");
            result.setSig(HMacMD5Util.getHMacMD5(sysOperator.getSigSecret(), result.getRet() + result.getMsg() + result.getData()));
            return result;
        }


        SysConnector sysConnector = connectorService.getConnectorById(queryStartChargeData.getConnectorID());
        SysEquipment sysEquipment = equipmentService.getEquipmentById(sysConnector.getEquipmentId());

        SysOperator defaultOperator = connectorService.getDefaultOperatorByConnectorId(queryStartChargeData.getConnectorID());
        if(defaultOperator == null || !defaultOperator.getOperatorId().equals(operatorId))
        {
            boolean isOk = false;
            if(sysEquipment != null) {
                SysStation sysStation = stationService.getStationById(sysEquipment.getStationId());
                if(sysStation != null && !TextUtils.isBlank(sysStation.getStationId())) {
                    List<SysStationOperatorLink> linkList = stationOperatorLinkService.getList4Station(sysStation.getStationId());
                    for(SysStationOperatorLink link : linkList){
                        if(link.getOperatorId().equals(operatorId)){
                            isOk = true;
                            break;
                        }
                    }
                }
            }
            if(!isOk){
                return HlhtResult.error(HlhtRet.ERROR_PARAM, "该运营商ID无权限");
            }
        }

        QueryStartChargeResponseData responseData = new QueryStartChargeResponseData();

        if (!TextUtils.isBlank(sysConnector.getEquipmentId())) {
            if (sysEquipment != null && !TextUtils.isBlank(sysEquipment.getServerIp()) && !sysEquipment.getServerIp().equals("127.0.0.1")) {
                String localIp = IpUtils.getHostIp();
                if (!localIp.equals(sysEquipment.getServerIp())) {
                    log.info("【TCP通信转发】当前IP" + localIp + " 桩连接服务IP" + sysEquipment.getServerIp());
                    return uTcpInnerV1Client.queryStartCharge(sysEquipment.getServerIp(), hlhtDto);
                }
            }
        }
        responseData.setStartChargeSeq(queryStartChargeData.getStartChargeSeq());
        responseData.setConnectorID(queryStartChargeData.getConnectorID());
        if (sysConnector == null) {
            //未找到充电桩，结束
            responseData.setStartChargeSeqStat((short) 4);
            responseData.setSuccStat((short) 1);
            responseData.setFailReason((short) 1);
        } else if (sysConnector.getStatus() != 2 || sysConnector.getState() != 0) {
            //getStatus状态 0离网 1空闲 2占用（未充电）3占用（充电）4占用（预约锁定）255故障
            //state充电桩状态 0正常 1故障
            responseData.setStartChargeSeqStat((short) 4);
            responseData.setSuccStat((short) 1);
            if (sysConnector.getStatus() == 0
                    || sysConnector.getStatus() == 255
                    || sysConnector.getState() == 1) {
                responseData.setFailReason((short) 2);
            } else if (sysConnector.getStatus() == 3) {
                responseData.setFailReason((short) 91);  //该车辆充电中
            } else if (sysConnector.getStatus() == 1) {
                responseData.setFailReason((short) 3);   //空闲（未插枪）
            } else if (sysConnector.getStatus() >= 3) {
                responseData.setFailReason((short) 4);   //其他错误
            }
        } else {
            try {
                if (TextUtils.isBlank(queryStartChargeData.getPhoneNum())) {
                    queryStartChargeData.setPhoneNum("13800000000");
                }
                if (queryStartChargeData.getBalance() == null || queryStartChargeData.getBalance().floatValue() <= 0) {
                    queryStartChargeData.setBalance(new BigDecimal("9999"));
                }
                int result = remoteStartChargingService.startCharging(sysOperator, queryStartChargeData);
                if (result != 0) {
                    //创建订单失败
                    responseData.setStartChargeSeqStat((short) 4);
                    responseData.setSuccStat((short) 1);
                    responseData.setFailReason((short) result);
                } else {
                    responseData.setStartChargeSeqStat((short) 1);
                    responseData.setSuccStat((short) 0);
                    responseData.setFailReason((short) 0);
                }
            } catch (BaseException ex) {
                log.error("【远程启机异常】***********");
                log.error(ex.toString());
                responseData.setStartChargeSeqStat((short) 4);
                responseData.setSuccStat((short) 1);
                responseData.setFailReason((short) 99);
            }
        }

        String responseJson = JsonUtils.toJsonString(responseData);
        HlhtResult result = new HlhtResult();
        result.setData(AESUtils.aes128CbcPKCS5Padding(sysOperator.getDataSecret(), sysOperator.getDataSecretIv(), responseJson));
        result.setRet(HlhtRet.OK);
        result.setMsg("");
        result.setSig(HMacMD5Util.getHMacMD5(sysOperator.getSigSecret(), result.getRet() + result.getMsg() + result.getData()));
        return result;
    }

    /**
     *  接口名称：查询充电状态query_equip_charge_status
     *  接口说明：通过充电订单号查询充电状态。
     *  请求格式：json
     *  请求方式：post
     *  注意事项
     *  （1）该接口符合互联互通标准，需要在消息头中配置Content-Type为application/json、Authorization为Bearer token，
     *  消息体中必须包括OperatorID,Data,TimeStamp,Seq,Sig,并且对Data部分进行加密，详细说明请参照AES128位加密和MD5签名。
     *  （2）返回数据全部是0：没有启动充电成功。
     *  （3）充电订单号格式：运营商ID+年月日时间+编号
     * @return
     */
    @Operation(summary = "[V1]查询充电状态")
    @PostMapping("query_equip_charge_status")
    public HlhtResult queryEquipChargeStatus(@RequestBody HlhtDto hlhtDto){
        String operatorId = hlhtDto.getOperatorID();
        SysOperator sysOperator = operatorService.getOperatorById(operatorId);
        if(sysOperator == null){
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-运营商ID");
        }
        HlhtResult ret = hlhtDto.checkValid(sysOperator);
        if(ret.getRet() != HlhtRet.OK){
            return ret;
        }
        QueryEquipChargeStatusData queryEquipChargeStatusData = hlhtDto.getDataObj(sysOperator,
                QueryEquipChargeStatusData.class);
        SysChargeOrder sysChargeOrder = chargeOrderService.getChargeOrderByStartChargeSeq(queryEquipChargeStatusData.getStartChargeSeq());

        QueryEquipChargeStatusResponseData responseData = new QueryEquipChargeStatusResponseData();
        responseData.setStartChargeSeq(queryEquipChargeStatusData.getStartChargeSeq());
        if(sysChargeOrder == null){
            //没有这个订单
            //所有数据默认0即可，所以什么都不用set
        }
        else{
            responseData.setStartChargeSeqStat(sysChargeOrder.getStartChargeSeqStat());
            responseData.setConnectorID(sysChargeOrder.getConnectorId());
            responseData.setConnectorStatus(sysChargeOrder.getConnectorStatus());
            responseData.setCurrentA(sysChargeOrder.getCurrentA());
            responseData.setCurrentB(sysChargeOrder.getCurrentB());
            responseData.setCurrentC(sysChargeOrder.getCurrentC());
            responseData.setVoltageA(sysChargeOrder.getVoltageA());
            responseData.setVoltageB(sysChargeOrder.getVoltageB());
            responseData.setVoltageC(sysChargeOrder.getVoltageC());
            if(sysChargeOrder.getSoc() == null){
                responseData.setSoc(new BigDecimal("0"));
            }
            else {
                responseData.setSoc(sysChargeOrder.getSoc().multiply(new BigDecimal("100.0")));
            }
            responseData.setStartTime(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss",
                    sysChargeOrder.getStartTime()));
            responseData.setEndTime(DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss",
                    sysChargeOrder.getEndTime()));
            responseData.setTotalPower(sysChargeOrder.getTotalPower());
            responseData.setElecMoney(sysChargeOrder.getElecMoney());
            responseData.setServiceMoney(sysChargeOrder.getServiceMoney());
            responseData.setTotalMoney(sysChargeOrder.getTotalMoney());
            responseData.setSumPeriod(sysChargeOrder.getSumPeriod());
            if(sysChargeOrder.getStartChargeSeqStat() == 4) {
                //充电结束后读取数据库
                List<ChargeDetailData> chargeDetailDataList = chargeOrderItemService.getList4StartChargeSeq(sysChargeOrder.getStartChargeSeq());
                responseData.setChargeDetails(chargeDetailDataList);
            }
            else{
                //充电未结束读取实时数据
                List<ChargeDetailData> chargeDetailDataList = tradeService.getChargeOrderDetails(sysChargeOrder.getStartChargeSeq(), true);
                responseData.setChargeDetails(chargeDetailDataList);
            }
        }
        String responseJson = JsonUtils.toJsonString(responseData);
        HlhtResult result = new HlhtResult();
        result.setData(AESUtils.aes128CbcPKCS5Padding(sysOperator.getDataSecret(), sysOperator.getDataSecretIv(), responseJson));
        result.setRet(HlhtRet.OK);
        result.setMsg("");
        result.setSig(HMacMD5Util.getHMacMD5(sysOperator.getSigSecret(), result.getRet() + result.getMsg() + result.getData()));
        return result;
    }

    /**
     * 接口名称：请求停止充电query_stop_charge
     * 接口说明：通过充电订单号、设备接口编码，请求停止充电。
     * 请求格式：json
     * 请求方式：post
     * @return
     */
    @Operation(summary = "[V1]请求停止充电")
    @PostMapping("query_stop_charge")
    public HlhtResult queryStopCharge(@RequestBody HlhtDto hlhtDto){
        String operatorId = hlhtDto.getOperatorID();
        SysOperator sysOperator = operatorService.getOperatorById(operatorId);
        if(sysOperator == null){
            return HlhtResult.error(HlhtRet.ERROR_PARAM, "参数错误-运营商ID");
        }
        HlhtResult ret = hlhtDto.checkValid(sysOperator);
        if(ret.getRet() != HlhtRet.OK){
            return ret;
        }

        QueryStopChargeData queryStopChargeData = hlhtDto.getDataObj(sysOperator,
                QueryStopChargeData.class);
        log.info("query_stop_charge--operator--" + sysOperator.getOperatorId() + "--name--" + sysOperator.getOperatorName());
        log.info("query_stop_charge--info--" + JsonUtils.toJsonString(queryStopChargeData));

        SysChargeOrder sysChargeOrder = chargeOrderService.getChargeOrderByStartChargeSeq(queryStopChargeData.getStartChargeSeq());

        QueryStopChargeResponseData responseData = new QueryStopChargeResponseData();
        SysConnector sysConnector = connectorService.getConnectorById(queryStopChargeData.getConnectorID());
        SysEquipment sysEquipment = equipmentService.getEquipmentById(sysConnector.getEquipmentId());

        if (!TextUtils.isBlank(sysConnector.getEquipmentId())) {
            if (sysEquipment != null && !sysEquipment.getServerIp().equals("127.0.0.1")) {
                String localIp = IpUtils.getHostIp();
                if (!localIp.equals(sysEquipment.getServerIp())) {
                    log.info("【TCP通信转发】当前IP" + localIp + " 桩连接服务IP" + sysEquipment.getServerIp());
                    return uTcpInnerV1Client.queryStopCharge(sysEquipment.getServerIp(), hlhtDto);
                }
            }
        }


        responseData.setStartChargeSeq(queryStopChargeData.getStartChargeSeq());
        if (sysChargeOrder == null) {
            responseData.setStartChargeSeqStat((short) 5);
            responseData.setSuccStat((short) 1);
            responseData.setFailReason((short) 4);   //订单不存在
        } else if (sysChargeOrder.getStartChargeSeqStat() == 1 || sysChargeOrder.getStartChargeSeqStat() == 2 || sysChargeOrder.getStartChargeSeqStat() == 3) {
            //正确的逻辑
            int result = remoteStopChargingService.stopCharging(sysOperator, queryStopChargeData);
            if (result == 0) {
                responseData.setStartChargeSeqStat((short) 3);
                responseData.setSuccStat((short) 0);
                responseData.setFailReason((short) 0);
            } else {
                responseData.setStartChargeSeqStat(sysChargeOrder.getStartChargeSeqStat());
                responseData.setSuccStat((short) 1);
                responseData.setFailReason((short) result);
            }

        }//充电订单状态；1启动中 2充电中 3停止中 4已结束 5未知
        else if (sysChargeOrder.getStartChargeSeqStat() == 4) {
            responseData.setStartChargeSeqStat((short) 4);
            responseData.setSuccStat((short) 0);
            responseData.setFailReason((short) 3);
        } else if (sysChargeOrder.getStartChargeSeqStat() == 5) {
            responseData.setStartChargeSeqStat((short) 5);
            responseData.setSuccStat((short) 1);
            responseData.setFailReason(sysChargeOrder.getFailReason().shortValue());
        }

        String responseJson = JsonUtils.toJsonString(responseData);
        HlhtResult result = new HlhtResult();
        result.setData(AESUtils.aes128CbcPKCS5Padding(sysOperator.getDataSecret(), sysOperator.getDataSecretIv(), responseJson));
        result.setRet(HlhtRet.OK);
        result.setMsg("");
        result.setSig(HMacMD5Util.getHMacMD5(sysOperator.getSigSecret(), result.getRet() + result.getMsg() + result.getData()));
        return result;
    }
}
