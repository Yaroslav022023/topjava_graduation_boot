package com.topjava.graduation.web.restaurant;

import com.topjava.graduation.dto.RestaurantWithDishesViewDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = UserRestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserRestaurantController extends AbstractRestaurantController {
    static final String REST_URL = "/api/restaurants";

    @Override
    @GetMapping("/menu_today")
    public List<RestaurantWithDishesViewDto> getAllWithTodayDishes() {
        return super.getAllWithTodayDishes();
    }
}