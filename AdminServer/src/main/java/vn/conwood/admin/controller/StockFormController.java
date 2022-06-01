package vn.conwood.admin.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.conwood.admin.controller.dto.StockFormDTO;
import vn.conwood.admin.common.BaseResponse;
import vn.conwood.admin.common.ErrorCode;
import vn.conwood.admin.controller.converter.FormConverter;
import vn.conwood.admin.controller.form.UpdateStockForm;
import vn.conwood.admin.service.FormService;
import vn.conwood.jpa.entity.form.StockFormEntity;

@RestController
@RequestMapping("/api/stock-form")
public class StockFormController {
    private static final Logger LOGGER = LogManager.getLogger(StockFormController.class);

    @Autowired
    private FormService formService;

    @Autowired
    private FormConverter formConverter;

    @PostMapping(path = "/update", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BaseResponse> update(@RequestParam(required = true) int id, @RequestBody UpdateStockForm form) {
        BaseResponse response = new BaseResponse();
        try{
            StockFormEntity formEntity = (StockFormEntity) formService.getById(id);
            formEntity.setNote(form.getNote());
            formService.update(formEntity);
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }


}
