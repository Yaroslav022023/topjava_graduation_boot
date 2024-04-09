package com.topjava.graduation.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.Set;

@Value
@EqualsAndHashCode(callSuper = true)
public class RestaurantViewDto extends NamedDto {
    Set<MealViewDto> meals;

    public RestaurantViewDto(Integer id, String name, Set<MealViewDto> meals) {
        super(id, name);
        this.meals = meals;
    }
}