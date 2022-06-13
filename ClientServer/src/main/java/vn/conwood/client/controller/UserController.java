package vn.conwood.client.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.conwood.common.BaseResponse;
import vn.conwood.common.ErrorCode;
import vn.conwood.jpa.entity.UserEntity;
import vn.conwood.client.controller.converter.UserConverter;
import vn.conwood.client.controller.dto.UserDTO;
import vn.conwood.client.controller.form.RegisterForm;
import vn.conwood.client.service.UserService;
import vn.conwood.client.util.AuthenticationUtils;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger LOGGER = LogManager.getLogger(UserController.class);

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private UserService userService;

    @GetMapping(path = "/me")
    public ResponseEntity<BaseResponse> get(Authentication auth) {
        BaseResponse response = new BaseResponse();
        try{
            UserEntity user = AuthenticationUtils.getAuthUser(auth);
            if (user == null) {
                throw new Exception("not permission");
            }
            user = userService.findById(user.getId());
            UserDTO userDTO = userConverter.convert2DTO(user);
            response.setData(userDTO);
        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse> post(@RequestBody RegisterForm form, Authentication auth) {
        BaseResponse response = new BaseResponse();
        try{
            UserEntity user = AuthenticationUtils.getAuthUser(auth);
            if (user == null) {
                throw new Exception("not permission");
            }
            user.setPhone(form.getPhone());
            if (!StringUtils.isEmpty(form.getName())) {
                user.setName(form.getName());
            }

            if (form.getCityId() != null && form.getCityId() != 0) {
                user.setCityId(form.getCityId());
            }

            if (form.getDistrictId() != null && form.getDistrictId() != 0) {
                user.setDistrictId(form.getDistrictId());
            }

            if (!StringUtils.isEmpty(form.getAddress())) {
                user.setAddress(form.getAddress());
            }

            if (form.getRoleId() != null && form.getRoleId() != 0) {
                user.setRoleId(form.getRoleId());
            }

            LOGGER.error("nameCompany: " + form.getNameCompany());
            LOGGER.error("position: " + form.getPosition());
            if (form.getNameCompany() != null && !StringUtils.isEmpty(form.getNameCompany())) {
                user.setNameCompany(form.getNameCompany());
            }

            if (form.getPosition() != null && !StringUtils.isEmpty(form.getPosition())) {
                user.setPosition(form.getPosition());
            }

            user = userService.register(user);
            response.setData(userConverter.convert2DTO(user));
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/check-phone")
    public ResponseEntity<BaseResponse> checkPhone(@RequestParam(required = true) String phone, Authentication auth) {
        BaseResponse response = new BaseResponse();
        try{
            UserEntity user = AuthenticationUtils.getAuthUser(auth);
            if (user == null) {
                throw new Exception("not permission");
            }
            UserEntity byPhone = userService.findByPhone(phone);
            if (byPhone == null) {
                response.setError(-9);
            }else {
                response.setError(byPhone.getStatus());
            }
        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}
