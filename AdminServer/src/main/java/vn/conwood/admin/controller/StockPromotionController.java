package vn.conwood.admin.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.conwood.admin.controller.dto.StockPromotionDTO;
import vn.conwood.admin.common.BaseResponse;
import vn.conwood.admin.common.ErrorCode;
import vn.conwood.admin.controller.converter.StockConverter;
import vn.conwood.admin.service.StockPromotionService;
import vn.conwood.jpa.entity.promotion.StockPromotionEntity;


@RestController
@RequestMapping("/api/promotion/stock")
public class StockPromotionController {

    private static final Logger LOGGER = LogManager.getLogger(StockPromotionController.class);

    @Autowired
    private StockPromotionService promotionService;

    @Autowired
    private StockConverter stockConverter;

    @GetMapping(path = "/get")
    public ResponseEntity<BaseResponse> get(@RequestParam(required = true) int id) {
        BaseResponse response = new BaseResponse();
        try{
            StockPromotionEntity stockPromotionEntity = promotionService.get(id);
            StockPromotionDTO stockPromotionDTO = stockConverter.convert2DTO(stockPromotionEntity);
            response.setData(stockPromotionDTO);
        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

}
