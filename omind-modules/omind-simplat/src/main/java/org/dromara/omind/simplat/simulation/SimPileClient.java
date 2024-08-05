package org.dromara.omind.simplat.simulation;

import cn.hutool.core.util.RandomUtil;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.api.common.utils.ip.IpUtils;
import org.dromara.omind.baseplat.api.domain.PolicyInfoData;
import org.dromara.omind.baseplat.api.domain.entity.*;
import org.dromara.omind.baseplat.api.service.*;
import org.dromara.omind.mq.api.producer.ChargeOrderProducer;
import org.dromara.omind.mq.api.producer.HeartBeatProducer;
import org.dromara.omind.mq.api.producer.RealtimeDataProducer;
import org.dromara.omind.simplat.service.StartChargingReturnService;
import org.dromara.omind.simplat.service.StopChargingReturnService;
import org.dromara.omind.simplat.simulation.interfaces.SimPileIClient;
import org.dromara.omind.simplat.utils.ApplicationContextProvider;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Log4j2
@Data
public class SimPileClient implements SimPileIClient {

    /**
     * 充电枪信息
     */
    private SysConnector sysConnector = null;
    /**
     * 充电桩信息
     */
    private SysEquipment sysEquipment;

    long lastRealtime = 0;
    SysChargeOrder sysChargeOrder;
    Map<String, SysChargeOrderItem> chargeOrderItemMap;

    StartChargingReturnService startChargingReturnService = ApplicationContextProvider.getBean(StartChargingReturnService.class);
    StopChargingReturnService stopChargingReturnService = ApplicationContextProvider.getBean(StopChargingReturnService.class);

    ChargeOrderProducer chargeOrderProducer = ApplicationContextProvider.getBean(ChargeOrderProducer.class);
    HeartBeatProducer heartBeatProducer = ApplicationContextProvider.getBean(HeartBeatProducer.class);
    RealtimeDataProducer realtimeDataProducer = ApplicationContextProvider.getBean(RealtimeDataProducer.class);
    RemoteSysPriceService remoteSysPriceService = ApplicationContextProvider.getBean(RemoteSysPriceService.class);
    RemoteSysEquipmentService remoteSysEquipmentService = ApplicationContextProvider.getBean(RemoteSysEquipmentService.class);
    RemoteSysChargeOrderService remoteSysChargeOrderService = ApplicationContextProvider.getBean(RemoteSysChargeOrderService.class);

    RemoteTradeService remoteTradeService;

    public SimPileClient(RemoteTradeService remoteTradeService, SysEquipment sysEquipment, SysConnector sysConnector){

        chargeOrderItemMap = new LinkedHashMap<>();
        this.sysConnector = sysConnector;
        this.sysEquipment = sysEquipment;
        this.remoteTradeService = remoteTradeService;

    }

    @Override
    public void start() throws BaseException {
        if(this.sysConnector != null){
            this.sysConnector.setStatus((short)1);
        }
        String ip = IpUtils.getHostIp();
        SysEquipment upEquipment = new SysEquipment();
        upEquipment.setId(sysEquipment.getId());
        upEquipment.setServerIp(ip);
        remoteSysEquipmentService.update(upEquipment);

        this.startHeart();
    }

    @Override
    public void stop() throws BaseException {
        sysConnector.setStatus((short)0);
        sendRealTimeData(true);
        log.info("【模拟桩" + sysConnector.getConnectorId() + "】" + "下线");
    }

    @Override
    public void link() throws BaseException {
        if(sysConnector.getStatus() == 2 || sysConnector.getStatus() == 3){
            return;
        }
        //插枪
        log.info("=====》插枪");
        sysConnector.setStatus((short)2);
        sendRealTimeData(true);
    }

    @Override
    public void unlink() throws BaseException {
        if(sysConnector.getStatus() == 3){
            log.info("=====》充电中无法拔枪");
        }
        //拔枪
        log.info("=====》拔枪");
        sysConnector.setStatus((short)1);
        sendRealTimeData(true);
    }

