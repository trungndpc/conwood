package vn.conwood.client.mapper;

import org.modelmapper.PropertyMap;
import vn.conwood.jpa.entity.FormEntity;
import vn.conwood.client.controller.dto.FormHistoryDTO;

public class FormEntity2FormHistoryDTO extends PropertyMap<FormEntity, FormHistoryDTO> {
    @Override
    protected void configure() {
//        using(Mapper.ZONE_DATE_TIME_2_LONG).map(source.getUpdatedTime(), destination.getUpdatedTime());
    }
}
