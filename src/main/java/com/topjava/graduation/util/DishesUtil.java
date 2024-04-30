package com.topjava.graduation.util;

import com.topjava.graduation.dto.DishViewDto;
import com.topjava.graduation.model.Dish;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class DishesUtil {

    public static List<DishViewDto> asViewDtos(Set<Dish> dishes) {
        return dishes.stream()
                .map(m -> new DishViewDto(m.getId(), m.getName(), m.getPrice()))
                .collect(Collectors.toList());
    }
}