    @Override
    public void startCharge(SysChargeOrder sysChargeOrder) throws BaseException {
        log.info("充电桩"+ sysConnector.getConnectorId() +"状态码："+sysConnector.getStatus());
        chargeOrderItemMap = new HashMap<>();
        if(sysConnector.getStatus() == 2){
            //可以充电
            log.info("充电桩"+sysConnector.getConnectorId()+"状态码：设置3");
            sysConnector.setStatus((short)3);
        }
        else{
            startChargingReturnService.startFail(sysChargeOrder);
            throw new BaseException("启动失败-状态"+ sysConnector.getStatus());
        }
        SysChargeOrder dbInfo = remoteSysChargeOrderService.getChargeOrderByStartChargeSeq(sysChargeOrder.getStartChargeSeq());

        if(dbInfo == null){
            log.error("[启动充电]=>订单数据为null");
        }
        log.info("[启动充电]=>tradeNo=" + sysChargeOrder.getStartChargeSeq());
        log.info("" + dbInfo.getStartChargeSeq());

        this.sysChargeOrder = new SysChargeOrder();
        this.sysChargeOrder.setStartChargeSeq(dbInfo.getStartChargeSeq());
        this.sysChargeOrder.setStartChargeSeqStat((short)2);
        this.sysChargeOrder.setTradeNo(dbInfo.getStartChargeSeq());
        this.sysChargeOrder.setConnectorId(sysConnector.getConnectorId());
        this.sysChargeOrder.setConnectorStatus(3);
        this.sysChargeOrder.setCurrentA(RandomUtil.randomBigDecimal(new BigDecimal("200")));
        this.sysChargeOrder.setVoltageA(RandomUtil.randomBigDecimal(new BigDecimal("400")));
        this.sysChargeOrder.setSoc(RandomUtil.randomBigDecimal(new BigDecimal("0.001"), new BigDecimal("0.600")));
        this.sysChargeOrder.setStartTime(new Date());
        this.sysChargeOrder.setEndTime(new Date());
        this.sysChargeOrder.setTotalPower(new BigDecimal("0.00"));
        this.sysChargeOrder.setTotalMoney(new BigDecimal("0.00"));
        this.sysChargeOrder.setElecMoney(new BigDecimal("0.00"));
        this.sysChargeOrder.setServiceMoney(new BigDecimal("0.00"));
        this.sysChargeOrder.setSumPeriod((short)0);
        this.sysChargeOrder.setCarVin(RandomUtil.randomStringUpper(17));
        this.sysChargeOrder.setSyncFlag((short)0);
        this.sysChargeOrder.setReportGov((short)0);
        sysConnector.setStatus((short)3);

        startChargingReturnService.startSuccess(this.sysChargeOrder);
        sendRealTimeData(true);
    }

    @Override
    public void stopCharge(int type, SysChargeOrder sysChargeOrder) throws BaseException {
        if(type == 1){
            //人工应答
            log.info("【人工停机命令】桩编号：" + sysChargeOrder.getConnectorId());
            if(sysConnector.getStatus() == 3){
                sysConnector.setStatus((short)2);
                sendRealTimeData(true);
                stopChargingReturnService.stopSuccess(sysChargeOrder);
            }
            else{
                stopChargingReturnService.stopFail(sysChargeOrder);
                throw new BaseException("停止失败");
            }
            log.info("【发送停机回复】桩编号：" + sysChargeOrder.getConnectorId());

        }
        this.sysChargeOrder = null;
        sendTradeInfo(type, sysChargeOrder.getStartChargeSeq());
    }

