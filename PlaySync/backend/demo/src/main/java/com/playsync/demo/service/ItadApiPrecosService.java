package com.playsync.demo.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.playsync.demo.Entities.ItadMainClass;
import com.playsync.demo.client.PriceClientItad;
import com.playsync.demo.dtoresponse.ItadMainClassDto;
import com.playsync.demo.repository.ItadMainClassRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItadApiPrecosService {

    private final ItadMainClassRepository itadMainClassRepository;

    private final PriceClientItad priceClientItad;

    public List<ItadMainClassDto> principalMethod(List<String> ids) {
        List<ItadMainClass> itadMainClassDtos = this.itadMainClassRepository.findByIds(ids);
        if (itadMainClassDtos.isEmpty()) {
            // chama api
        }
        // validarInformacao
    }

    private List<ItadMainClassDto> callApi(List<String> ids) {
        return this.priceClientItad.buscarPrecos(ids).block();
    }

    public void validaInfosNoBanco(List<ItadMainClassDto> itadMainClassDtos) {
        List<String> ids = new ArrayList<>();
        for (ItadMainClassDto itadMainClassDto : itadMainClassDtos) {
            ids.add(itadMainClassDto.getIdGame());
        }
        List<ItadMainClass> entidadesNoBanco = this.itadMainClassRepository.findByIds(ids);
        if (entidadesNoBanco.isEmpty()) {
            validaSeInfoPassouDoPrazo(entidadesNoBanco);
        }

    }

    private void validaSeInfoPassouDoPrazo(List<ItadMainClass> entidades) {
        List<ItadMainClass> entidadesAtrasadas = new ArrayList<>();
        LocalDateTime dataLimite = LocalDateTime.now().minusSeconds(10);

        for (ItadMainClass itadMainClass : entidades) {
            if (itadMainClass.getDataLastSearch().isBefore(dataLimite)) {
                entidadesAtrasadas.add(itadMainClass);
            }
        }

    }

    private void atualizaInformacoesAtrasadas(List<ItadMainClass> entidadesAtradas) {
        
    }

}
