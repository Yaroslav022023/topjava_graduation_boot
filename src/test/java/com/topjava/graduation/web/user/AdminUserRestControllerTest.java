package com.topjava.graduation.web.user;

import com.topjava.graduation.exception.ErrorType;
import com.topjava.graduation.exception.NotFoundException;
import com.topjava.graduation.model.User;
import com.topjava.graduation.service.UserService;
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
import static com.topjava.graduation.web.meal.MealTestData.NOT_FOUND;
import static com.topjava.graduation.web.user.AdminUserRestController.REST_URL;
import static com.topjava.graduation.web.user.UniqueMailValidator.EXCEPTION_DUPLICATE_EMAIL;
import static com.topjava.graduation.web.user.UserTestData.*;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminUserRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL_SLASH = REST_URL + '/';
    @Autowired
    private UserService service;

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(users));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + ADMIN_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(admin));
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
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithUserDetails(value = USER_1_MAIL)
    void getForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getByEmail() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + "by-email?email=" + user_1.getEmail()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(user_1));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getByEmailNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL_SLASH + "by-email?email=" + "not_found@gmail.com"))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(titleMessage(ErrorType.NOT_FOUND.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createWithLocation() throws Exception {
        User newUser = getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUser, newUser.getPassword())))
                .andExpect(status().isCreated())
                .andDo(print());

        User created = USER_MATCHER.readFromJson(action);
        int newId = created.id();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(service.get(newId), newUser);
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createInvalidName() throws Exception {
        User newUser = getNew();
        newUser.setName("");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUser, newUser.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createInvalidEmail() throws Exception {
        User newUser = getNew();
        newUser.setEmail("invalid");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUser, newUser.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createInvalidPassword() throws Exception {
        User newUser = getNew();
        newUser.setPassword("1234");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUser, newUser.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createHtmlUnsafeName() throws Exception {
        User newUser = getNew();
        newUser.setName("<script>alert(123)</script>");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUser, newUser.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void createHtmlUnsafeEmail() throws Exception {
        User newUser = getNew();
        newUser.setEmail("<script>alert(123)</script>@gmail.com");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUser, newUser.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @WithUserDetails(value = ADMIN_MAIL)
    void createDuplicateEmail() throws Exception {
        User newUser = getNew();
        newUser.setName(user_1.getName());
        newUser.setEmail(user_1.getEmail());
        newUser.setPassword(user_1.getPassword());
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUser, newUser.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title))
                .andExpect(content().string(containsString(EXCEPTION_DUPLICATE_EMAIL)));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void update() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + USER_2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(getUpdated(), getUpdated().getPassword())))
                .andExpect(status().isNoContent())
                .andDo(print());
        USER_MATCHER.assertMatch(service.get(USER_2_ID), getUpdated());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateInvalidName() throws Exception {
        User updated = getUpdated();
        updated.setName("");
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + USER_2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateInvalidEmail() throws Exception {
        User updated = getUpdated();
        updated.setEmail("invalid");
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + USER_2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateInvalidPassword() throws Exception {
        User updated = getUpdated();
        updated.setPassword("1234");
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + USER_2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateHtmlUnsafeName() throws Exception {
        User updated = getUpdated();
        updated.setName("<script>alert(123)</script>");
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + USER_2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void updateHtmlUnsafeEmail() throws Exception {
        User updated = getUpdated();
        updated.setEmail("<script>alert(123)</script>@gmail.com");
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + USER_2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @WithUserDetails(value = ADMIN_MAIL)
    void updateDuplicateEmail() throws Exception {
        User updated = getUpdated();
        updated.setEmail(user_1.getEmail());
        perform(MockMvcRequestBuilders.put(REST_URL_SLASH + USER_2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title))
                .andExpect(content().string(containsString(EXCEPTION_DUPLICATE_EMAIL)));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL_SLASH + USER_1_ID))
                .andExpect(status().isNoContent())
                .andDo(print());
        assertThrows(NotFoundException.class, () -> service.get(USER_1_ID));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL_SLASH + NOT_FOUND))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(titleMessage(ErrorType.NOT_FOUND.title));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void enable() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL_SLASH + USER_1_ID)
                .param("enabled", "false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());

        assertFalse(service.get(USER_1_ID).isEnabled());
    }
}