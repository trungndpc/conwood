package vn.conwood.client.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import vn.conwood.common.BaseResponse;
import vn.conwood.common.ErrorCode;
import vn.conwood.jpa.entity.UserEntity;
import vn.conwood.jpa.entity.form.StockFormEntity;
import vn.conwood.client.controller.converter.StockFormConverter;
import vn.conwood.client.controller.form.StockForm;
import vn.conwood.client.service.StockFormService;
import vn.conwood.client.util.AuthenticationUtils;

@RestController
@RequestMapping("/api/stock-form")
public class StockFormController {
    private static final Logger LOGGER = LogManager.getLogger(StockFormController.class);

    @Autowired
    private StockFormConverter stockFormConverter;

    @Autowired
    private StockFormService stockFormService;


    @PostMapping(path = "/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse> post(@RequestBody StockForm form, Authentication auth) {
        BaseResponse response = new BaseResponse();
        try{
            UserEntity user = AuthenticationUtils.getAuthUser(auth);
            if (user == null) {
                throw new Exception("not permission");
            }
            StockFormEntity stockFormEntity = stockFormConverter.convert2Entity(form);
            stockFormEntity.setUserId(user.getId());
            stockFormEntity =  stockFormService.create(stockFormEntity);
            if (stockFormEntity == null) {
                response.setError(ErrorCode.FAILED);
            }
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

}
