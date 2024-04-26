package com.topjava.graduation.web.dish;

import com.topjava.graduation.exception.ErrorType;
import com.topjava.graduation.exception.NotFoundException;
import com.topjava.graduation.model.Dish;
import com.topjava.graduation.service.DishService;
import com.topjava.graduation.web.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.topjava.graduation.exception.ErrorType.BAD_DATA;
import static com.topjava.graduation.exception.ErrorType.DATA_CONFLICT;
import static com.topjava.graduation.util.JsonUtil.writeValue;
import static com.topjava.graduation.web.dish.DishTestData.*;
import static com.topjava.graduation.web.restaurant.RestaurantTestData.ITALIAN_ID;
import static com.topjava.graduation.web.user.UserTestData.ADMIN_MAIL;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminDishRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL =
            AdminDishRestController.REST_URL.replace("{restaurantId}", Integer.toString(ITALIAN_ID));
    private static final String REST_URL_SLASH = REST_URL + '/';

    @Autowired
    private DishService service;

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DISH_MATCHER.contentJson(ITALIAN_DISHES));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + italian_dish_1.id()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DISH_MATCHER.contentJson(italian_dish_1));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + DishTestData.NOT_FOUND))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(titleMessage(ErrorType.NOT_FOUND.title));
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + italian_dish_1.id()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createWithLocation() throws Exception {
        Dish newDish = getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(newDish)))
                .andExpect(status().isCreated())
                .andDo(print());

        Dish created = DISH_MATCHER.readFromJson(action);
        int newId = created.id();
        newDish.setId(newId);
        DISH_MATCHER.assertMatch(created, newDish);
        DISH_MATCHER.assertMatch(service.get(newId, ITALIAN_ID), newDish);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createInvalidName() throws Exception {
        Dish newDish = getNew();
        newDish.setName("");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(newDish)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createInvalidPriceLessThanMin() throws Exception {
        Dish newDish = getNew();
        newDish.setPrice(4);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(newDish)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createInvalidPriceMoreThanMax() throws Exception {
        Dish newDish = getNew();
        newDish.setPrice(2001);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(newDish)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createHtmlUnsafe() throws Exception {
        Dish newDish = getNew();
        newDish.setName("<script>alert(123)</script>");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(newDish)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @WithUserDetails(value = ADMIN_MAIL)
    void createDuplicateDateAndName() throws Exception {
        Dish newDish = getNew();
        newDish.setDate(italian_dish_1.getDate());
        newDish.setName(italian_dish_1.getName());
        newDish.setPrice(100);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(newDish)))
                .andExpect(status().isConflict())
                .andDo(print())
                .andExpect(titleMessage(DATA_CONFLICT.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void update() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + italian_dish_1.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(getUpdated())))
                .andExpect(status().isNoContent())
                .andDo(print());
        DISH_MATCHER.assertMatch(service.get(italian_dish_1.id(), ITALIAN_ID), getUpdated());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateInvalidName() throws Exception {
        Dish updated = getUpdated();
        updated.setName("");
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + italian_dish_1.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateInvalidPriceLessThanMin() throws Exception {
        Dish updated = getUpdated();
        updated.setPrice(4);
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + italian_dish_1.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateInvalidPriceMoreThanMax() throws Exception {
        Dish updated = getUpdated();
        updated.setPrice(2001);
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + italian_dish_1.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateHtmlUnsafe() throws Exception {
        Dish updated = getUpdated();
        updated.setName("<script>alert(123)</script>");
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + italian_dish_1.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @WithUserDetails(value = ADMIN_MAIL)
    void updateDuplicateDateAndName() throws Exception {
        Dish updated = new Dish(null, italian_dish_1.getDate(), italian_dish_1.getName(), 100);
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + italian_dish_2.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updated)))
                .andExpect(status().isConflict())
                .andDo(print())
                .andExpect(titleMessage(DATA_CONFLICT.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL_SLASH + italian_dish_1.id()))
                .andExpect(status().isNoContent())
                .andDo(print());
        assertThrows(NotFoundException.class, () -> service.get(italian_dish_1.id(), ITALIAN_ID));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL_SLASH + DishTestData.NOT_FOUND))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(titleMessage(ErrorType.NOT_FOUND.title));
    }
}