package vn.conwood.admin.controller.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import vn.conwood.admin.controller.dto.PageDTO;
import vn.conwood.admin.controller.dto.PostDTO;
import vn.conwood.admin.controller.form.PostForm;
import vn.conwood.admin.mapper.Mapper;
import vn.conwood.jpa.entity.PostEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostConverter {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Mapper mapper;

    public PostEntity convert2Entity(PostForm postForm) {
        return mapper.map(postForm, PostEntity.class);
    }

    public PostDTO convert2DTO(PostEntity postEntity) {
        return mapper.map(postEntity, PostDTO.class);
    }

    public PageDTO<PostDTO> convertToPageDTO(Page<PostEntity> postEntities)  {
        List<PostEntity> content = postEntities.getContent();
        List<PostDTO> dtos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(content)) {
            for (PostEntity entity: content) {
                dtos.add(convert2DTO(entity));
            }
        }
        return new PageDTO<PostDTO>(postEntities.getNumber(), dtos.size(), postEntities.getTotalPages(), dtos);
    }

    public List<PostDTO> convert2ListDTO(List<PostEntity> postEntities) {
        return postEntities.stream().map(this::convert2DTO).collect(Collectors.toList());
    }
}
