package vn.conwood.admin.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.conwood.admin.controller.dto.PromotionDTO;
import vn.conwood.admin.common.BaseResponse;
import vn.conwood.admin.common.ErrorCode;
import vn.conwood.admin.controller.converter.PromotionConverter;
import vn.conwood.admin.controller.converter.StockConverter;
import vn.conwood.admin.controller.dto.PageDTO;
import vn.conwood.admin.controller.form.PromotionForm;
import vn.conwood.admin.service.PromotionService;
import vn.conwood.admin.service.StockPromotionService;
import vn.conwood.common.status.StatusPromotion;
import vn.conwood.common.type.TypePromotion;
import vn.conwood.jpa.entity.PromotionEntity;
import vn.conwood.jpa.entity.promotion.StockPromotionEntity;

import java.util.List;

@RestController
@RequestMapping("/api/promotion")
public class PromotionController {
    private static final Logger LOGGER = LogManager.getLogger(PromotionConverter.class);
    @Autowired
    private PromotionService promotionService;

    @Autowired
    private StockPromotionService stockPromotionService;

    @Autowired
    private PromotionConverter promotionConverter;

    @Autowired
    private StockConverter stockConverter;



    @PostMapping(path = "/update", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse> update(@RequestBody PromotionForm form) {
        BaseResponse response = new BaseResponse();
        try{
            PromotionEntity promotionEntity = promotionService.get(form.getId());
            promotionEntity.setTitle(form.getTitle());
            promotionEntity.setTimeStart(form.getTimeStart());
            promotionEntity.setTimeEnd(form.getTimeEnd());
            promotionEntity.setCityIds(form.getCityIds());
            promotionEntity.setDistrictIds(form.getDistrictIds());
            promotionService.update(promotionEntity);
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/list")
    public ResponseEntity<BaseResponse> list (
            @RequestParam(required = false) List<Integer> types,
            @RequestParam(required = false, defaultValue = "0") int page,
                                             @RequestParam(required = false, defaultValue = "10") int pageSize) {
        BaseResponse response = new BaseResponse();
        try{
            Page<PromotionEntity> promotionEntityPage = promotionService.find(types, page, pageSize);
            PageDTO<PromotionDTO> promotionDTOPageDTO =
                    promotionConverter.convertToPageDTO(promotionEntityPage);
            response.setData(promotionDTOPageDTO);
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/list-promotion-for-map-post")
    public ResponseEntity<BaseResponse> listPromotionForMapPost() {
        BaseResponse response = new BaseResponse();
        try{
            List<PromotionEntity> promotionForMapPost = promotionService.findPromotionForMapPost();
            response.setData(promotionConverter.covert2DTOOnlyIdAndName(promotionForMapPost));
        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/get")
    public ResponseEntity<BaseResponse> get(@RequestParam(required = true) int id) {
        BaseResponse response = new BaseResponse();
        try{
            PromotionEntity promotionEntity = promotionService.get(id);
            PromotionDTO promotionDTO = promotionConverter.convert2DTO(promotionEntity);
            response.setData(promotionDTO);
        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/update-status")
    public ResponseEntity<BaseResponse> updateStatus(@RequestParam(required = true) int id,
                                                     @RequestParam(required = true) int status) {
        BaseResponse response = new BaseResponse();
        try{
            PromotionEntity entity = promotionService.updateStatus(id, status);
            PromotionDTO promotionDTO = promotionConverter.convert2DTO(entity);
            response.setData(promotionDTO);
        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }



}
