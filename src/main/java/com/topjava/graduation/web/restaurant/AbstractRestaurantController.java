package com.topjava.graduation.web.restaurant;

import com.topjava.graduation.dto.RestaurantWithDishesViewDto;
import com.topjava.graduation.service.RestaurantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class AbstractRestaurantController {
    @Autowired
    protected RestaurantService service;

    public List<RestaurantWithDishesViewDto> getAllWithTodayDishes() {
        log.info("getAllWithTodayDishes");
        return service.getAllWithTodayDishes();
    }
}