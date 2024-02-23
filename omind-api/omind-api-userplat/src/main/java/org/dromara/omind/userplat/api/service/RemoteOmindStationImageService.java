package org.dromara.omind.userplat.api.service;

import org.dromara.omind.userplat.api.domain.entity.OmindStationImageEntity;

import java.util.List;

public interface RemoteOmindStationImageService {

    List<OmindStationImageEntity> all(String stationId);

}
