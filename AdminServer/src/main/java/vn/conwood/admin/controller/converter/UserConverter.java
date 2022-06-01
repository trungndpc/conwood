package vn.conwood.admin.controller.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import vn.conwood.admin.controller.dto.PageDTO;
import vn.conwood.admin.controller.dto.UserDTO;
import vn.conwood.admin.controller.dto.metric.UserDataMetricDTO;
import vn.conwood.admin.controller.form.CustomerForm;
import vn.conwood.admin.mapper.Mapper;
import vn.conwood.jpa.entity.UserEntity;
import vn.conwood.jpa.metric.UserCityMetric;
import vn.conwood.jpa.metric.UserDataMetric;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserConverter {
    private static final Logger LOGGER = LogManager.getLogger();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Mapper mapper;

    public UserEntity map(UserEntity userEntity, CustomerForm form) {
        mapper.map(form, userEntity);
        return userEntity;
    }

    public UserEntity convert2Entity(CustomerForm registerForm) {
        return mapper.map(registerForm, UserEntity.class);
    }

    public UserDTO convert2DTO(UserEntity userEntity) {
        return mapper.map(userEntity, UserDTO.class);
    }

    public PageDTO<UserDTO> convertToPageDTO(Page<UserEntity> userEntityPage)  {
        List<UserEntity> content = userEntityPage.getContent();
        List<UserDTO> dtos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(content)) {
            for (UserEntity entity: content) {
                dtos.add(convert2DTO(entity));
            }
        }
        return new PageDTO<UserDTO>(userEntityPage.getNumber(), dtos.size(), userEntityPage.getTotalPages(), dtos);
    }

    public UserDataMetricDTO convert2DTO(UserDataMetric dataMetricDTO) {
        return mapper.map(dataMetricDTO, UserDataMetricDTO.class);
    }

    public UserCityMetric convert2DTO(UserCityMetric userCityMetric) {
        return mapper.map(userCityMetric, UserCityMetric.class);
    }

}
