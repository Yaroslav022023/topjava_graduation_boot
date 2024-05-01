package com.topjava.graduation.service;

import com.topjava.graduation.dto.RestaurantViewDto;
import com.topjava.graduation.exception.NotFoundException;
import com.topjava.graduation.model.Restaurant;
import com.topjava.graduation.web.user.UserTestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.topjava.graduation.util.RestaurantUtil.*;
import static com.topjava.graduation.web.dish.DishTestData.NOT_FOUND;
import static com.topjava.graduation.web.restaurant.RestaurantTestData.*;
import static com.topjava.graduation.web.user.UserTestData.USER_1_ID;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RestaurantServiceTest extends AbstractServiceTest {

    @Autowired
    private RestaurantService service;

    @Test
    void getAll() {
        List<RestaurantViewDto> actual = new ArrayList<>(service.getAll());
        actual.sort(Comparator.comparingInt(RestaurantViewDto::getId));
        RESTAURANT_VIEW_DTO_MATCHER.assertMatch(actual, asViewDtos(restaurantsSort));
    }

    @Test
    void get() {
        RESTAURANT_VIEW_DTO_MATCHER.assertMatch(service.get(ITALIAN_ID), asViewDto(italian));
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND));
    }

    @Test
    void create() {
        Restaurant created = service.save(getNew());
        int newId = created.id();
        Restaurant newRestaurant = getNew();
        newRestaurant.setId(newId);
        RESTAURANT_MATCHER.assertMatch(created, newRestaurant);
        RESTAURANT_VIEW_DTO_MATCHER.assertMatch(service.get(newId), asViewDto(newRestaurant));
    }

    @Test
    void update() {
        Restaurant updated = getUpdated();
        service.save(updated);
        RESTAURANT_VIEW_DTO_MATCHER.assertMatch(service.get(ITALIAN_ID), asViewDto(getUpdated()));
    }

    @Test
    void delete() {
        service.delete(ASIAN_ID);
        assertThrows(NotFoundException.class, () -> service.get(ASIAN_ID));
    }

    @Test
    void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND));
    }

    @Test
    void getAllWithDishsForToday() {
        RESTAURANT_WITH_DISHES_VIEW_DTO_MATCHER.assertMatch(service.getAllWithTodayDishes(), asWithDishesViewDtos(restaurantsSort));
    }

    @Test
    void getAllWithNumberVoicesForToday() {
        RESTAURANT_WITH_NUMBER_VOICES_DTO_MATCHER.assertMatch(
                service.getAllWithTodayNumberVoices(), restaurantsWithNumberVoices);
    }

    @Test
    void getVotedByUser() {
        RESTAURANT_VIEW_DTO_MATCHER.assertMatch(
                service.getVotedByUserForToday(USER_1_ID), asViewDto(italian));
    }

    @Test
    void getNotVotedByUser() {
        assertNull(service.getVotedByUserForToday(UserTestData.GUEST_ID));
    }
}