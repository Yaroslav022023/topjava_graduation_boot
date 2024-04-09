package com.topjava.graduation.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class RestaurantWithNumberVoicesDto extends NamedDto {
    int voices;

    public RestaurantWithNumberVoicesDto(Integer id, String name, long voices) {
        super(id, name);
        this.voices = (int) voices;
    }
}