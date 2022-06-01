package vn.conwood.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.conwood.common.status.StatusForm;
import vn.conwood.jpa.entity.FormEntity;
import vn.conwood.jpa.repository.FormRepository;

import java.util.List;

@Service
public class FormService {
    @Autowired
    private FormRepository formRepository;

    public List<FormEntity> findByUserId(int userId) {
        return formRepository.findByUserId(userId);
    }

    public void updateReceivedStatusGift(int giftId) {
        List<FormEntity> formEntityList = formRepository.findAll();
        if (formEntityList != null) {
            for (FormEntity form: formEntityList) {
                if (form.getGifts() != null && form.getGifts().contains(giftId)) {
                    form.setStatus(StatusForm.RECEIVED);
                    formRepository.saveAndFlush(form);
                }
            }
        }
    }
}
