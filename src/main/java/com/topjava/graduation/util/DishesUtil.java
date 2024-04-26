package com.topjava.graduation.util;

import com.topjava.graduation.dto.DishViewDto;
import com.topjava.graduation.model.Dish;
import lombok.experimental.UtilityClass;

import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class DishesUtil {

    public static Set<DishViewDto> getTos(Set<Dish> dishes) {
        return dishes.stream()
                .map(m -> new DishViewDto(m.getId(), m.getName(), m.getPrice()))
                .collect(Collectors.toSet());
    }
}