package vn.conwood.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.conwood.admin.message.User;
import vn.conwood.admin.message.gift.PhoneCardMessage;
import vn.conwood.common.type.TypeGift;
import vn.conwood.jpa.entity.GiftEntity;
import vn.conwood.jpa.entity.UserEntity;
import vn.conwood.jpa.repository.GiftRepository;
import vn.conwood.jpa.specification.GiftSpecification;

@Service
public class GiftService {
    @Autowired
    private GiftRepository giftRepository;

    @Autowired
    private GiftSpecification giftSpecification;

    public GiftEntity create(GiftEntity form) {
        return giftRepository.saveAndFlush(form) ;
    }

    public long count(Integer status) {
        Specification<GiftEntity> specs =  Specification.where(null);
        if (status != null) {
            specs = specs.and(giftSpecification.isStatus(status));
        }
        return giftRepository.count(specs);
    }

    public void sendGift(GiftEntity giftEntity, UserEntity userEntity) {
        User user = new User(userEntity.getId(), userEntity.getFollowerId(), userEntity.getName());
        if (giftEntity.getType() == TypeGift.CARD_PHONE) {
            PhoneCardMessage phoneCardMessage = new PhoneCardMessage(user, user.getName(), giftEntity.getTitle(), giftEntity.getId());
            phoneCardMessage.send();
        }
    }

    public Page<GiftEntity> find(Integer status, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Specification<GiftEntity> specs =  Specification.where(null);
        if (status != null) {
            specs = specs.and(giftSpecification.isStatus(status));
        }
        return giftRepository.findAll(specs, pageable);
    }

    public long countByStatus(int status) {
        return giftRepository.countByStatus(status);
    }

}
