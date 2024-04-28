package com.topjava.graduation.web.restaurant;

import com.topjava.graduation.dto.RestaurantViewDto;
import com.topjava.graduation.dto.RestaurantVotedByUserDto;
import com.topjava.graduation.dto.RestaurantWithNumberVoicesDto;
import com.topjava.graduation.web.AuthUser;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = UserRestaurantRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserRestaurantRestController extends AbstractRestaurantController {
    static final String REST_URL = "/api/restaurants";

    @Override
    @GetMapping
    public List<RestaurantViewDto> getAllWithTodayDishes() {
        return super.getAllWithTodayDishes();
    }

    @Override
    @GetMapping("/number-voices")
    public List<RestaurantWithNumberVoicesDto> getAllWithTodayNumberVoices() {
        return super.getAllWithTodayNumberVoices();
    }

    @GetMapping("/voted-by-user")
    public ResponseEntity<RestaurantVotedByUserDto> getVotedByUser(@AuthenticationPrincipal AuthUser authUser) {
        RestaurantVotedByUserDto result = super.getVotedByUser(authUser.id());
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }
}