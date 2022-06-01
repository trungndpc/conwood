package vn.conwood.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.conwood.common.status.StatusStockForm;
import vn.conwood.common.type.TypePromotion;
import vn.conwood.jpa.entity.form.StockFormEntity;
import vn.conwood.jpa.repository.StockFormRepository;

@Service
public class StockFormService {

    @Autowired
    private StockFormRepository stockFormRepository;

    public StockFormEntity create(StockFormEntity stockFormEntity) {
        stockFormEntity.setStatus(StatusStockForm.INIT);
        stockFormEntity.setType(TypePromotion.STOCK_PROMOTION_TYPE);
        stockFormEntity = stockFormRepository.saveAndFlush(stockFormEntity);
        return stockFormEntity;
    }

}
