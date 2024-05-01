package com.topjava.graduation.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class RestaurantWithNumVoicesViewDto extends NamedDto {
    int voices;

    public RestaurantWithNumVoicesViewDto(Integer id, String name, long voices) {
        super(id, name);
        this.voices = (int) voices;
    }
}