package vn.conwood.admin.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.conwood.admin.controller.dto.dashboard.CountUserDTO;
import vn.conwood.admin.common.BaseResponse;
import vn.conwood.admin.common.ErrorCode;
import vn.conwood.admin.common.UserStatus;
import vn.conwood.admin.controller.converter.UserConverter;
import vn.conwood.admin.controller.dto.PageDTO;
import vn.conwood.admin.controller.dto.UserDTO;
import vn.conwood.admin.controller.dto.metric.UserDataMetricDTO;
import vn.conwood.admin.controller.form.CustomerForm;
import vn.conwood.admin.service.UserService;
import vn.conwood.common.status.StatusUser;
import vn.conwood.jpa.entity.UserEntity;
import vn.conwood.jpa.metric.UserCityMetric;
import vn.conwood.jpa.metric.UserDataMetric;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger LOGGER = LogManager.getLogger(UserController.class);

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private UserService userService;

    @GetMapping(path = "/get")
    public ResponseEntity<BaseResponse> get(@RequestParam(required = true) int id) {
        BaseResponse response = new BaseResponse();
        try{
            UserEntity userEntity = userService.findById(id);
            UserDTO userDTO = userConverter.convert2DTO(userEntity);
            response.setData(userDTO);
        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/find")
    public ResponseEntity<BaseResponse> find(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer city,
            @RequestParam(required = false, defaultValue = "0") int page,
                                             @RequestParam(required = false, defaultValue = "10") int pageSize) {
        BaseResponse response = new BaseResponse();
        try{
            Page<UserEntity> userEntityPage = userService.find(search, status, city, page, pageSize);
            PageDTO<UserDTO> userDTOPageDTO = userConverter.convertToPageDTO(userEntityPage);
            response.setData(userDTOPageDTO);
        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/upload-customer", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse> post(@RequestBody CustomerForm form) {
        BaseResponse response = new BaseResponse();
        try{
            UserEntity customer = userConverter.convert2Entity(form);
            customer = userService.createUserFromInseeCustomer(customer);
            UserDTO customerDTO = userConverter.convert2DTO(customer);
            response.setData(customerDTO);
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/update", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse> post(@RequestBody CustomerForm form, @RequestParam(required = true) Integer id) {
        BaseResponse response = new BaseResponse();
        try{
            UserEntity userEntity = userService.findById(id);
            userEntity = userConverter.map(userEntity, form);
            userEntity = userService.update(userEntity);
            UserDTO customerDTO = userConverter.convert2DTO(userEntity);
            response.setData(customerDTO);
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/update-status")
    public ResponseEntity<BaseResponse> updateStatus(@RequestParam(required = true) int uid,
                                                     @RequestParam(required = true) int status,
                                                     @RequestParam(required = false) String note) {
        BaseResponse response = new BaseResponse();
        try{
            userService.updateStatus(uid, status, note);
        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/count")
    public ResponseEntity<BaseResponse> count(@RequestParam(required = false) Integer location) {
        BaseResponse response = new BaseResponse();
        try{
            CountUserDTO countUserDTO = new CountUserDTO();
            countUserDTO.setNumUser(userService.count(location, Arrays.asList(UserStatus.APPROVED,
                    UserStatus.WAITING_ACTIVE, UserStatus.WAIT_APPROVAL)));
            countUserDTO.setNumApprovedUser(userService.count(location, Arrays.asList(UserStatus.APPROVED)));
            countUserDTO.setNumWaitingActiveUser(userService.count(location, Arrays.asList(UserStatus.WAITING_ACTIVE)));
            countUserDTO.setNumWaitingReviewUser(userService.count(location, Arrays.asList(UserStatus.WAIT_APPROVAL)));
            response.setData(countUserDTO);
        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/stats-user-date")
    public ResponseEntity<BaseResponse> statisticUserByDate() {
        BaseResponse response = new BaseResponse();
        try{
            List<UserDataMetric> userDataMetrics = userService.statisticUserByDate(Arrays.asList(StatusUser.APPROVED));
            List<UserDataMetricDTO> dtos = userDataMetrics.stream().map(userDataMetric -> {
                return userConverter.convert2DTO(userDataMetric);
            }).collect(Collectors.toList());
            response.setData(dtos);
        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/stats-user-city")
    public ResponseEntity<BaseResponse> statisticUserByCity() {
        BaseResponse response = new BaseResponse();
        try{
            List<UserCityMetric> userCityMetrics = userService.statisticUserByCity();
            List<UserCityMetric> dtos = userCityMetrics.stream().map(userCityMetric -> {
                return userConverter.convert2DTO(userCityMetric);
            }).collect(Collectors.toList());
            response.setData(dtos);
        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }


}
