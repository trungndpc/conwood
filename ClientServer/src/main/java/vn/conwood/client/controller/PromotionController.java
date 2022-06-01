package vn.conwood.client.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.conwood.common.BaseResponse;
import vn.conwood.common.ErrorCode;
import vn.conwood.jpa.entity.PromotionEntity;
import vn.conwood.jpa.entity.UserEntity;
import vn.conwood.client.controller.converter.PromotionConverter;
import vn.conwood.client.service.PromotionService;
import vn.conwood.client.util.AuthenticationUtils;

@RestController
@RequestMapping("/api/promotion")
public class PromotionController {
    private static final Logger LOGGER = LogManager.getLogger(PromotionController.class);

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private PromotionConverter promotionConverter;

    @GetMapping(path = "/get")
    public ResponseEntity<BaseResponse> getById(@RequestParam(required = true) int id, Authentication auth) {
        BaseResponse response = new BaseResponse();
        try{
            UserEntity user = AuthenticationUtils.getAuthUser(auth);
            if (user == null) {
                throw new Exception("not permission");
            }
            PromotionEntity promotionEntity = promotionService.get(id);
            response.setData(promotionConverter.convert2DTO(promotionEntity));
        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

}
