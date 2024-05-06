package com.topjava.graduation.web.restaurant;

import com.topjava.graduation.exception.ErrorType;
import com.topjava.graduation.exception.NotFoundException;
import com.topjava.graduation.model.Restaurant;
import com.topjava.graduation.service.RestaurantService;
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
import static com.topjava.graduation.util.RestaurantUtil.asViewDto;
import static com.topjava.graduation.util.RestaurantUtil.asWithDishesViewDtos;
import static com.topjava.graduation.web.dish.DishTestData.NOT_FOUND;
import static com.topjava.graduation.web.restaurant.AdminRestaurantController.REST_URL;
import static com.topjava.graduation.web.restaurant.RestaurantTestData.*;
import static com.topjava.graduation.web.user.UserTestData.ADMIN_MAIL;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminRestaurantControllerTest extends AbstractControllerTest {
    private static final String REST_URL_SLASH = REST_URL + '/';

    @Autowired
    private RestaurantService service;

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_MATCHER.contentJson(restaurants));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAllWithTodayDishes() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + "menu_today"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_WITH_DISHES_VIEW_DTO_MATCHER.contentJson(asWithDishesViewDtos(restaurantsSort)));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAllWithTodayNumberVoices() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + "number-voices"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_WITH_NUMBER_VOICES_DTO_MATCHER.contentJson(getWithNumberVoicesDtos()));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + italian.id()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_MATCHER.contentJson(italian));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + NOT_FOUND))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(titleMessage(ErrorType.NOT_FOUND.title));
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + "all"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createWithLocation() throws Exception {
        Restaurant newRestaurant = getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(newRestaurant)))
                .andExpect(status().isCreated())
                .andDo(print());

        Restaurant created = RESTAURANT_MATCHER.readFromJson(action);
        int newId = created.id();
        newRestaurant.setId(newId);
        RESTAURANT_MATCHER.assertMatch(created, newRestaurant);
        RESTAURANT_VIEW_DTO_MATCHER.assertMatch(service.get(newId), asViewDto(newRestaurant));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createInvalidName() throws Exception {
        Restaurant newRestaurant = getNew();
        newRestaurant.setName("");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(newRestaurant)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createHtmlUnsafe() throws Exception {
        Restaurant newRestaurant = getNew();
        newRestaurant.setName("<script>alert(123)</script>");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(newRestaurant)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @WithUserDetails(value = ADMIN_MAIL)
    void createDuplicateDateAndName() throws Exception {
        Restaurant newRestaurant = getNew();
        newRestaurant.setName(italian.getName());
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(newRestaurant)))
                .andExpect(status().isConflict())
                .andDo(print())
                .andExpect(titleMessage(DATA_CONFLICT.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void update() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + italian.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(getUpdated())))
                .andExpect(status().isNoContent())
                .andDo(print());
        RESTAURANT_VIEW_DTO_MATCHER.assertMatch(service.get(italian.id()), asViewDto(getUpdated()));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateInvalidName() throws Exception {
        Restaurant updated = getUpdated();
        updated.setName("");
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + italian.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updated)))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateHtmlUnsafe() throws Exception {
        Restaurant updated = getUpdated();
        updated.setName("<script>alert(123)</script>");
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + italian.id())
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
        Restaurant updated = new Restaurant(null, italian.getName());
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + asian.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(updated)))
                .andExpect(status().isConflict())
                .andDo(print())
                .andExpect(titleMessage(DATA_CONFLICT.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL_SLASH + italian.id()))
                .andExpect(status().isNoContent())
                .andDo(print());
        assertThrows(NotFoundException.class, () -> service.get(italian.id()));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL_SLASH + NOT_FOUND))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(titleMessage(ErrorType.NOT_FOUND.title));
    }
}