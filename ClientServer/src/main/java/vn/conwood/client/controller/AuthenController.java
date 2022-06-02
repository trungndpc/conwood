package vn.conwood.client.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vn.conwood.client.config.AppConfig;
import vn.conwood.common.Permission;
import vn.conwood.common.status.StatusUser;
import vn.conwood.jpa.entity.UserEntity;
import vn.conwood.jpa.repository.UserRepository;
import vn.conwood.client.util.AuthenticationUtils;
import vn.conwood.client.wrapper.ZaloService;
import vn.conwood.client.wrapper.entity.ZaloUserEntity;
import vn.conwood.util.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class AuthenController {
    private static final Logger LOGGER = LogManager.getLogger(AuthenController.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppConfig appConfig;

    public static final ConcurrentHashMap<String, Integer> MAP_FOLLOWER = new ConcurrentHashMap<>();

    @GetMapping(value = "/dang-ky", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String register(Authentication authentication,
                           @RequestParam(required = false) String continueUrl,
                           @RequestParam(required = false) String code,
                           @RequestParam(required = false) String src,
                           @RequestParam(required = false) String utm,
                           HttpServletResponse response) throws Exception {
        UserEntity user = AuthenticationUtils.getAuthUser(authentication);
        if (user == null && StringUtils.isEmpty(code)) {
            StringBuilder hookBuilder = new StringBuilder(appConfig.CLIENT_DOMAIN);
            hookBuilder.append("/dang-ky");
            if (!StringUtils.isEmpty(continueUrl)) {
                hookBuilder.append("?continueUrl=");
                hookBuilder.append(continueUrl);
                if (!StringUtils.isEmpty(src)) {
                    hookBuilder.append("&src=" + src);
                }
                if (!StringUtils.isEmpty(utm)) {
                    hookBuilder.append("&utm=" + utm);
                }
            }else {
                if (!StringUtils.isEmpty(src)) {
                    hookBuilder.append("?src=" + src);
                    if (!StringUtils.isEmpty(utm)) {
                        hookBuilder.append("&utm=" + utm);
                    }
                }else {
                    if (!StringUtils.isEmpty(utm)) {
                        hookBuilder.append("?utm=" + utm);
                    }
                }
            }
            StringBuilder urlAuthenZaloBuilder = new StringBuilder(appConfig.getAuthenZaloUrl());
            urlAuthenZaloBuilder.append("&redirect_uri=");
            urlAuthenZaloBuilder.append(HttpUtil.encodeUrl(hookBuilder.toString()));
            response.sendRedirect(urlAuthenZaloBuilder.toString());
            return "OK";
        }

        if (user == null && !StringUtils.isEmpty(code)) {
            String accessToken = ZaloService.INSTANCE.getAccessToken(code);
            if (StringUtils.isEmpty(accessToken)) {
                response.sendRedirect("/oops");
                return "OK";
            }

            ZaloUserEntity zaloUserEntity = ZaloService.INSTANCE.getUserInfo(accessToken);
            if (zaloUserEntity == null) {
                response.sendRedirect("/oops");
                return "OK";
            }

            user = convert2UserEntity(zaloUserEntity, src);
            if (user == null) {
                response.sendRedirect("/oops");
                return "OK";
            }

            if (!StringUtils.isEmpty(utm)) {
                user.setUtm(utm);
            }

            user.setRoleId(Permission.RETAILER.getId());
            //to get user id to store into session
            user = userRepository.saveAndFlush(user);
            String session = TokenUtil.generate(user.getId(), user.getPhone(), 43200000L);
            AuthenticationUtils.writeCookie("_rtl_insee_ss", session, response);
            List<String> lstSession = ListUtils.putWithMaximumSize(session, user.getSessions());
            user.setSessions(lstSession);
            user = userRepository.saveAndFlush(user);

            if (StringUtils.isEmpty(user.getFollowerId())) {
                response.sendRedirect(new StringBuilder("https://zalo.me/")
                        .append(appConfig.ZALO_OA_ID).toString());
                return "OK";
            }
        }

        if (user.getRoleId() == Permission.RETAILER.getId()) {
            if (user.getStatus() == StatusUser.WAIT_COMPLETE_PROFILE) {
                return RenderUtils.render("index.html");
            }else {
                response.sendRedirect(StringUtils.isEmpty(continueUrl) ? "/" : continueUrl);
                return "OK";
            }
        }else {
            response.sendRedirect("/oops");
            return "OK";
        }
    }

    private UserEntity convert2UserEntity(ZaloUserEntity zaloUserEntity, String src) {
        UserEntity userEntity = userRepository.findByZaloId(zaloUserEntity.getId());
        if(userEntity == null) {
            userEntity = new UserEntity();
            userEntity.setId(0);

            //Waiting event follow
            Integer id = MAP_FOLLOWER.getOrDefault(zaloUserEntity.getId(), null);
            if (id == null){
                for (int i = 0; i < 10; i++) {
                    id = MAP_FOLLOWER.getOrDefault(zaloUserEntity.getId(), null);
                    if (id != null) {
                        break;
                    }
                    try {
                        Thread.sleep(200);
                    }catch (Exception e) {
                    }
                }
            }
            userEntity = userRepository.getOne(id);
        }

        //Remove to ignore over heap
        MAP_FOLLOWER.remove(userEntity.getZaloId());
        if (userEntity.getRoleId() == null) {
            userEntity.setRoleId(Permission.ANONYMOUS.getId());
            userEntity.setName(zaloUserEntity.getName());
            userEntity.setZaloId(zaloUserEntity.getId());
            userEntity.setPassword(new String());
            userEntity.setAvatar(zaloUserEntity.getAvatar());
            userEntity.setStatus(StatusUser.WAIT_COMPLETE_PROFILE);
            if (!StringUtils.isEmpty(zaloUserEntity.getBirthday())) {
                userEntity.setBirthday(TimeUtil.getTime(zaloUserEntity.getBirthday()));
            }
        }

        if (userEntity.getStatus() == StatusUser.WAITING_ACTIVE) {
            userEntity.setStatus(StatusUser.APPROVED);
            userEntity.setUtm("ZNS");
            userEntity.setRoleId(Permission.RETAILER.getId());
            userEntity.setAvatar(zaloUserEntity.getAvatar());
            userEntity.setPassword(new String());
            userEntity.setRoleId(Permission.RETAILER.getId());
            if (!StringUtils.isEmpty(zaloUserEntity.getBirthday())) {
                userEntity.setBirthday(TimeUtil.getTime(zaloUserEntity.getBirthday()));
            }
            userRepository.saveAndFlush(userEntity);
            return userEntity;
        }


        if (!StringUtils.isEmpty(src)) {
            try {
                int customerId =  Integer.parseInt(src);
                UserEntity customer = userRepository.getOne(customerId);
                if (customer != null && customer.getStatus() == StatusUser.WAITING_ACTIVE) {
                    userEntity = link2CustomerProfile(userEntity, customer);
                }
            }catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return userEntity;
    }

    private UserEntity link2CustomerProfile(UserEntity  newUserEntity, UserEntity customer) {
        newUserEntity.setPhone(customer.getPhone());
        newUserEntity.setName(customer.getName());
        newUserEntity.setCityId(customer.getCityId());
        newUserEntity.setDistrictId(customer.getDistrictId());
        newUserEntity.setAddress(customer.getAddress());
        newUserEntity.setStatus(StatusUser.APPROVED);
        userRepository.delete(customer);
        userRepository.saveAndFlush(newUserEntity);
        return newUserEntity;
    }

}
