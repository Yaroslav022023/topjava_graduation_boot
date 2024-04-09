package com.topjava.graduation.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class RestaurantVotedByUserDto extends NamedDto {
    public RestaurantVotedByUserDto(Integer id, String name) {
        super(id, name);
    }
}