    @Override
    public void sendRealTimeData(Boolean sendForce) throws BaseException {
        try {
            long minDurant = (sysChargeOrder == null)?60000:15000;
            if((System.currentTimeMillis() - lastRealtime < minDurant) && !sendForce)
            {
                return;
            }
            lastRealtime = System.currentTimeMillis();

            short status = 0; //0离线 1故障 2空闲 3充电
            short isHome = 1;
            short isPlugn = 0;
            if(sysConnector.getStatus() == 0){
                status = 2;
            }
            else if(sysConnector.getStatus() == 1){
                status = 2;
            }
            else if(sysConnector.getStatus() == 2){
                status = 2;
                isHome = 0;
                isPlugn = 1;
            }
            else if(sysConnector.getStatus() == 3){
                status = 3;
                isHome = 0;
                isPlugn = 1;
            }
            else if(sysConnector.getStatus() == 4){
                status = 2;
            }
            else{
                status = 255;
            }

            PlatConnectorRealtimeData platConnectorRealtimeData = null;
            if(sysConnector.getStatus() == 3){
                culChargeState();
                if(sysConnector.getStatus() != 3){
                    return;
                }
                platConnectorRealtimeData = new PlatConnectorRealtimeData();
                platConnectorRealtimeData.setTradeNo(sysChargeOrder.getTradeNo());
                platConnectorRealtimeData.setConnectorId(sysConnector.getConnectorId());
                platConnectorRealtimeData.setPileNo(sysConnector.getEquipmentId());
                platConnectorRealtimeData.setGunNo("1");
                platConnectorRealtimeData.setState(status);
                platConnectorRealtimeData.setGunState(isHome);
                platConnectorRealtimeData.setGunLink(isPlugn);
                platConnectorRealtimeData.setOutVoltage(sysChargeOrder.getVoltageA());
                platConnectorRealtimeData.setOutCurrent(sysChargeOrder.getCurrentA());
                platConnectorRealtimeData.setGunlineTemp(RandomUtil.randomInt(60, 80));
                platConnectorRealtimeData.setGunlineNo(sysConnector.getGunNo().longValue());
                platConnectorRealtimeData.setSoc(sysChargeOrder.getSoc().setScale(2,RoundingMode.DOWN));
                platConnectorRealtimeData.setBatteryMaxTemp(RandomUtil.randomInt(60, 80));
                platConnectorRealtimeData.setTotalChargeDurant((int)((sysChargeOrder.getEndTime().getTime() - sysChargeOrder.getStartTime().getTime())/60000));
                platConnectorRealtimeData.setRemainChargeDurent((int)((1.0-sysChargeOrder.getSoc().floatValue()) / 0.008));
                platConnectorRealtimeData.setChargingKWH(sysChargeOrder.getTotalPower());
                platConnectorRealtimeData.setLoseKwh(sysChargeOrder.getTotalPower());
                platConnectorRealtimeData.setChargeMoney(sysChargeOrder.getTotalMoney());
                platConnectorRealtimeData.setCreateTime(new Date());
            }
            else {
                //发送一般实时状态
                platConnectorRealtimeData = new PlatConnectorRealtimeData();
                platConnectorRealtimeData.setTradeNo("");
                platConnectorRealtimeData.setConnectorId(sysConnector.getConnectorId());
                platConnectorRealtimeData.setPileNo(sysConnector.getEquipmentId());
                platConnectorRealtimeData.setGunNo("1");
                platConnectorRealtimeData.setState(status);
                platConnectorRealtimeData.setGunState(isHome);
                platConnectorRealtimeData.setGunLink(isPlugn);
                platConnectorRealtimeData.setOutVoltage(RandomUtil.randomBigDecimal(new BigDecimal(220.0), new BigDecimal(800.0)));
                platConnectorRealtimeData.setOutCurrent(RandomUtil.randomBigDecimal(new BigDecimal(10.0), new BigDecimal(200.0)));
                platConnectorRealtimeData.setGunlineTemp(RandomUtil.randomInt(60, 80));
                platConnectorRealtimeData.setGunlineNo(sysConnector.getGunNo().longValue());
                platConnectorRealtimeData.setSoc(new BigDecimal("0.00"));
                platConnectorRealtimeData.setBatteryMaxTemp(0);
                platConnectorRealtimeData.setTotalChargeDurant(0);
                platConnectorRealtimeData.setRemainChargeDurent(0);
                platConnectorRealtimeData.setChargingKWH(new BigDecimal("0.00"));
                platConnectorRealtimeData.setLoseKwh(new BigDecimal("0.00"));
                platConnectorRealtimeData.setChargeMoney(new BigDecimal("0.00"));
                platConnectorRealtimeData.setCreateTime(new Date());
            }
            realtimeDataProducer.sendMsg(platConnectorRealtimeData);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendTradeInfo(int stopType, String startChargeSeq) throws BaseException {
        //充电完成后，更新充电订单信息，发送充电数据
        SysChargeOrder theChargeOrder = remoteSysChargeOrderService.getChargeOrderByStartChargeSeq(startChargeSeq);
        if(theChargeOrder == null){
            return;
        }
        theChargeOrder.setEndTime(new Date());

        PlatTradingRecordData platTradingRecordData = new PlatTradingRecordData();
        platTradingRecordData.setTradeNo(theChargeOrder.getStartChargeSeq());
        platTradingRecordData.setPileNo(sysEquipment.getPileNo());
        platTradingRecordData.setGunNo(sysConnector.getGunNo() + "");
        platTradingRecordData.setStartTime(theChargeOrder.getStartTime());
        platTradingRecordData.setEndTime(new Date());

        platTradingRecordData.setSharpPerPrice(new BigDecimal("0.00"));
        platTradingRecordData.setSharpKwh(new BigDecimal("0.00"));
        platTradingRecordData.setSharpAllKwh(new BigDecimal("0.00"));
        platTradingRecordData.setSharpPrice(new BigDecimal("0.00"));

        platTradingRecordData.setPeakPerPrice(new BigDecimal("0.00"));
        platTradingRecordData.setPeakKwh(new BigDecimal("0.00"));
        platTradingRecordData.setPeakAllKwh(new BigDecimal("0.00"));
        platTradingRecordData.setPeakPrice(new BigDecimal("0.00"));

        platTradingRecordData.setFlatPerPrice(new BigDecimal("0.00"));
        platTradingRecordData.setFlatKwh(new BigDecimal("0.00"));
        platTradingRecordData.setFlatAllKwh(new BigDecimal("0.00"));
        platTradingRecordData.setFlatPrice(new BigDecimal("0.00"));

        platTradingRecordData.setValleyPerPrice(new BigDecimal("0.00"));
        platTradingRecordData.setValleyKwh(new BigDecimal("0.00"));
        platTradingRecordData.setValleyAllKwh(new BigDecimal("0.00"));
        platTradingRecordData.setValleyPrice(new BigDecimal("0.00"));

        platTradingRecordData.setStartKwh(new BigDecimal("0.00"));
        platTradingRecordData.setEndKwh(theChargeOrder.getTotalPower());
        platTradingRecordData.setFinalKwh(theChargeOrder.getTotalPower());
        platTradingRecordData.setFinalAllKwh(theChargeOrder.getTotalPower());

        platTradingRecordData.setCost(theChargeOrder.getTotalMoney());
        platTradingRecordData.setVin(theChargeOrder.getCarVin());
        platTradingRecordData.setTradeType((short)1);
        platTradingRecordData.setTradeTime(new Date());
        platTradingRecordData.setStopType(stopType);
        platTradingRecordData.setSystemCardNo(theChargeOrder.getPhoneNum());


        remoteTradeService.finishTrade(platTradingRecordData);

        //remoteSysChargeOrderService.update(theChargeOrder);
        chargeOrderProducer.sendMsg(theChargeOrder);
    }

    @Override
    public void startHeart() throws BaseException {
        new Thread(()->{
            for(;;) {
                try {
                    //发送一般心跳
                    log.info("循环-心跳----" + sysConnector.getConnectorId());
                    if(sysConnector != null) {
                        heartBeatProducer.sendMsg(sysConnector.getConnectorId());
                    }
                    Thread.sleep(5000);
                    if(sysConnector != null && sysConnector.getStatus() == 0){
                        break;
                    }
                } catch (InterruptedException e) {
                    log.error(e.toString(), e);
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    log.error(e.toString(), e);
                    throw new RuntimeException(e);
                }

            }
        }).start();
        new Thread(()->{
            for(;;) {
                try {
                    log.info("循环-实时数据----" + sysConnector.getConnectorId());
                    sendRealTimeData(false);
                    if(sysChargeOrder != null){
                        Thread.sleep(5000);
                    }
                    else {
                        Thread.sleep(10000);
                    }
                    if(sysConnector != null && sysConnector.getStatus() == 0){
                        break;
                    }
                } catch (InterruptedException e) {
                    log.error(e.toString(), e);
                    throw new RuntimeException(e);
                } catch (Exception ex){
                    log.error(ex.toString(), ex);
                }
            }
        }).start();
    }


    private void culChargeState()
    {
        if(sysChargeOrder == null || sysConnector.getStatus() != 3){
            //异常情况 中止充电
            stopCharge(0, sysChargeOrder);
            return;
        }

        if(sysChargeOrder.getSoc().floatValue() > 0.98){
            //停止充电
            stopCharge(0, sysChargeOrder);
            return;
        }
        sysChargeOrder.setSoc(sysChargeOrder.getSoc().add(new BigDecimal("0.001")).setScale(3, RoundingMode.DOWN));
        sysChargeOrder.setCurrentA(RandomUtil.randomBigDecimal(new BigDecimal(200)).setScale(2,RoundingMode.UP));
        sysChargeOrder.setVoltageA(RandomUtil.randomBigDecimal(new BigDecimal(400)).setScale(2,RoundingMode.UP));
        sysChargeOrder.setEndTime(new Date());
        BigDecimal currentPower = RandomUtil.randomBigDecimal(new BigDecimal("0.2"), new BigDecimal("0.5")).setScale(5,RoundingMode.UP);
        sysChargeOrder.setTotalPower(sysChargeOrder.getTotalPower().add(currentPower).setScale(5,RoundingMode.DOWN));

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        PolicyInfoData policyInfoData = remoteSysPriceService.getCurrentPrice(sysEquipment.getStationId(), System.currentTimeMillis());
        BigDecimal priceS = new BigDecimal(0.00000);
        BigDecimal priceE = new BigDecimal(0.00000);
        priceE = policyInfoData.getElecPrice();
        priceS = policyInfoData.getSevicePrice();

        BigDecimal currentPrice = priceE.add(priceS);
        SysChargeOrderItem item = null;
        if(chargeOrderItemMap.containsKey(hour+"")){
            item = chargeOrderItemMap.get(hour+"");

            item.setPower(item.getPower().add(currentPower));
            item.setElecMoney(item.getElecMoney().add(priceE.multiply(currentPower)).setScale(4, RoundingMode.UP));
            item.setServiceMoney(item.getServiceMoney().add(priceE.multiply(currentPower)));
        }
        else{
            item = new SysChargeOrderItem();
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(date);
            startCal.set(Calendar.MINUTE, 0);
            startCal.set(Calendar.SECOND, 0);
            item.setStartTime(startCal.getTime());
            startCal.set(Calendar.MINUTE, 59);
            startCal.set(Calendar.SECOND, 59);
            item.setEndTime(startCal.getTime());

            item.setPower(currentPower);
            item.setStartChargeSeq(sysChargeOrder.getStartChargeSeq());
            item.setElecPrice(priceE);
            item.setSevicePrice(priceS);
            item.setElecMoney(priceE.multiply(currentPower));
            item.setServiceMoney(priceE.multiply(currentPower));
            chargeOrderItemMap.put(hour+"", item);
        }
        sysChargeOrder.setTotalMoney(sysChargeOrder.getTotalMoney().add(currentPrice.multiply(currentPower)));
        sysChargeOrder.setElecMoney(sysChargeOrder.getElecMoney().add(priceE.multiply(currentPower)).setScale(5,RoundingMode.UP));
        sysChargeOrder.setServiceMoney(sysChargeOrder.getServiceMoney().add(priceS.multiply(currentPower)).setScale(5,RoundingMode.UP));
        sysChargeOrder.setSumPeriod((short)chargeOrderItemMap.size());

        log.info("[实时状态]->充电度数" + sysChargeOrder.getTotalPower());
        log.info("[实时状态]->时段内充电度数" + currentPower);
        log.info("[实时状态]->SOC百分比" + sysChargeOrder.getSoc());
        log.info("[实时状态]->金额" + sysChargeOrder.getTotalMoney());
        log.info("[实时状态]->时段" + chargeOrderItemMap.size());
        log.info("[实时状态]->电压" + sysChargeOrder.getVoltageA());
        log.info("[实时状态]->电流" + sysChargeOrder.getCurrentA());
        log.info("[实时状态]->电价" + currentPrice.floatValue());
        log.info("[实时状态]->时" + hour);
        log.info("[实时状态]->电价类别" + policyInfoData.getPriceType());
    }
}
