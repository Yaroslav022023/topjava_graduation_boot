package com.topjava.graduation.service;

import com.topjava.graduation.exception.NotFoundException;
import com.topjava.graduation.model.Meal;
import com.topjava.graduation.web.meal.MealTestData;
import org.h2.jdbc.JdbcBatchUpdateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.topjava.graduation.TestUtil.validateRootCause;
import static com.topjava.graduation.web.meal.MealTestData.*;
import static com.topjava.graduation.web.restaurant.RestaurantTestData.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MealServiceTest extends AbstractServiceTest {

    @Autowired
    protected MealService service;

    @Test
    void getAll() {
        MealTestData.MEAL_MATCHER.assertMatch(service.getAll(ITALIAN_ID), italian_meals);
    }

    @Test
    void get() {
        Meal actual = service.get(asian_meal1.id(), ASIAN_ID);
        MealTestData.MEAL_MATCHER.assertMatch(actual, asian_meal1);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, ITALIAN_ID));
    }

    @Test
    void getNotOwn() {
        assertThrows(NotFoundException.class, () -> service.get(asian_meal1.id(), ITALIAN_ID));
    }

    @Test
    void create() {
        Meal created = service.save(MealTestData.getNew(), FRENCH_ID);
        int newId = created.id();
        Meal newMeal = MealTestData.getNew();
        newMeal.setId(newId);
        MealTestData.MEAL_MATCHER.assertMatch(created, newMeal);
        MealTestData.MEAL_MATCHER.assertMatch(service.get(newId, FRENCH_ID), newMeal);
    }

    @Test
    void update() {
        Meal updated = MealTestData.getUpdated();
        service.save(updated, ITALIAN_ID);
        MealTestData.MEAL_MATCHER.assertMatch(service.get(italian_meal1.id(), ITALIAN_ID), MealTestData.getUpdated());
    }

    @Test
    void updateNotOwn() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.save(MealTestData.getUpdated(), ASIAN_ID));
        Assertions.assertEquals("Meal id=" + italian_meal1.id() +
                " is not exist or doesn't belong to Restaurant id=" + ASIAN_ID, exception.getMessage());
        MealTestData.MEAL_MATCHER.assertMatch(service.get(italian_meal1.id(), ITALIAN_ID), italian_meal1);
    }

    @Test
    void updateToDuplicate() {
        Meal updated = service.get(asian_meal1.id(), ASIAN_ID);
        updated.setName(asian_meal2.getName());
        validateRootCause(JdbcBatchUpdateException.class, () -> service.save(updated, ASIAN_ID));
    }

    @Test
    void delete() {
        service.delete(asian_meal1.id(), ASIAN_ID);
        assertThrows(NotFoundException.class, () -> service.get(asian_meal1.id(), ASIAN_ID));
    }

    @Test
    void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND, ASIAN_ID));
    }

    @Test
    void deleteNotOwn() {
        assertThrows(NotFoundException.class, () -> service.delete(italian_meal1.id(), FRENCH_ID));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void addDuplicateMealIntoRestaurantForToday() {
        assertThrows(DataIntegrityViolationException.class, () ->
                service.save(new Meal(
                                null,
                                asian_meal1.getDate(),
                                asian_meal1.getName(),
                                asian_meal1.getPrice()),
                        ASIAN_ID));
    }
}