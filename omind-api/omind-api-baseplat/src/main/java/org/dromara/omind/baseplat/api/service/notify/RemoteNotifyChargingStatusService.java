package org.dromara.omind.baseplat.api.service.notify;

import org.dromara.common.core.exception.base.BaseException;
import org.dromara.omind.baseplat.api.domain.entity.*;

/**
 * 远程停机命令回复
 * 上传实时数据
 * 用户主动结束
 * 从服务器断开
 */
public interface RemoteNotifyChargingStatusService {

    /**
     * 远程停机
     * @param sysChargeOrder
     */
    void remoteStopCharging(SysChargeOrder sysChargeOrder) throws BaseException;

    void realtimeData(SysChargeOrder sysChargeOrder) throws BaseException;

    void chargerStop(SysChargeOrder sysChargeOrder) throws BaseException;

    void disconnect(String startChargeSeq) throws BaseException;

}
