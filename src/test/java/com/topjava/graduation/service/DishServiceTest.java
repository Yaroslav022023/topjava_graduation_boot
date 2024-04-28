package com.topjava.graduation.service;

import com.topjava.graduation.exception.NotFoundException;
import com.topjava.graduation.model.Dish;
import com.topjava.graduation.web.dish.DishTestData;
import org.h2.jdbc.JdbcBatchUpdateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.topjava.graduation.TestUtil.validateRootCause;
import static com.topjava.graduation.web.dish.DishTestData.*;
import static com.topjava.graduation.web.restaurant.RestaurantTestData.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DishServiceTest extends AbstractServiceTest {

    @Autowired
    private DishService service;

    @Test
    void getAll() {
        DishTestData.DISH_MATCHER.assertMatch(service.getAll(ITALIAN_ID), ITALIAN_DISHES);
    }

    @Test
    void get() {
        Dish actual = service.get(asian_dish_1.id(), ASIAN_ID);
        DishTestData.DISH_MATCHER.assertMatch(actual, asian_dish_1);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, ITALIAN_ID));
    }

    @Test
    void getNotOwn() {
        assertThrows(NotFoundException.class, () -> service.get(asian_dish_1.id(), ITALIAN_ID));
    }

    @Test
    void create() {
        Dish created = service.save(DishTestData.getNew(), FRENCH_ID);
        int newId = created.id();
        Dish newDish = DishTestData.getNew();
        newDish.setId(newId);
        DishTestData.DISH_MATCHER.assertMatch(created, newDish);
        DishTestData.DISH_MATCHER.assertMatch(service.get(newId, FRENCH_ID), newDish);
    }

    @Test
    void update() {
        Dish updated = DishTestData.getUpdated();
        service.save(updated, ITALIAN_ID);
        DishTestData.DISH_MATCHER.assertMatch(service.get(italian_dish_1.id(), ITALIAN_ID), DishTestData.getUpdated());
    }

    @Test
    void updateNotOwn() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.save(DishTestData.getUpdated(), ASIAN_ID));
        Assertions.assertEquals("Dish id=" + italian_dish_1.id() +
                " is not exist or doesn't belong to Restaurant id=" + ASIAN_ID, exception.getMessage());
        DishTestData.DISH_MATCHER.assertMatch(service.get(italian_dish_1.id(), ITALIAN_ID), italian_dish_1);
    }

    @Test
    void updateToDuplicate() {
        Dish updated = service.get(asian_dish_1.id(), ASIAN_ID);
        updated.setName(asian_dish_2.getName());
        validateRootCause(JdbcBatchUpdateException.class, () -> service.save(updated, ASIAN_ID));
    }

    @Test
    void delete() {
        service.delete(asian_dish_1.id(), ASIAN_ID);
        assertThrows(NotFoundException.class, () -> service.get(asian_dish_1.id(), ASIAN_ID));
    }

    @Test
    void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND, ASIAN_ID));
    }

    @Test
    void deleteNotOwn() {
        assertThrows(NotFoundException.class, () -> service.delete(italian_dish_1.id(), FRENCH_ID));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void addDuplicateDishIntoRestaurantForToday() {
        assertThrows(DataIntegrityViolationException.class, () ->
                service.save(new Dish(
                                null,
                                asian_dish_1.getDate(),
                                asian_dish_1.getName(),
                                asian_dish_1.getPrice()),
                        ASIAN_ID));
    }
}