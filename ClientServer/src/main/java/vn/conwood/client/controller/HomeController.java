package vn.conwood.client.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import vn.conwood.common.Permission;
import vn.conwood.jpa.entity.PostEntity;
import vn.conwood.jpa.entity.PromotionEntity;
import vn.conwood.jpa.entity.UserEntity;
import vn.conwood.client.controller.converter.UserConverter;
import vn.conwood.client.service.PostService;
import vn.conwood.client.service.StockPromotionService;
import vn.conwood.client.service.UserService;
import vn.conwood.client.util.AuthenticationUtils;
import vn.conwood.util.HttpUtil;
import vn.conwood.util.RenderUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
public class HomeController {
    private static final Logger LOGGER = LogManager.getLogger(HomeController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StockPromotionService stockPromotionService;

    @Autowired
    private PostService postService;

    @GetMapping(value = "/gioi-thieu-chuong-trinh-moi", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String introduce(HttpServletResponse response) throws
            IOException {
        response.sendRedirect("/");
        return "OK";
    }


    @GetMapping(value = "/khoe-kho-nhan-qua", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String stockPromotion(Authentication auth, HttpServletRequest request, HttpServletResponse response) throws
            IOException {
        UserEntity authUser = AuthenticationUtils.getAuthUser(auth);
        if (authUser == null) {
            String continueUrl = HttpUtil.getFullURL(request);
            response.sendRedirect("/dang-ky?continueUrl=" + HttpUtil.encodeUrl(continueUrl));
            return "OK";
        }
        if (!isRetailer(authUser)) {
            response.sendRedirect("/oops");
            return "OK";
        }

        PromotionEntity promotionEntity = stockPromotionService.find(authUser);
        if (promotionEntity != null) {
            PostEntity post = postService.findPost(promotionEntity.getId(), authUser);
            if (post != null) {
                response.sendRedirect("/bai-viet/" + post.getId());
                return "OK";
            }
        }
        response.sendRedirect("/");
        return "OK";
    }

    @GetMapping(value = "/**", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String index(Authentication auth, HttpServletRequest request, HttpServletResponse response) throws
            IOException {
        UserEntity authUser = AuthenticationUtils.getAuthUser(auth);
        if (authUser == null) {
            String continueUrl = HttpUtil.getFullURL(request);
            response.sendRedirect("/dang-ky?continueUrl=" + HttpUtil.encodeUrl(continueUrl));
            return "OK";
        }
        if (!isRetailer(authUser)) {
            response.sendRedirect("/oops");
            return "OK";
        }
        return RenderUtils.render("index.html");
    }

    private boolean isRetailer(UserEntity userEntity) {
        return userEntity.getRoleId() == Permission.RETAILER.getId();
    }

}
