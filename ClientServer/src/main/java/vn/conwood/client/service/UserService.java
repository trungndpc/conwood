package vn.conwood.client.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vn.conwood.common.Constant;
import vn.conwood.common.status.StatusUser;
import vn.conwood.jpa.entity.UserEntity;
import vn.conwood.jpa.repository.UserRepository;
import vn.conwood.jpa.specification.UserSpecification;

import java.util.List;

@Service
public class UserService {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger LOGGER = LogManager.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSpecification userSpecification;

    public UserEntity register(UserEntity userEntity) throws Exception {
        if (userEntity.getStatus() != StatusUser.WAIT_COMPLETE_PROFILE) {
            throw new Exception("status is not valid to update profile | status: " + userEntity.getStatus() + ", id: " + userEntity.getId());
        }
        String phone = userEntity.getPhone();
        UserEntity exitUserEntity = userRepository.findByPhone(phone);
        if (exitUserEntity != null) {
            if (exitUserEntity.getStatus() == StatusUser.APPROVED) {
                throw new Exception("status is not valid to update profile | phone: " + userEntity.getPhone() + ", id: " + userEntity.getId());
            }
            if (exitUserEntity.getStatus() == StatusUser.WAITING_ACTIVE) {
                userEntity.setPairingId(exitUserEntity.getId());
                userEntity.setAddress(exitUserEntity.getAddress());
                userEntity.setDistrictId(exitUserEntity.getDistrictId());
                userEntity.setCityId(exitUserEntity.getCityId());
            }
        }
        userEntity.setStatus(StatusUser.WAIT_APPROVAL);
        userRepository.saveAndFlush(userEntity);

        String utm = userEntity.getUtm();
        if (!StringUtils.isEmpty(utm)
                && ("WORKSHOP_001".equals(utm) || "SALER".equals(utm) || "TRADER".equals(utm))) {
            this.restTemplate.getForEntity(Constant.ADMIN_DOMAIN
                    + "/int/auto-approved?uid=" + userEntity.getId(), String.class);
        }
        return userEntity;
    }

    public UserEntity findByPhone(String phone){
        return userRepository.findByPhone(phone);
    }

    public UserEntity findById(int id) {
        return userRepository.getOne(id);
    }

    public UserEntity findByZaloId(long zaloId) {
        return userRepository.findByZaloId(String.valueOf(zaloId));
    }

    public UserEntity findByFollowerId(String followerId) {
        return userRepository.findByFollowerId(followerId);
    }

    public UserEntity saveOrUpdate(UserEntity userEntity) {
        return userRepository.saveAndFlush(userEntity);
    }

}
