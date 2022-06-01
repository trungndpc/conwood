package vn.conwood.admin.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.conwood.admin.common.BaseResponse;
import vn.conwood.admin.common.ErrorCode;
import vn.conwood.admin.common.UserStatus;
import vn.conwood.admin.service.UserService;

@RestController
@RequestMapping("/int")
public class InternalController {
    private static final Logger LOGGER = LogManager.getLogger(InternalController.class);

    @Autowired
    private UserService userService;

    @GetMapping(path = "/auto-approved")
    public ResponseEntity<BaseResponse> autoApproval(@RequestParam(required = true) int uid) {
        BaseResponse response = new BaseResponse();
        try{
            userService.updateStatus(uid, UserStatus.APPROVED, null);
            response.setError(0);
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}
