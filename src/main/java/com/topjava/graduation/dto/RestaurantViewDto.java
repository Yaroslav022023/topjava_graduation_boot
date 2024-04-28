package com.topjava.graduation.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class RestaurantViewDto extends NamedDto {
    public RestaurantViewDto(Integer id, String name) {
        super(id, name);
    }
}