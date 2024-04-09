package com.topjava.graduation.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class MealViewDto extends NamedDto {
    long price;

    public MealViewDto(Integer id, String name, long price) {
        super(id, name);
        this.price = price;
    }
}