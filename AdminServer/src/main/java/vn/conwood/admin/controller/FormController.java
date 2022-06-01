package vn.conwood.admin.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.conwood.admin.controller.dto.FormDTO;
import vn.conwood.admin.controller.dto.StockFormDTO;
import vn.conwood.admin.controller.dto.dashboard.CountFormDTO;
import vn.conwood.admin.controller.dto.dashboard.CountPromotionDTO;
import vn.conwood.admin.controller.dto.metric.FormCityMetricDTO;
import vn.conwood.admin.controller.dto.metric.FormDateMetricDTO;
import vn.conwood.admin.controller.dto.metric.FormPromotionMetricDTO;
import vn.conwood.admin.common.BaseResponse;
import vn.conwood.admin.common.ErrorCode;
import vn.conwood.admin.controller.converter.FormConverter;
import vn.conwood.admin.controller.dto.PageDTO;
import vn.conwood.admin.service.FormService;
import vn.conwood.admin.service.PromotionService;
import vn.conwood.common.status.StatusForm;
import vn.conwood.common.status.StatusPromotion;
import vn.conwood.common.type.TypePromotion;
import vn.conwood.jpa.entity.FormEntity;
import vn.conwood.jpa.entity.PromotionEntity;
import vn.conwood.jpa.entity.form.StockFormEntity;
import vn.conwood.jpa.metric.FormCityMetric;
import vn.conwood.jpa.metric.FormDateMetric;
import vn.conwood.jpa.metric.FormPromotionMetric;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/form")
public class FormController {
    private static final Logger LOGGER = LogManager.getLogger(FormController.class);

    @Autowired
    private FormService formService;

    @Autowired
    private FormConverter formConverter;

    @Autowired
    private PromotionService promotionService;

    @GetMapping(path = "/find-by-promotion")
    public ResponseEntity<BaseResponse> list( @RequestParam(required = true) int promotionId,
            @RequestParam(required = false) Integer city,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize) {
        BaseResponse response = new BaseResponse();
        try{
            Page<FormEntity> formEntities = formService.findByPromotionId(promotionId, status, city, search, page, pageSize);
            PageDTO<FormDTO> promotionDTOPageDTO =
                    formConverter.convertToPageDTO(formEntities);
            response.setData(promotionDTOPageDTO);
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/find-by-promotions")
    public ResponseEntity<BaseResponse> list( @RequestParam(required = true) List<Integer> promotionIds,
                                              @RequestParam(required = false) Integer city,
                                              @RequestParam(required = false) Integer status,
                                              @RequestParam(required = false) String search,
                                              @RequestParam(required = false, defaultValue = "0") int page,
                                              @RequestParam(required = false, defaultValue = "10") int pageSize) {
        BaseResponse response = new BaseResponse();
        try{
            Page<FormEntity> formEntities = formService.findByPromotions(promotionIds,
                    status, city, search, page, pageSize);
            PageDTO<FormDTO> promotionDTOPageDTO =
                    formConverter.convertToPageDTO(formEntities);
            response.setData(promotionDTOPageDTO);
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/count")
    public ResponseEntity<BaseResponse> count(@RequestParam(required = false) Integer status) {
        BaseResponse response = new BaseResponse();
        try{
            List<PromotionEntity> promotionEntityList = promotionService.find(TypePromotion.STOCK_PROMOTION_TYPE,
                    StatusPromotion.APPROVED);
            long countPromotion = formService.count(promotionEntityList.stream()
                    .map(p -> p.getId()).collect(Collectors.toList()), status);

            List<PromotionEntity> engagementPromotionEntityList = promotionService.find(TypePromotion.LIGHTING_QUIZ_GAME_PROMOTION_TYPE,
                    StatusPromotion.APPROVED);
            long countEngagement = formService.count(engagementPromotionEntityList.stream().map(p -> p.getId())
                    .collect(Collectors.toList()), status);
            CountPromotionDTO promotionDTO = new CountPromotionDTO();
            promotionDTO.setPromotion((int) countPromotion);
            promotionDTO.setEngagement((int) countEngagement);
            response.setData(promotionDTO);
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/get")
    public ResponseEntity<BaseResponse> get(@RequestParam(required = true) int id) {
        BaseResponse response = new BaseResponse();
        try{
            FormEntity formEntity = formService.getById(id);
            if (formEntity.getType() == TypePromotion.STOCK_PROMOTION_TYPE) {
                StockFormDTO stockFormDTO = formConverter.convert2StockFormDTO((StockFormEntity) formEntity);
                response.setData(stockFormDTO);
            }else {
                FormDTO formDTO = formConverter.convert2FormDTO(formEntity);
                response.setData(formDTO);
            }
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }


    @GetMapping(path = "/update-status")
    public ResponseEntity<BaseResponse> updateStatus(@RequestParam(required = true) int id,
                                                     @RequestParam(required = true) int status,
                                                     @RequestParam(required = false) String note) {
        BaseResponse response = new BaseResponse();
        try{
            formService.updateStatus(id, status, note);
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/stats-form-date")
    public ResponseEntity<BaseResponse> statisticUserByDate(@RequestParam(required = false) List<Integer> promotionIds) {
        BaseResponse response = new BaseResponse();
        try{
            List<FormDateMetric> formDateMetrics = formService.statisticFormByDate(promotionIds);
            List<FormDateMetricDTO> dtos = formDateMetrics.stream().map(userDataMetric -> {
                return formConverter.map(userDataMetric);
            }).collect(Collectors.toList());
            response.setData(dtos);
        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/stats-form-city")
    public ResponseEntity<BaseResponse> statisticUserByCity(@RequestParam(required = false) List<Integer> promotionIds) {
        BaseResponse response = new BaseResponse();
        try{
            List<FormCityMetric> formCityMetrics = formService.statisticFormByCity(promotionIds);
            List<FormCityMetricDTO> dtos = formCityMetrics.stream().map(userDataMetric -> {
                return formConverter.map(userDataMetric);
            }).collect(Collectors.toList());
            response.setData(dtos);
        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/stats-form-promotion")
    public ResponseEntity<BaseResponse> statisticFormPromotion() {
        BaseResponse response = new BaseResponse();
        try{
            List<FormPromotionMetric> formPromotionMetrics = formService.statisticFormByPromotion();
            List<FormPromotionMetricDTO> dtos = formPromotionMetrics.stream().map(userDataMetric -> {
                return formConverter.map(userDataMetric);
            }).collect(Collectors.toList());
            response.setData(dtos);
        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/count-by-type-promotion")
    public ResponseEntity<BaseResponse> countByTypePromotion(@RequestParam(required = true) int type) {
        BaseResponse response = new BaseResponse();
        try{
            List<PromotionEntity> promotionEntityList = promotionService.find(type,StatusPromotion.APPROVED);
            long total = formService.count(promotionEntityList.stream().map(p -> p.getId()).collect(Collectors.toList()), null);
            long approved = formService.count(promotionEntityList.stream().map(p -> p.getId()).collect(Collectors.toList()), StatusForm.APPROVED);
            long init = formService.count(promotionEntityList.stream().map(p -> p.getId()).collect(Collectors.toList()), StatusForm.INIT);
            long sentGift = formService.count(promotionEntityList.stream().map(p -> p.getId()).collect(Collectors.toList()), StatusForm.SENT_GIFT);
            CountFormDTO countFormDTO = new CountFormDTO();
            countFormDTO.setTotal((int) total);
            countFormDTO.setApproved((int) approved);
            countFormDTO.setInit((int) init);
            countFormDTO.setSendGift((int) sentGift);
            response.setData(countFormDTO);
        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }



}
