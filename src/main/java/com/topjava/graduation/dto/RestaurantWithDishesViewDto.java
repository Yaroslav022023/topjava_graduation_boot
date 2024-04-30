package com.topjava.graduation.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
public class RestaurantWithDishesViewDto extends NamedDto {
    List<DishViewDto> dishes;

    public RestaurantWithDishesViewDto(Integer id, String name, List<DishViewDto> dishes) {
        super(id, name);
        this.dishes = dishes;
    }
}