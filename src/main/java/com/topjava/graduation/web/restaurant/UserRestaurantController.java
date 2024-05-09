package com.topjava.graduation.web.restaurant;

import com.topjava.graduation.dto.RestaurantWithDishesViewDto;
import com.topjava.graduation.service.RestaurantService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(value = UserRestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserRestaurantController {
    static final String REST_URL = "/api/restaurants";
    protected RestaurantService service;

    @GetMapping("/menu_today")
    public List<RestaurantWithDishesViewDto> getAllWithTodayDishes() {
        log.info("getAllWithTodayDishes");
        return service.getAllWithTodayDishes();
    }
}