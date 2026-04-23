package com.playsync.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.playsync.demo.Entities.ItadDeals;
import com.playsync.demo.Entities.ItadDrm;
import com.playsync.demo.Entities.ItadMainClass;
import com.playsync.demo.Entities.ItadPlataforms;
import com.playsync.demo.client.PriceClientItad;
import com.playsync.demo.dtoresponse.DrmItadResponse;
import com.playsync.demo.dtoresponse.ItadDealsDto;
import com.playsync.demo.dtoresponse.ItadMainClassDto;
import com.playsync.demo.dtoresponse.ItadPlataformsDto;
import com.playsync.demo.dtoresponse.ItadPriceDto;
import com.playsync.demo.dtoresponse.ItadRegularDto;
import com.playsync.demo.dtoresponse.ItadShopDto;
import com.playsync.demo.repository.ItadMainClassRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItadApiPrecosService {

    private final ItadMainClassRepository itadMainClassRepository;
    private final PriceClientItad priceClientItad;

    public List<ItadMainClassDto> principalMethod(List<String> ids) {
        List<ItadMainClass> itadMainClass = this.itadMainClassRepository.findByIds(ids);

        if (itadMainClass == null || itadMainClass.isEmpty()) {
            return persistDataOfApiInDatabase(callApi(ids), ids);
        }

        return validDataInDatabase(itadMainClass, ids);
    }

    public List<ItadMainClassDto> callApi(List<String> ids) {
        return this.priceClientItad.buscarPrecos(ids).block();
    }

    private List<ItadMainClassDto> validDataInDatabase(List<ItadMainClass> listaEntidadeNoBanco, List<String> ids) {

        List<ItadMainClass> listaDeVencidos = new ArrayList<>();
        LocalDateTime dataLimite = LocalDateTime.now().minusSeconds(10);

        for (ItadMainClass itadMainClass : listaEntidadeNoBanco) {
            if (itadMainClass.getDataLastSearch() != null &&
                    itadMainClass.getDataLastSearch().isBefore(dataLimite)) {
                listaDeVencidos.add(itadMainClass);
            }
        }

        if (listaDeVencidos.isEmpty()) {
            return montaDto(listaEntidadeNoBanco);
        }

        return atualizaInformacaoVencida(listaDeVencidos, ids);
    }

    private List<ItadMainClassDto> persistDataOfApiInDatabase(List<ItadMainClassDto> itadMainClassDtos,
            List<String> ids) {

        List<ItadMainClass> existentes = itadMainClassRepository.findByIds(ids);

        Map<String, ItadMainClass> mapaExistentes = new HashMap<>();
        if (existentes != null) {
            for (ItadMainClass existente : existentes) {
                mapaExistentes.put(existente.getIdGame(), existente);
            }
        }

        List<ItadMainClass> itadMainClasses = new ArrayList<>();

        for (ItadMainClassDto dto : itadMainClassDtos) {

            ItadMainClass entity = mapaExistentes.get(dto.getIdGame());

            if (entity == null) {
                entity = new ItadMainClass(dto.getIdGame(), LocalDateTime.now());
            } else {
                entity.setDataLastSearch(LocalDateTime.now());
                if (entity.getItad_deals() != null) {
                    entity.getItad_deals().clear();
                }
            }

            if (dto.getDeals() != null) {
                for (ItadDealsDto dealsDto : dto.getDeals()) {

                    ItadDeals deal = new ItadDeals(null, null, null, null, null);

                    auxUpdateInformations(dealsDto, deal);

                    List<DrmItadResponse> drmList = dealsDto.getDrm() == null ? new ArrayList<>() : dealsDto.getDrm();
                    List<ItadPlataformsDto> platformList = dealsDto.getPlataformas() == null ? new ArrayList<>()
                            : dealsDto.getPlataformas();

                    insereInformacaoDrm(deal, drmList);
                    insereInformacaoPlataforms(deal, platformList);

                    deal.setItadMainClass(entity);

                    if (entity.getItad_deals() == null) {
                        entity.setItad_deals(new ArrayList<>());
                    }

                    entity.getItad_deals().add(deal);
                }
            }

            itadMainClasses.add(entity);
        }

        this.itadMainClassRepository.saveAll(itadMainClasses);
        return montaDto(itadMainClasses);
    }

    private List<ItadMainClassDto> atualizaInformacaoVencida(List<ItadMainClass> listaDeVencidos, List<String> ids) {

        List<ItadMainClassDto> callApi = callApi(ids);

        Map<String, ItadMainClassDto> mapperEntity = new HashMap<>();
        for (ItadMainClassDto dto : callApi) {
            mapperEntity.put(dto.getIdGame(), dto);
        }

        for (ItadMainClass entity : listaDeVencidos) {

            ItadMainClassDto dto = mapperEntity.get(entity.getIdGame());

            entity.setDataLastSearch(LocalDateTime.now());

            if (dto == null)
                continue;

            if (entity.getItad_deals() != null) {
                entity.getItad_deals().clear();
            }

            if (dto.getDeals() != null) {
                for (ItadDealsDto dealsDto : dto.getDeals()) {

                    ItadDeals deal = new ItadDeals(null, null, null, null, null);

                    auxUpdateInformations(dealsDto, deal);

                    List<DrmItadResponse> drmList = dealsDto.getDrm() == null ? new ArrayList<>() : dealsDto.getDrm();
                    List<ItadPlataformsDto> platformList = dealsDto.getPlataformas() == null ? new ArrayList<>()
                            : dealsDto.getPlataformas();

                    insereInformacaoDrm(deal, drmList);
                    insereInformacaoPlataforms(deal, platformList);

                    deal.setItadMainClass(entity);

                    if (entity.getItad_deals() == null) {
                        entity.setItad_deals(new ArrayList<>());
                    }

                    entity.getItad_deals().add(deal);
                }
            }
        }

        this.itadMainClassRepository.saveAll(listaDeVencidos);
        return montaDto(listaDeVencidos);
    }

    private void auxUpdateInformations(ItadDealsDto dto, ItadDeals deal) {
        if (dto.getPrice() != null && dto.getPrice().getAmount() != null) {
            deal.setPrice(dto.getPrice().getAmount());
        }
        if (dto.getRegular() != null && dto.getRegular().getAmount() != null) {
            deal.setRegular(dto.getRegular().getAmount());
        }
        if (dto.getShop() != null && dto.getShop().getId() != null) {
            deal.setShopId(dto.getShop().getId());
            if (dto.getShop().getName() != null) {
                deal.setShopName(dto.getShop().getName());
            }
        }
        if (dto.getCut() != null) {
            deal.setDesconto(dto.getCut());
        }
    }

    private void insereInformacaoDrm(ItadDeals deal, List<DrmItadResponse> drmList) {

        if (deal.getDrms() == null) {
            deal.setDrms(new ArrayList<>());
        }

        for (DrmItadResponse drm : drmList) {
            ItadDrm entity = new ItadDrm(drm.getId(), drm.getName(), null);
            entity.setItadDeals(deal);
            deal.getDrms().add(entity);
        }
    }

    private void insereInformacaoPlataforms(ItadDeals deal, List<ItadPlataformsDto> listDto) {

        if (deal.getPlatforms() == null) {
            deal.setPlatforms(new ArrayList<>());
        }

        for (ItadPlataformsDto p : listDto) {
            ItadPlataforms entity = new ItadPlataforms(p.getId(), p.getName(), null);
            entity.setItadDeals(deal);
            deal.getPlatforms().add(entity);
        }
    }

    private List<ItadMainClassDto> montaDto(List<ItadMainClass> entidades) {

        List<ItadMainClassDto> dtos = new ArrayList<>();

        for (ItadMainClass entity : entidades) {

            ItadMainClassDto dto = new ItadMainClassDto(entity.getIdGame(), null);
            List<ItadDealsDto> dealsDtos = new ArrayList<>();

            if (entity.getItad_deals() != null) {
                for (ItadDeals deal : entity.getItad_deals()) {
                    ItadDealsDto d = new ItadDealsDto();
                    auxMontaDto(deal, d);
                    dealsDtos.add(d);
                }
            }

            dto.setDeals(dealsDtos);
            dtos.add(dto);
        }

        return dtos;
    }

    private void auxMontaDto(ItadDeals deal, ItadDealsDto dto) {

        if (deal.getDesconto() != null) {
            dto.setCut(deal.getDesconto());
        }

        if (deal.getShopId() != null || deal.getShopName() != null) {
            ItadShopDto shop = new ItadShopDto();
            shop.setId(deal.getShopId());
            shop.setName(deal.getShopName());
            dto.setShop(shop);
        }

        if (deal.getPrice() != null) {
            ItadPriceDto price = new ItadPriceDto();
            price.setAmount(deal.getPrice());
            dto.setPrice(price);
        }

        if (deal.getRegular() != null) {
            ItadRegularDto regular = new ItadRegularDto();
            regular.setAmount(deal.getRegular());
            dto.setRegular(regular);
        }

        validaInformacaoDrmParaRetornoDto(deal, dto);
        validaInformacaoPlataforms(deal, dto);
    }

    private void validaInformacaoDrmParaRetornoDto(ItadDeals deal, ItadDealsDto dto) {

        if (deal.getDrms() != null) {
            List<DrmItadResponse> list = new ArrayList<>();

            for (ItadDrm drm : deal.getDrms()) {
                DrmItadResponse d = new DrmItadResponse(null, null);
                d.setId(drm.getIdDrm());
                d.setName(drm.getNome());
                list.add(d);
            }

            dto.setDrm(list);
        }
    }

    private void validaInformacaoPlataforms(ItadDeals deal, ItadDealsDto dto) {

        if (deal.getPlatforms() != null) {
            List<ItadPlataformsDto> list = new ArrayList<>();

            for (ItadPlataforms p : deal.getPlatforms()) {
                ItadPlataformsDto d = new ItadPlataformsDto();
                d.setId(p.getIdPlataforma());
                d.setName(p.getNomePlataforma());
                list.add(d);
            }

            dto.setPlataformas(list);
        }
    }
}