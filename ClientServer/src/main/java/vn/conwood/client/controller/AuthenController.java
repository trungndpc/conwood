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
import vn.conwood.client.config.Constant;
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
import java.util.Optional;
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
                LOGGER.error("failed: accessToken");
                return "OK";
            }

            ZaloUserEntity zaloUserEntity = ZaloService.INSTANCE.getUserInfo(accessToken);
            if (zaloUserEntity == null) {
                LOGGER.error("can not get user info");
                response.sendRedirect("/oops");
                return "OK";
            }

            user = convert2UserEntity(zaloUserEntity, src);
            if (user == null) {
                LOGGER.error("convert2UserEntity is null");
                response.sendRedirect("/oops");
                return "OK";
            }

            if (!StringUtils.isEmpty(utm)) {
                user.setUtm(utm);
            }

            if (user.getStatus() == StatusUser.WAITING_ACTIVE) {
                user.setStatus(StatusUser.APPROVED);
                user.setUtm("ZNS");
            }

            //to get user id to store into session
            user = userRepository.saveAndFlush(user);
            String session = TokenUtil.generate(user.getId(), user.getPhone(), 43200000L);
            AuthenticationUtils.writeCookie(Constant.CONWOOD_SESSION_NAME, session, response);
            List<String> lstSession = ListUtils.putWithMaximumSize(session, user.getSessions());
            user.setSessions(lstSession);
            user = userRepository.saveAndFlush(user);

            if (StringUtils.isEmpty(user.getFollowerId())) {
                response.sendRedirect(new StringBuilder("https://zalo.me/")
                        .append(appConfig.ZALO_OA_ID).toString());
                return "OK";
            }
        }

        if (user.getStatus() == StatusUser.WAIT_COMPLETE_PROFILE) {
            return RenderUtils.render("index.html");
        }

        response.sendRedirect(StringUtils.isEmpty(continueUrl) ? "/" : continueUrl);
        return "OK";
    }

    private UserEntity convert2UserEntity(ZaloUserEntity zaloUserEntity, String src) {
        UserEntity userEntity = userRepository.findByZaloId(zaloUserEntity.getId());
        if(userEntity == null) {
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

        if (userEntity.getStatus() == StatusUser.APPROVED) {
            return userEntity;
        }

        userEntity.setZaloId(zaloUserEntity.getId());
        userEntity.setAvatar(zaloUserEntity.getAvatar());
        if (userEntity.getRoleId() == null) {
            userEntity.setName(zaloUserEntity.getName());
            userEntity.setPassword("");
            userEntity.setStatus(StatusUser.WAIT_COMPLETE_PROFILE);
            userEntity.setRoleId(Permission.ANONYMOUS.getId());
        }

        if (!StringUtils.isEmpty(zaloUserEntity.getBirthday())) {
            userEntity.setBirthday(TimeUtil.getTime(zaloUserEntity.getBirthday()));
        }

        return userEntity;
    }


}
