package vn.conwood.admin.mapper;

import org.modelmapper.PropertyMap;
import vn.conwood.admin.controller.dto.UserDTO;
import vn.conwood.jpa.entity.UserEntity;

public class UserEntity2UserDTO  extends PropertyMap<UserEntity, UserDTO> {

    @Override
    protected void configure() {
        using(Mapper.ZONE_DATE_TIME_2_LONG).map(source.getCreatedTime(), destination.getCreatedTime());
        using(Mapper.ZONE_DATE_TIME_2_LONG).map(source.getUpdatedTime(), destination.getUpdatedTime());
    }
}
