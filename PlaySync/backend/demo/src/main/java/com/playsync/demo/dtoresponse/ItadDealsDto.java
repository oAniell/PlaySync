package com.playsync.demo.dtoresponse;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItadDealsDto {
    private ItadShopDto shop;
    private ItadPriceDto price;
    private ItadRegularDto regular;
    private Double cut;
    private List<DrmItadResponse> drm;
    @JsonProperty("platforms")
    private List<ItadPlataformsDto> plataformas;
    
    
}
