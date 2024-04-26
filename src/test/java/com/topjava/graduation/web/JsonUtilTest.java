package com.topjava.graduation.web;

import com.topjava.graduation.model.Dish;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.topjava.graduation.util.JsonUtil.*;
import static com.topjava.graduation.web.dish.DishTestData.*;

@SpringBootTest
class JsonUtilTest {
    private static final Logger log = LoggerFactory.getLogger(JsonUtilTest.class);

    @Test
    void readWriteValue() {
        String json = writeValue(italian_dish_1);
        log.info(json);
        Dish dish = readValue(json, Dish.class);
        DISH_MATCHER.assertMatch(dish, italian_dish_1);
    }

    @Test
    void readWriteValues() {
        String json = writeValue(ITALIAN_DISHES);
        log.info(json);
        List<Dish> actual = readValues(json, Dish.class);
        DISH_MATCHER.assertMatch(actual, ITALIAN_DISHES);
    }
}