package com.topjava.graduation.util;

import com.topjava.graduation.dto.RestaurantViewDto;
import com.topjava.graduation.dto.RestaurantVotedByUserDto;
import com.topjava.graduation.model.Restaurant;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class RestaurantUtil {

    public static List<RestaurantViewDto> convertToViewDtos(List<Restaurant> restaurants) {
        return restaurants.stream()
                .map(r -> new RestaurantViewDto(r.getId(), r.getName(), DishesUtil.getTos(r.getDishes())))
                .toList();
    }

    public static RestaurantVotedByUserDto convertToVotedByUserDto(Restaurant restaurant) {
        return new RestaurantVotedByUserDto(restaurant.getId(), restaurant.getName());
    }
}