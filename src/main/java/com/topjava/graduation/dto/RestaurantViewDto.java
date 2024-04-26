package com.topjava.graduation.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.Set;

@Value
@EqualsAndHashCode(callSuper = true)
public class RestaurantViewDto extends NamedDto {
    Set<DishViewDto> dishes;

    public RestaurantViewDto(Integer id, String name, Set<DishViewDto> dishes) {
        super(id, name);
        this.dishes = dishes;
    }
}