package vn.conwood.client.controller.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.conwood.jpa.entity.UserEntity;
import vn.conwood.client.controller.dto.UserDTO;
import vn.conwood.client.controller.form.RegisterForm;
import vn.conwood.client.mapper.Mapper;

@Component
public class UserConverter {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Mapper mapper;

    public UserEntity convert2Entity(RegisterForm registerForm) {
        return mapper.map(registerForm, UserEntity.class);
    }

    public UserDTO convert2DTO(UserEntity userEntity) {
        return mapper.map(userEntity, UserDTO.class);
    }

}
