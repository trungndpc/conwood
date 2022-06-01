package vn.conwood.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.conwood.common.status.StatusPost;
import vn.conwood.jpa.entity.PostEntity;
import vn.conwood.jpa.entity.UserEntity;
import vn.conwood.jpa.repository.PostRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public List<PostEntity> findPost(UserEntity userEntity) {
        long currentTime = System.currentTimeMillis();
        List<PostEntity> postEntities = postRepository.findAllByOrderByUpdatedTimeDesc();
        postEntities = postEntities.stream()
                .filter(post -> post.getStatus() == StatusPost.PUBLISHED)
                .filter(post -> post.getCityIds() != null ? post.getCityIds().contains(userEntity.getCityId()) : true)
                .filter(post -> post.getDistrictIds() != null ? post.getDistrictIds().contains(userEntity.getDistrictId()) : true)
                .filter(post -> currentTime >= post.getTimeStart() && currentTime <= post.getTimeEnd())
                .collect(Collectors.toList());
        return postEntities;
    }

    public PostEntity findPost(int promotionId, UserEntity userEntity) {
        long currentTime = System.currentTimeMillis();
        List<PostEntity> postEntities = postRepository.findAllByOrderByUpdatedTimeDesc();
        Optional<PostEntity> first = postEntities.stream().filter(post -> {
                    return post.getPromotionId() != null && post.getPromotionId() == promotionId;
                })
                .filter(post -> {
                    if (post.getDistrictIds() != null) {
                        return post.getDistrictIds().contains(userEntity.getDistrictId());
                    }
                    if (post.getCityIds() != null) {
                        return post.getCityIds().contains(userEntity.getCityId());
                    }
                    return true;
                })
                .filter(post -> post.getTimeStart() <= currentTime)
                .filter(post -> post.getTimeEnd() >= currentTime)
                .findFirst();
        if (first.isPresent()) {
            return first.get();
        }
        return null;
    }

    public PostEntity get(int id) {
        return postRepository.getOne(id);
    }

}
