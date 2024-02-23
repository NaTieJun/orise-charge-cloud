package org.dromara.omind.userplat.service.impl;

import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedissonLockExecutor;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.TextUtils;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.omind.userplat.api.domain.datas.ChargeDetailData;
import org.dromara.omind.userplat.api.domain.entity.*;
import org.dromara.omind.userplat.api.domain.notifications.NotificationChargeOrderInfoData;
import org.dromara.omind.userplat.api.domain.notifications.NotificationEquipChargeStatusData;
import org.dromara.omind.userplat.api.domain.request.BillForceStopRequest;
import org.dromara.omind.userplat.api.domain.request.BillPageRequest;
import org.dromara.omind.userplat.api.domain.request.ChargeOrderListRequest;
import org.dromara.omind.userplat.api.domain.request.QueryStopChargeData;
import org.dromara.omind.userplat.api.domain.response.QueryStopChargeResponseData;
import org.dromara.omind.userplat.api.domain.vo.ChargeOrderSimpleVo;
import org.dromara.omind.userplat.constant.PlatChargeBillStatusConstant;
import org.dromara.omind.userplat.constant.PlatRedisKey;
import org.dromara.omind.userplat.domain.service.OmindBillEntityIService;
import org.dromara.omind.userplat.service.*;
import org.dromara.omind.userplat.service.hlht.HlhtInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class OmindBillServiceImpl implements OmindBillService {

    @Autowired
    @Lazy
    OmindBillEntityIService iService;

    @Autowired
    @Lazy
    OmindConnectorService omindConnectorService;

    @Autowired
    @Lazy
    HlhtInfoService hlhtInfoService;

    @Autowired
    @Lazy
    OmindBillService selfService;

    @Autowired
    @Lazy
    OmindStationService omindStationService;

    @Autowired
    @Lazy
    OmindOperatorService omindOperatorService;

    @Autowired
    @Lazy
    OmindUserService omindUserService;

    @Autowired
    private LockTemplate lockTemplate;

    @Override
    public OmindBillEntity get(String startChargeSeq) {

        LambdaQueryWrapper<OmindBillEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OmindBillEntity::getStartChargeSeq, startChargeSeq).last("limit 1");
        OmindBillEntity odBillInfo = iService.getOne(lambdaQueryWrapper);

        if(odBillInfo != null) {
            if (odBillInfo.getStartChargeSeqStat() == PlatChargeBillStatusConstant.CHARGE_BILL_FINISH) {
                Date startTm = odBillInfo.getStartTime() == null ? odBillInfo.getCreateTime() : odBillInfo.getStartTime();
                Date endTm = odBillInfo.getEndTime();
                if (endTm == null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startTm);
                    calendar.add(Calendar.HOUR_OF_DAY, 10);
                    endTm = calendar.getTime();
                    if (endTm.after(new Date())) {
                        endTm = new Date();
                    }
                }

                odBillInfo.setChargeDur(endTm.getTime() - startTm.getTime());
                odBillInfo.setCurrentA(new BigDecimal(0.00));
                odBillInfo.setVoltageA(new BigDecimal(0.00));
                odBillInfo.setCurrentP(new BigDecimal(0.00));
            } else {
                Date startTm = odBillInfo.getStartTime() == null ? odBillInfo.getCreateTime() : odBillInfo.getStartTime();
                Date endTm = odBillInfo.getEndTime() == null ? (new Date()) : odBillInfo.getEndTime();

                odBillInfo.setChargeDur(endTm.getTime() - startTm.getTime());
                odBillInfo.setCurrentP(odBillInfo.getCurrentA().multiply(odBillInfo.getVoltageA()));
            }
            OmindStationEntity omindStation = omindStationService.get(odBillInfo.getStationId());
            String stationName = omindStation != null ? omindStation.getStationName() : "";
            odBillInfo.setStationName(stationName);

            OmindUserEntity userEntity = omindUserService.getUserById(odBillInfo.getUserId());
            String userName = userEntity != null ? userEntity.getNickName() : "";
            String mobile = userEntity != null ? userEntity.getMobile() : "";
            odBillInfo.setNickName(userName);
            odBillInfo.setMobile(mobile);

            OmindOperatorEntity omindOperator = omindOperatorService.selectOperatorInfoById(odBillInfo.getBaseOperatorId());
            String operatorName = omindOperator != null ? omindOperator.getOperatorName() : "";
            odBillInfo.setOperatorName(operatorName);

        }

        return odBillInfo;
    }

    @Override
    public OmindBillEntity getLatest(String connectorId) {
        LambdaQueryWrapper<OmindBillEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OmindBillEntity::getConnectorId, connectorId);
        lambdaQueryWrapper.orderByDesc(OmindBillEntity::getBillId);
        lambdaQueryWrapper.last("limit 1");

        return iService.getOne(lambdaQueryWrapper);
    }

    @Override
    public OmindBillEntity insertBillInfo(OmindBillEntity odBillInfo) throws BaseException {
        if (odBillInfo == null) {
            throw new BaseException("充电订单为空");
        }
        if (!iService.save(odBillInfo)) {
            throw new BaseException("数据保存失败");
        }
        return odBillInfo;
    }

    @Override
    public boolean updateBillInfo(OmindBillEntity odBillInfo) throws BaseException {
        if (!iService.updateById(odBillInfo)) {
            throw new BaseException("数据更新失败");
        }

        //更新redis
        String key = PlatRedisKey.CHARGE_BILL_INFO + odBillInfo.getStartChargeSeq();
        RedisUtils.deleteObject(key);

        return true;
    }

    @Override
    public QueryStopChargeResponseData billStopChargeDeal(String startChargeSeq, String connectorId, Long billId) throws BaseException {
        try {
            QueryStopChargeData queryStopChargeData = new QueryStopChargeData();
            queryStopChargeData.setStartChargeSeq(startChargeSeq);
            queryStopChargeData.setConnectorID(connectorId);

            QueryStopChargeResponseData queryStopChargeResponseData = hlhtInfoService.stopCharge(queryStopChargeData);
            log.info("stopCharge---queryStopChargeResponseData=" + queryStopChargeResponseData);
            if (queryStopChargeResponseData.getSuccStat() == 0) {
                //更新充电订单信息
                OmindBillEntity updateBillInfoObjData = new OmindBillEntity();
                updateBillInfoObjData.setBillId(billId);
                updateBillInfoObjData.setStartChargeSeq(startChargeSeq);
                updateBillInfoObjData.setStopReason(1);
                updateBillInfoObjData.setSuccStat(queryStopChargeResponseData.getSuccStat());
                updateBillInfoObjData.setStopFailReason(queryStopChargeResponseData.getFailReason());
                selfService.updateBillInfo(updateBillInfoObjData);
            } else {
                //更新充电订单信息
                OmindBillEntity updateBillInfoObjData = new OmindBillEntity();
                updateBillInfoObjData.setBillId(billId);
                updateBillInfoObjData.setStartChargeSeq(startChargeSeq);
                updateBillInfoObjData.setStartChargeSeqStat((short) PlatChargeBillStatusConstant.CHARGE_BILL_FINISH);
                updateBillInfoObjData.setStopReason(1);
                updateBillInfoObjData.setSuccStat(queryStopChargeResponseData.getSuccStat());
                updateBillInfoObjData.setStopFailReason(queryStopChargeResponseData.getFailReason());
                selfService.updateBillInfo(updateBillInfoObjData);
            }

            return queryStopChargeResponseData;
        } catch (BaseException ube) {
            log.error("bill-billStopChargeDeal-error", ube);
            throw ube;
        } catch (Exception e) {
            log.error("bill-billStopChargeDeal-error", e);
            throw new BaseException("未知错误");
        }
    }

    @Override
    public TableDataInfo chargeOrderList(ChargeOrderListRequest chargeOrderListRequest, Long userId, PageQuery pageQuery) {
        LambdaQueryWrapper<OmindBillEntity> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(OmindBillEntity::getUserId, userId);

        if (chargeOrderListRequest.getIsOnlyCharing() == 1) {
            List<Short> statusList = new ArrayList<>();
            statusList.add((short) 1);
            statusList.add((short) 2);
            lambdaQuery.in(OmindBillEntity::getStartChargeSeqStat, statusList);
        }
        lambdaQuery.orderByDesc(OmindBillEntity::getBillId);

        Page<OmindBillEntity> page = iService.page(pageQuery.build(), lambdaQuery);

        List<ChargeOrderSimpleVo> simpleVoList = new ArrayList<>();

        if (page.getRecords() != null && page.getSize() > 0) {
            for (OmindBillEntity omindBill : page.getRecords()) {
                OmindConnectorEntity odConnectorInfo = omindConnectorService.selectConnectorInfo(omindBill.getConnectorId());
                OmindStationEntity odStationEntity = null;
                if (odConnectorInfo != null) {
                    odStationEntity = omindStationService.get(omindBill.getStationId());
                }

                ChargeOrderSimpleVo simpleVo = new ChargeOrderSimpleVo();
                simpleVo.setStartChargeSeq(omindBill.getStartChargeSeq());
                simpleVo.setConnectorId(omindBill.getConnectorId());
                simpleVo.setStatus(omindBill.getStartChargeSeqStat());
                simpleVo.setStationName(odStationEntity == null ? "" : odStationEntity.getStationName());
                simpleVo.setType(omindBill.getBillType());
                simpleVo.setStartTm(omindBill.getStartTime() == null ? omindBill.getCreateTime() : omindBill.getStartTime());

                if (omindBill.getEndTime() == null) {
                    simpleVo.setEndTm(omindBill.getEndTime());
                }
                simpleVo.setMoney(omindBill.getTotalMoney());
                simpleVo.setRealMoney(omindBill.getRealPayMoney());

                simpleVoList.add(simpleVo);
            }
        }
        Page<ChargeOrderSimpleVo> orderPage = new Page<>();
        orderPage.setTotal(page.getTotal());
        orderPage.setCurrent(page.getCurrent());
        orderPage.setSize(page.getSize());
        orderPage.setRecords(simpleVoList);
        orderPage.setPages(page.getPages());
        return TableDataInfo.build(orderPage);
    }

    @Override
    public QueryStopChargeResponseData stopCharge(Long billId) throws BaseException {
        OmindBillEntity odBillEntity = selfService.selectBillInfoById(billId);
        if (odBillEntity == null) {
            throw new BaseException("该充电订单不存在");
        }
        String connectorId = odBillEntity.getConnectorId();
        String lockKey = PlatRedisKey.CONNECTOR_START_STOP_CHARGE_LOCK + connectorId;
        LockInfo lockInfo = null;
        try {
            //小程序停止充电请求，上分布式同步锁
            lockInfo = lockTemplate.lock(lockKey, 5000L, 10000L, RedissonLockExecutor.class);

            //获取充电设备编码信息
            OmindConnectorEntity odConnectorEntity = omindConnectorService.selectConnectorInfo(connectorId);
            if (odConnectorEntity == null) {
                throw new BaseException("该设备不存在");
            }

            QueryStopChargeResponseData queryStopChargeResponseData = new QueryStopChargeResponseData();
            try {
                queryStopChargeResponseData = selfService.billStopChargeDeal(odBillEntity.getStartChargeSeq(),
                        connectorId, odBillEntity.getBillId());
            } catch (BaseException ube) {
                log.error("bill-stopCharge-error", ube);
                throw ube;
            } catch (Exception e) {
                log.error("bill-stopCharge-error", e);
                throw new BaseException("未知错误");
            }

            return queryStopChargeResponseData;
        } catch (BaseException ube) {
            log.error("bill-stopCharge-error", ube);
            throw ube;
        } catch (Exception e) {
            log.error("bill-stopCharge-error", e);
            throw new BaseException("未知错误");
        } finally {
            //解锁
            if (lockInfo != null) {
                lockTemplate.releaseLock(lockInfo);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void equipChargeStatusDeal(NotificationEquipChargeStatusData notificationEquipChargeStatusData, OmindConnectorEntity odConnectorEntity, String operatorId, Short platType) throws BaseException {
        try {
            String startChargeSeq = notificationEquipChargeStatusData.getStartChargeSeq();
            if (odConnectorEntity != null && !TextUtils.isBlank(startChargeSeq)) {

                //实际消费电费总金额
                BigDecimal detailRealElecMoney = new BigDecimal("0.00");
                //实际消费服务费总金额
                BigDecimal detailRealServiceMoney = new BigDecimal("0.00");
                BigDecimal currentMoney = new BigDecimal("0.00");

                log.info("equip-charge-status==notificationEquipChargeStatusData==" + notificationEquipChargeStatusData);
                detailRealElecMoney = notificationEquipChargeStatusData.getElecMoney();
                detailRealServiceMoney = notificationEquipChargeStatusData.getServiceMoney();
                //当前花费金额
                currentMoney = notificationEquipChargeStatusData.getTotalMoney();

                //联合用户冻结金额
                OmindBillEntity odBillEntity = selfService.get(startChargeSeq);
                log.info("equip-charge-status==odBillEntity==" + odBillEntity);
                if (odBillEntity != null) {
                    //充电订单状态
                    Short startChargeSeqStat = odBillEntity.getStartChargeSeqStat();

                    //充电订单信息更新
                    if ((startChargeSeqStat != PlatChargeBillStatusConstant.CHARGE_BILL_FINISH)) {//如果订单推送数据先于充电状态推送数据，订单已经更新为完成状态时，在处理晚到的充电状态时则不更新充电订单的状态

                        //获取停止充电阈值
                        BigDecimal threshold = new BigDecimal(PlatChargeBillStatusConstant.THRESHOLD);
                        if(odBillEntity.getChargeType() == PlatChargeBillStatusConstant.CHARGE_TYPE_QUOTA) {
                            if (currentMoney.compareTo(odBillEntity.getChargeMoney().subtract(threshold)) >= 0) {//超额
                                //调用停止充电请求
                                //获取platType
                                OmindOperatorEntity odOperatorEntity = omindOperatorService.getOperatorInfo(odConnectorEntity.getBaseOperatorId(), odConnectorEntity.getUserOperatorId());
                                if (odOperatorEntity == null) {
                                    throw new BaseException("运营商:" + odConnectorEntity.getBaseOperatorId() + "不存在");
                                }

                                //封装停止充电请求data
                                QueryStopChargeData queryStopChargeData = new QueryStopChargeData();
                                queryStopChargeData.setStartChargeSeq(startChargeSeq);
                                queryStopChargeData.setConnectorID(notificationEquipChargeStatusData.getConnectorID());

                                hlhtInfoService.stopCharge(queryStopChargeData);

                            }
                        }

                        OmindBillEntity updateBillInfoObjData = new OmindBillEntity();
                        updateBillInfoObjData.setBillId(odBillEntity.getBillId());
                        updateBillInfoObjData.setStartChargeSeq(odBillEntity.getStartChargeSeq());
                        updateBillInfoObjData.setStartChargeSeqStat(notificationEquipChargeStatusData.getStartChargeSeqStat());
                        if (currentMoney != null) {
                            currentMoney = currentMoney.setScale(2, RoundingMode.HALF_EVEN);
                        }
                        updateBillInfoObjData.setRealPayMoney(currentMoney);
                        updateBillInfoObjData.setTotalMoney(currentMoney);
                        if (detailRealElecMoney != null) {
                            detailRealElecMoney = detailRealElecMoney.setScale(2, RoundingMode.HALF_EVEN);
                        }
                        updateBillInfoObjData.setTotalElecMoney(detailRealElecMoney);
                        if (detailRealServiceMoney != null) {
                            detailRealServiceMoney = detailRealServiceMoney.setScale(2, RoundingMode.HALF_EVEN);
                        }
                        updateBillInfoObjData.setTotalServiceMoney(detailRealServiceMoney);
                        updateBillInfoObjData.setSoc(notificationEquipChargeStatusData.getSoc());
                        updateBillInfoObjData.setTotalPower(notificationEquipChargeStatusData.getTotalPower());
                        if (notificationEquipChargeStatusData.getStartTime() != null) {
                            updateBillInfoObjData.setStartTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, notificationEquipChargeStatusData.getStartTime()));
                        }
                        if (notificationEquipChargeStatusData.getEndTime() != null) {
                            updateBillInfoObjData.setEndTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, notificationEquipChargeStatusData.getEndTime()));
                        }
                        updateBillInfoObjData.setCurrentA(notificationEquipChargeStatusData.getCurrentA());
                        updateBillInfoObjData.setVoltageA(notificationEquipChargeStatusData.getVoltageA());

                        log.info("equip-charge-status==updatebillinfoobjdata=" + updateBillInfoObjData);
                        selfService.updateBillInfo(updateBillInfoObjData);
                    }
                }

            }
        } catch (BaseException ube) {
            log.error("bill-equipChargeStatusDeal-error", ube);
            throw ube;
        } catch (Exception e) {
            log.error("bill-equipChargeStatusDeal-error", e);
            throw new BaseException("未知错误");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void chargeOrderInfoDeal(NotificationChargeOrderInfoData notificationChargeOrderInfoData, OmindConnectorEntity odConnectorEntity, String operatorId, Short platType) throws BaseException {
        try {
            String startChargeSeq = notificationChargeOrderInfoData.getStartChargeSeq();
            String vin = notificationChargeOrderInfoData.getVin() != null ? notificationChargeOrderInfoData.getVin() : "";

            if (odConnectorEntity != null && !TextUtils.isBlank(startChargeSeq)) {

                OmindBillEntity odBillInfo = selfService.get(notificationChargeOrderInfoData.getStartChargeSeq());
                log.info("charge-order-info-----billentity==" + odBillInfo);

                //该充值订单运营人员已经人工强制结束或者充值订单信息重复推送，则不处理
                if (odBillInfo != null) {

                    //实际消费金额
                    BigDecimal realPayMoney = new BigDecimal("0.00");
                    //实际消费电费总金额
                    BigDecimal realPayElecMoney = new BigDecimal("0.00");
                    //实际消费服务费总金额
                    BigDecimal realPayServiceMoney = new BigDecimal("0.00");

                    realPayMoney = notificationChargeOrderInfoData.getTotalMoney();
                    realPayElecMoney = notificationChargeOrderInfoData.getTotalElecMoney();
                    realPayServiceMoney = notificationChargeOrderInfoData.getTotalSeviceMoney();

                    BigDecimal oriRealPayMoney = realPayMoney;

                    //更新充值订单表支付实际金额和订单状态
                    log.info("charge-order-info----realpaymoney=" + realPayMoney);
                    OmindBillEntity updateBillInfoObjData = new OmindBillEntity();
                    updateBillInfoObjData.setBillId(odBillInfo.getBillId());
                    updateBillInfoObjData.setStartChargeSeq(odBillInfo.getStartChargeSeq());
                    updateBillInfoObjData.setCarVin(vin);
                    updateBillInfoObjData.setStartTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, notificationChargeOrderInfoData.getStartTime()));
                    updateBillInfoObjData.setEndTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, notificationChargeOrderInfoData.getEndTime()));
                    updateBillInfoObjData.setTotalMoney(oriRealPayMoney);//存储应支付价格
                    updateBillInfoObjData.setTotalPower(notificationChargeOrderInfoData.getTotalPower());
                    updateBillInfoObjData.setTotalElecMoney(realPayElecMoney.setScale(2, RoundingMode.HALF_EVEN));
                    updateBillInfoObjData.setTotalServiceMoney(realPayServiceMoney.setScale(2, RoundingMode.HALF_EVEN));
                    updateBillInfoObjData.setRealPayMoney(realPayMoney.setScale(2, RoundingMode.HALF_EVEN));
                    updateBillInfoObjData.setStartChargeSeqStat((short) PlatChargeBillStatusConstant.CHARGE_BILL_FINISH);
                    updateBillInfoObjData.setPayState((short) 1);

                    if (notificationChargeOrderInfoData.getStopReason() != null) {
                        updateBillInfoObjData.setStopReason(notificationChargeOrderInfoData.getStopReason().intValue());
                    }
                    updateBillInfoObjData.setSumPeriod(notificationChargeOrderInfoData.getSumPeriod());
                    log.info("charge-order-info----updateBillInfoObjData==" + updateBillInfoObjData);
                    boolean upFlag = selfService.updateBillInfo(updateBillInfoObjData);
                    log.info("charge-order-info----updateBillInfoObjData--startChargeSeq=" + odBillInfo.getStartChargeSeq() + "|dealflag=" + upFlag);

                }
            }
        } catch (BaseException ube) {
            log.error("bill-chargeOrderInfoDeal-error", ube);
            throw ube;
        } catch (Exception e) {
            log.error("bill-chargeOrderInfoDeal-error", e);
            throw new BaseException("未知错误");
        }
    }

    @Override
    public OmindBillEntity selectBillInfoById(Long billId) {

        LambdaQueryWrapper<OmindBillEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OmindBillEntity::getBillId, billId).last("limit 1");

        return iService.getOne(lambdaQueryWrapper);
    }

    @Override
    public void forceStopBill(BillForceStopRequest billForceStopRequest) throws BaseException {
        selfService.forceStopBillDeal(billForceStopRequest);
    }

    @Override
    public void forceStopBillDeal(BillForceStopRequest billForceStopRequest) throws BaseException {
        if (billForceStopRequest.getId() == null || billForceStopRequest.getId() <= 0) {
            throw new BaseException("订单id参数无效");
        }
        if (billForceStopRequest.getElecFee() == null || billForceStopRequest.getServiceFee() == null) {
            throw new BaseException("结算金额参数无效");
        }
        OmindBillEntity odBillEntity = selfService.selectBillInfoById(billForceStopRequest.getId());
        if (odBillEntity == null) {
            throw new BaseException("账单不存在");
        }

        if (odBillEntity.getStartChargeSeqStat() == PlatChargeBillStatusConstant.CHARGE_BILL_FINISH
                || odBillEntity.getPayState() == 1) {
            throw new BaseException("该订单已处理或者为正常订单");
        }

        BigDecimal realPayMoney = billForceStopRequest.getElecFee().add(billForceStopRequest.getServiceFee()).setScale(2, RoundingMode.HALF_EVEN);

        //更新充值订单表支付实际金额和订单状态
        OmindBillEntity updateBillInfoObjData = new OmindBillEntity();
        updateBillInfoObjData.setBillId(odBillEntity.getBillId());
        updateBillInfoObjData.setStartChargeSeq(odBillEntity.getStartChargeSeq());
        updateBillInfoObjData.setRealPayMoney(realPayMoney.setScale(2, RoundingMode.HALF_EVEN));
        updateBillInfoObjData.setTotalElecMoney(billForceStopRequest.getElecFee().setScale(2, RoundingMode.HALF_EVEN));
        updateBillInfoObjData.setTotalServiceMoney(billForceStopRequest.getServiceFee().setScale(2, RoundingMode.HALF_EVEN));
        //标记为已处理异常订单
        updateBillInfoObjData.setStartChargeSeqStat((short) PlatChargeBillStatusConstant.CHARGE_BILL_FINISH);
        updateBillInfoObjData.setPayState((short) 1);
        selfService.updateBillInfo(updateBillInfoObjData);

    }

    @Override
    public String validateBillListlFields(BillPageRequest billPageRequest) {
        String msg = "";
        if (!TextUtils.isBlank(billPageRequest.getStartTime()) && !TextUtils.isBlank(billPageRequest.getEndTime())) {
            if (DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, billPageRequest.getStartTime()).getTime() > DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, billPageRequest.getEndTime()).getTime()) {
                return msg = "开始时间不应晚于结束时间";
            }
        }

        if (!TextUtils.isBlank(billPageRequest.getStartChargeSeq()) && billPageRequest.getStartChargeSeq().length() > 32) {
            return msg = "订单号太长啦";
        }

        if (!TextUtils.isBlank(billPageRequest.getMobile()) && billPageRequest.getMobile().length() != 11) {
            return msg = "手机号长度错误";
        }
        return msg;
    }

    @Override
    public TableDataInfo<OmindBillEntity> getBillList(BillPageRequest billPageRequest, PageQuery pageQuery) {
        LambdaQueryWrapper<OmindBillEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (!TextUtils.isBlank(billPageRequest.getStationId())) {
            lambdaQueryWrapper.eq(OmindBillEntity::getStationId, billPageRequest.getStationId());
        }
        if (!TextUtils.isBlank(billPageRequest.getConnectorId())) {
            lambdaQueryWrapper.eq(OmindBillEntity::getConnectorId, billPageRequest.getConnectorId());
        }
        if (!TextUtils.isBlank(billPageRequest.getMobile())) {
            List<OmindUserEntity> odUserList = omindUserService.selectUserListByMobile(billPageRequest.getMobile());
            List<Long> userIdList = new ArrayList<>();
            if (odUserList.size() > 0) {
                for (OmindUserEntity userInfo : odUserList) {
                    userIdList.add(userInfo.getUid());
                }
                lambdaQueryWrapper.in(OmindBillEntity::getUserId, userIdList);
            } else {
                //返回空结果
                lambdaQueryWrapper.isNull(OmindBillEntity::getBillId);
            }
        }
        if (!TextUtils.isBlank(billPageRequest.getStartChargeSeq())) {
            lambdaQueryWrapper.eq(OmindBillEntity::getStartChargeSeq, billPageRequest.getStartChargeSeq());
        }
        //订单状态查询
        if (billPageRequest.getStartChargeSeqStat() != null) {
            lambdaQueryWrapper.eq(OmindBillEntity::getStartChargeSeqStat, billPageRequest.getStartChargeSeqStat());
        }
        if (!TextUtils.isBlank(billPageRequest.getStartTime()) && TextUtils.isBlank(billPageRequest.getEndTime())) {
            lambdaQueryWrapper.ge(OmindBillEntity::getEndTime,
                    DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, billPageRequest.getStartTime()));
        }
        if (!TextUtils.isBlank(billPageRequest.getStartTime()) && !TextUtils.isBlank(billPageRequest.getEndTime())) {
            lambdaQueryWrapper.between(OmindBillEntity::getEndTime,
                    DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, billPageRequest.getStartTime()),
                    DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, billPageRequest.getEndTime()));
        }

        lambdaQueryWrapper.orderByDesc(OmindBillEntity::getBillId);

        Page<OmindBillEntity> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize(), true);
        IPage<OmindBillEntity> iPage = iService.page(page, lambdaQueryWrapper);

        if (iPage.getRecords() != null) {
            for (OmindBillEntity odBillEntity : iPage.getRecords()) {

                //用户信息
                OmindUserEntity odUserEntity = omindUserService.getUserById(odBillEntity.getUserId());
                String nickName = odUserEntity != null ? odUserEntity.getNickName() : "";
                String mobile = odUserEntity != null ? odUserEntity.getMobile() : "";
                odBillEntity.setNickName(nickName);
                odBillEntity.setMobile(mobile);

                //充电站附加信息
                //充电站信息
                OmindStationEntity odStationEntity = omindStationService.get(odBillEntity.getStationId());
                if (odStationEntity != null) {
                    odBillEntity.setStationName(odStationEntity.getStationName());

                    //基础运营商信息
                    OmindOperatorEntity odOperatorEntity = omindOperatorService.selectOperatorInfoById(odStationEntity.getBaseOperatorId());
                    odBillEntity.setOperatorName(odOperatorEntity != null ? odOperatorEntity.getOperatorName() : "");
                }
            }
        }

        return TableDataInfo.build(iPage);
    }

}
