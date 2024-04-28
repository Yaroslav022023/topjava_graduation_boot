package com.topjava.graduation.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.Set;

@Value
@EqualsAndHashCode(callSuper = true)
public class RestaurantWithDishesViewDto extends NamedDto {
    Set<DishViewDto> dishes;

    public RestaurantWithDishesViewDto(Integer id, String name, Set<DishViewDto> dishes) {
        super(id, name);
        this.dishes = dishes;
    }
}