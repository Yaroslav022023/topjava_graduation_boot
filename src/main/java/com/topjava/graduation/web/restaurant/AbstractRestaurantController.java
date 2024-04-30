package com.topjava.graduation.web.restaurant;

import com.topjava.graduation.dto.RestaurantWithDishesViewDto;
import com.topjava.graduation.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AbstractRestaurantController {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    protected RestaurantService service;

    public List<RestaurantWithDishesViewDto> getAllWithTodayDishes() {
        log.info("getAllWithTodayDishes");
        return service.getAllWithTodayDishes();
    }
}