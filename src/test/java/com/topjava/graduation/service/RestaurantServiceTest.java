package com.topjava.graduation.service;

import com.topjava.graduation.exception.NotFoundException;
import com.topjava.graduation.exception.VotingRestrictionsException;
import com.topjava.graduation.model.Restaurant;
import com.topjava.graduation.web.user.UserTestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

import static com.topjava.graduation.util.RestaurantUtil.convertToViewDtos;
import static com.topjava.graduation.util.RestaurantUtil.convertToVotedByUserDto;
import static com.topjava.graduation.web.meal.MealTestData.NOT_FOUND;
import static com.topjava.graduation.web.restaurant.RestaurantTestData.*;
import static com.topjava.graduation.web.user.UserTestData.ADMIN_ID;
import static com.topjava.graduation.web.user.UserTestData.USER_1_ID;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RestaurantServiceTest extends AbstractServiceTest {

    @Autowired
    protected RestaurantService service;

    @Test
    void getAll() {
        List<Restaurant> actual = service.getAll();
        actual.sort(Comparator.comparingInt(Restaurant::getId));
        RESTAURANT_MATCHER.assertMatch(actual, restaurantsSort);
    }

    @Test
    void get() {
        RESTAURANT_MATCHER.assertMatch(service.get(ITALIAN_ID), italian);
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
        RESTAURANT_MATCHER.assertMatch(service.get(newId), newRestaurant);
    }

    @Test
    void update() {
        Restaurant updated = getUpdated();
        service.save(updated);
        RESTAURANT_MATCHER.assertMatch(service.get(ITALIAN_ID), getUpdated());
    }

    @Test
    void updateNotFound() {
        Restaurant notFound = new Restaurant(NOT_FOUND, "Not Exist");
        assertThrows(NotFoundException.class, () -> service.save(notFound));
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
    void getAllWithMealsForToday() {
        RESTAURANT_VIEW_DTO_MATCHER.assertMatch(service.getAllWithMealsForToday(), convertToViewDtos(restaurantsSort));
    }

    @Test
    void getAllWithNumberVoicesForToday() {
        RESTAURANT_WITH_NUMBER_VOICES_DTO_MATCHER.assertMatch(
                service.getAllWithNumberVoicesForToday(), restaurantsWithNumberVoices);
    }

    @Test
    void vote() {
        assertThrows(NotFoundException.class, () -> service.getVotedByUserForToday(ADMIN_ID));

        service.vote(ADMIN_ID, FRENCH_ID);

        RESTAURANT_VOTED_BY_USER_DTO_MATCHER.assertMatch(
                service.getVotedByUserForToday(ADMIN_ID), convertToVotedByUserDto(french));
        RESTAURANT_WITH_NUMBER_VOICES_DTO_MATCHER.assertMatch(
                service.getAllWithNumberVoicesForToday(), restaurantsWithNumberVoicesUpdated);
    }

    @Test
    void voteChange() {
        service.vote(USER_1_ID, FRENCH_ID);

        RESTAURANT_VOTED_BY_USER_DTO_MATCHER.assertMatch(
                service.getVotedByUserForToday(USER_1_ID), convertToVotedByUserDto(french));
        RESTAURANT_WITH_NUMBER_VOICES_DTO_MATCHER.assertMatch(
                service.getAllWithNumberVoicesForToday(), restaurantsWithNumberVoicesUpdated_2);
    }

    @Test
    void voteRestrictions() {
        assertThrows(VotingRestrictionsException.class, () -> service.vote(UserTestData.USER_3_ID, FRENCH_ID));
    }

    @Test
    void getVotedByUser() {
        RESTAURANT_VOTED_BY_USER_DTO_MATCHER.assertMatch(
                service.getVotedByUserForToday(USER_1_ID), convertToVotedByUserDto(italian));
    }

    @Test
    void getVotedByUserNotFound() {
        assertThrows(NotFoundException.class, () -> service.getVotedByUserForToday(UserTestData.GUEST_ID));
    }
}