package vn.conwood.client.controller.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.conwood.jpa.entity.form.StockFormEntity;
import vn.conwood.client.controller.form.StockForm;
import vn.conwood.client.mapper.Mapper;

@Component
public class StockFormConverter {
    private static final Logger LOGGER = LogManager.getLogger(StockFormConverter.class);
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Mapper mapper;

    public StockFormEntity convert2Entity(StockForm stockForm) {
        StockFormEntity stockFormEntity = mapper.map(stockForm, StockFormEntity.class);
        stockFormEntity.setJsonImage(stockForm.getDetail().toString());
        return stockFormEntity;
    }
}
