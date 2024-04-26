package com.topjava.graduation.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class DishViewDto extends NamedDto {
    long price;

    public DishViewDto(Integer id, String name, long price) {
        super(id, name);
        this.price = price;
    }
}