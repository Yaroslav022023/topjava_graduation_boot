package com.topjava.graduation.web.meal;

import com.topjava.graduation.exception.ErrorType;
import com.topjava.graduation.exception.NotFoundException;
import com.topjava.graduation.model.Meal;
import com.topjava.graduation.service.MealService;
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
import static com.topjava.graduation.web.meal.MealTestData.*;
import static com.topjava.graduation.web.restaurant.RestaurantTestData.ITALIAN_ID;
import static com.topjava.graduation.web.user.UserTestData.ADMIN_MAIL;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminMealRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL =
            AdminMealRestController.REST_URL.replace("{restaurantId}", Integer.toString(ITALIAN_ID));
    private static final String REST_URL_SLASH = REST_URL + '/';

    @Autowired
    private MealService service;

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_MATCHER.contentJson(italian_meals));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + italian_meal1.id()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_MATCHER.contentJson(italian_meal1));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + MealTestData.NOT_FOUND))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(titleMessage(ErrorType.NOT_FOUND.title));
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + italian_meal1.id()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createWithLocation() throws Exception {
        Meal newMeal = getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(newMeal)))
                .andExpect(status().isCreated())
                .andDo(print());

        Meal created = MEAL_MATCHER.readFromJson(action);
        int newId = created.id();
        newMeal.setId(newId);
        MEAL_MATCHER.assertMatch(created, newMeal);
        MEAL_MATCHER.assertMatch(service.get(newId, ITALIAN_ID), newMeal);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createInvalidName() throws Exception {
        Meal newMeal = getNew();
        newMeal.setName("");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(newMeal)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createInvalidPriceLessThanMin() throws Exception {
        Meal newMeal = getNew();
        newMeal.setPrice(4);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(newMeal)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createInvalidPriceMoreThanMax() throws Exception {
        Meal newMeal = getNew();
        newMeal.setPrice(2001);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(newMeal)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createHtmlUnsafe() throws Exception {
        Meal newMeal = getNew();
        newMeal.setName("<script>alert(123)</script>");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(newMeal)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @WithUserDetails(value = ADMIN_MAIL)
    void createDuplicateDateAndName() throws Exception {
        Meal newMeal = getNew();
        newMeal.setDate(italian_meal1.getDate());
        newMeal.setName(italian_meal1.getName());
        newMeal.setPrice(100);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(newMeal)))
                .andExpect(status().isConflict())
                .andDo(print())
                .andExpect(titleMessage(DATA_CONFLICT.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void update() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + italian_meal1.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(getUpdated())))
                .andExpect(status().isNoContent())
                .andDo(print());
        MEAL_MATCHER.assertMatch(service.get(italian_meal1.id(), ITALIAN_ID), getUpdated());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateInvalidName() throws Exception {
        Meal updated = getUpdated();
        updated.setName("");
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + italian_meal1.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateInvalidPriceLessThanMin() throws Exception {
        Meal updated = getUpdated();
        updated.setPrice(4);
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + italian_meal1.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateInvalidPriceMoreThanMax() throws Exception {
        Meal updated = getUpdated();
        updated.setPrice(2001);
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + italian_meal1.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateHtmlUnsafe() throws Exception {
        Meal updated = getUpdated();
        updated.setName("<script>alert(123)</script>");
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + italian_meal1.id())
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
        Meal updated = new Meal(null, italian_meal1.getDate(), italian_meal1.getName(), 100);
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + italian_meal2.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updated)))
                .andExpect(status().isConflict())
                .andDo(print())
                .andExpect(titleMessage(DATA_CONFLICT.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL_SLASH + italian_meal1.id()))
                .andExpect(status().isNoContent())
                .andDo(print());
        assertThrows(NotFoundException.class, () -> service.get(italian_meal1.id(), ITALIAN_ID));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL_SLASH + MealTestData.NOT_FOUND))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(titleMessage(ErrorType.NOT_FOUND.title));
    }
}