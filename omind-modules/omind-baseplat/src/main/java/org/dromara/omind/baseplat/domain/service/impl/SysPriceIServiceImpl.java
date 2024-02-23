package org.dromara.omind.baseplat.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.omind.baseplat.api.domain.entity.SysPrice;
import org.dromara.omind.baseplat.domain.mapper.SysPriceMapper;
import org.dromara.omind.baseplat.domain.service.SysPriceIService;
import org.springframework.stereotype.Service;

@Service
public class SysPriceIServiceImpl extends ServiceImpl<SysPriceMapper, SysPrice> implements SysPriceIService {
}
