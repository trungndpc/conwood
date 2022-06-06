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
import vn.conwood.jpa.entity.PostEntity;
import vn.conwood.jpa.entity.UserEntity;
import vn.conwood.client.controller.converter.PostConverter;
import vn.conwood.client.service.PostService;
import vn.conwood.client.util.AuthenticationUtils;

import java.util.List;

@RestController
@RequestMapping("/api/post")
public class PostController {
    private static final Logger LOGGER = LogManager.getLogger(PostController.class);

    @Autowired
    private PostService postService;

    @Autowired
    private PostConverter postConverter;

    @GetMapping(path = "/get")
    public ResponseEntity<BaseResponse> getById(@RequestParam(required = true) int id, Authentication auth) {
        BaseResponse response = new BaseResponse();
        try{
            PostEntity postEntity = postService.get(id);
            response.setData(postConverter.convert2DTO(postEntity));
        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/list")
    public ResponseEntity<BaseResponse> get(Authentication auth) {
        BaseResponse response = new BaseResponse();
        try{
            UserEntity user = AuthenticationUtils.getAuthUser(auth);
            if (user == null) {
                throw new Exception("not permission");
            }
            List<PostEntity> posts = postService.findPost(user);
            response.setData(postConverter.covert2DTOs(posts));
        }catch (Exception e) {
            LOGGER.error(e.getMessage());
            response.setError(ErrorCode.FAILED);
            response.setMsg(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}
