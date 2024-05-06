package com.topjava.graduation.web.restaurant;

import com.topjava.graduation.dto.RestaurantViewDto;
import com.topjava.graduation.dto.RestaurantWithDishesViewDto;
import com.topjava.graduation.web.AuthUser;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = UserRestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserRestaurantController extends AbstractRestaurantController {
    static final String REST_URL = "/api/restaurants";

    @Override
    @GetMapping
    public List<RestaurantWithDishesViewDto> getAllWithTodayDishes() {
        return super.getAllWithTodayDishes();
    }

    @GetMapping("/voted-by-user")
    public ResponseEntity<RestaurantViewDto> getVotedByUser(@AuthenticationPrincipal AuthUser authUser) {
        log.info("getVotedByUser {}", authUser.id());
        RestaurantViewDto result = service.getVotedByUserForToday(authUser.id());
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }
}