package com.api.hotifi.payment.services.implementations;

import com.api.hotifi.payment.services.interfaces.IStatsService;
import com.api.hotifi.payment.web.responses.BuyerStatsResponse;
import com.api.hotifi.payment.web.responses.SellerStatsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class StatsServiceImpl implements IStatsService {

    @Transactional(readOnly = true)
    @Override
    public BuyerStatsResponse getBuyerStats(Long buyerId)   {
        try{

        }catch (Exception e){
            log.error("Error Occurred", e);
        }
        return null;
    }

    @Transactional(readOnly = true)
    @Override
    public SellerStatsResponse getSellerStats(Long sellerId) {
        try{

        }catch (Exception e){
            log.error("Error Occurred", e);
        }
        return null;
    }
}
