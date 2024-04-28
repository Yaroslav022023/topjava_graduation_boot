package com.topjava.graduation.util;

import com.topjava.graduation.dto.RestaurantWithDishesViewDto;
import com.topjava.graduation.dto.RestaurantViewDto;
import com.topjava.graduation.model.Restaurant;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class RestaurantUtil {

    public static List<RestaurantWithDishesViewDto> asWithDishesViewDtos(List<Restaurant> restaurants) {
        return restaurants.stream()
                .map(r -> new RestaurantWithDishesViewDto(r.getId(), r.getName(), DishesUtil.asViewDtos(r.getDishes())))
                .toList();
    }

    public static RestaurantViewDto asViewDto(Restaurant restaurant) {
        return new RestaurantViewDto(restaurant.getId(), restaurant.getName());
    }

    public static List<RestaurantViewDto> asViewDtos(List<Restaurant> restaurants) {
        return restaurants.stream()
                .map(r -> new RestaurantViewDto(r.getId(), r.getName()))
                .toList();
    }
}