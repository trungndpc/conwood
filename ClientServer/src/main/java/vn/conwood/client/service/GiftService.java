package vn.conwood.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.conwood.jpa.entity.GiftEntity;
import vn.conwood.jpa.repository.GiftRepository;

import java.util.Optional;

@Service
public class GiftService {

    @Autowired
    private GiftRepository giftRepository;

    public GiftEntity get(int id) {
        Optional<GiftEntity> optionalGiftEntity = giftRepository.findById(id);
        if (optionalGiftEntity.isPresent()) {
            return optionalGiftEntity.get();
        }
        return null;
    }

    public GiftEntity updateStatus(int id, int status) {
        GiftEntity one = giftRepository.getOne(id);
        one.setStatus(status);
        giftRepository.saveAndFlush(one);
        return one;
    }
}
