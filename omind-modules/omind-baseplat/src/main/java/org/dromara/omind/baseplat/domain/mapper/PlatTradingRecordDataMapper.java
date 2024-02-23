package org.dromara.omind.baseplat.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.dromara.omind.baseplat.api.domain.entity.PlatTradingRecordData;
import org.springframework.stereotype.Repository;

@Repository()
@Mapper
public interface PlatTradingRecordDataMapper extends BaseMapper<PlatTradingRecordData> {
}
