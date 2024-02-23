package org.dromara.omind.simplat.simulation.interfaces;

import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.baseplat.api.domain.entity.SysChargeOrder;

public interface SimPileIClient {

    /**
     * 充电桩启动
     */
    void start() throws BaseException;

    /**
     * 充电桩停止（断电）
     */
    void stop() throws BaseException;

    /**
     * 充电桩-车插枪
     */
    void link() throws BaseException;

    /**
     * 充电桩-车拔枪
     */
    void unlink() throws BaseException;

    /**
     * 启动充电
     */
    void startCharge(SysChargeOrder sysChargeOrder) throws BaseException;

    /**
     * 停止充电
     */
    void stopCharge(int type, SysChargeOrder sysChargeOrder) throws BaseException;

    void sendRealTimeData(Boolean sendForce) throws BaseException;

    void sendTradeInfo(int stopType, String startChargeSeq) throws BaseException;

    void startHeart() throws BaseException;

}
