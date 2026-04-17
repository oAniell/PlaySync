package com.playsync.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import com.playsync.demo.Entities.ItadDeals;
import com.playsync.demo.Entities.ItadDrm;
import com.playsync.demo.Entities.ItadMainClass;
import com.playsync.demo.Entities.ItadPlataforms;
import com.playsync.demo.Entities.ItadPrice;
import com.playsync.demo.Entities.ItadRegular;
import com.playsync.demo.Entities.ItadShop;
import com.playsync.demo.client.PriceClientItad;
import com.playsync.demo.dtoresponse.DrmItadResponse;
import com.playsync.demo.dtoresponse.ItadDealsDto;
import com.playsync.demo.dtoresponse.ItadMainClassDto;
import com.playsync.demo.dtoresponse.ItadPlataformsDto;
import com.playsync.demo.repository.ItadMainClassRepository;

import jakarta.persistence.OneToMany;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItadApiPrecosService {

    private final ItadMainClassRepository itadMainClassRepository;

    private final PriceClientItad priceClientItad;

    public List<ItadMainClassDto> principalMethod(List<String> ids) {
        List<ItadMainClass> itadMainClass = this.itadMainClassRepository.findByIds(ids);

        if (itadMainClass.isEmpty()) {
            persistDataOfApiInDatabase(callApi(ids));
        }
    }

    public List<ItadMainClassDto> callApi(List<String> ids) {
        return this.priceClientItad.buscarPrecos(ids).block();
    }

    private void persistDataOfApiInDatabase(List<ItadMainClassDto> itadMainClassDtos) {
        List<ItadMainClass> itadMainClasses = new ArrayList<>();
        for (ItadMainClassDto itadMainClassDto : itadMainClassDtos) {
            ItadMainClass itadMainClass = new ItadMainClass(itadMainClassDto.getIdGame(), LocalDateTime.now());
            for (ItadDealsDto itadDealsDto : itadMainClassDto.getDeals()) {
                ItadDeals itadDeals = new ItadDeals(null, itadDealsDto.getCut());
                validaExistenciaDeObjetoNoDTOeInsereNoBanco(itadDealsDto, itadDeals);
                if (!itadDealsDto.getDrm().isEmpty()) {
                    insereInformacaoDrm(itadDeals, itadDealsDto);
                }
                if (!itadDealsDto.getPlataformas().isEmpty()) {
                    insereInformacaoPlataforms(itadDeals, itadDealsDto);
                }
                itadDeals.setItadMainClass(itadMainClass);
                itadMainClass.getItad_deals().add(itadDeals);

            }
            itadMainClasses.add(itadMainClass);
        }
        this.itadMainClassRepository.saveAll(itadMainClasses);
    }

    private void insereInformacaoDrm(ItadDeals itadDeals, ItadDealsDto itadDealsDto) {
        for (DrmItadResponse drmItadResponse : itadDealsDto.getDrm()) {
            ItadDrm itadDrm = new ItadDrm(drmItadResponse.getId(), drmItadResponse.getName(), null);
            itadDrm.setItadDeals(itadDeals);
            itadDeals.getItadDrms().add(itadDrm);
        }
    }

    private void insereInformacaoPlataforms(ItadDeals itadDeals, ItadDealsDto itadDealsDto) {
        for (ItadPlataformsDto itadPlataformsDto : itadDealsDto.getPlataformas()) {
            ItadPlataforms itadPlataforms = new ItadPlataforms(itadPlataformsDto.getId(),
                    itadPlataformsDto.getName(), null);
            itadPlataforms.setItadDeals(itadDeals);
            itadDeals.getItadPlataforms().add(itadPlataforms);
        }
    }

    private void validaExistenciaDeObjetoNoDTOeInsereNoBanco(ItadDealsDto itadDealsDto, ItadDeals itadDeals) {
        if (itadDealsDto.getPrice() != null) {
            ItadPrice itadPrice = new ItadPrice(itadDealsDto.getPrice().getAmount(), null);
            itadPrice.setItadDeals(itadDeals);
            itadDeals.getItadPrices().add(itadPrice);
        }
        if (itadDealsDto.getShop() != null) {
            ItadShop itadShop = new ItadShop(itadDealsDto.getShop().getId(), itadDealsDto.getShop().getName(),
                    null);
            itadShop.setItadDeals(itadDeals);
            itadDeals.getItadShops().add(itadShop);
        }
        if (itadDealsDto.getRegular() != null) {
            ItadRegular itadRegular = new ItadRegular(itadDealsDto.getRegular().getAmount(), null);
            itadRegular.setItadDeals(itadDeals);
            itadDeals.getItadRegulars().add(itadRegular);
        }
    }
}
