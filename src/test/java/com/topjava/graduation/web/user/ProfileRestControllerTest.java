package com.topjava.graduation.web.user;

import com.topjava.graduation.dto.UserDto;
import com.topjava.graduation.model.User;
import com.topjava.graduation.service.UserService;
import com.topjava.graduation.util.UsersUtil;
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
import static com.topjava.graduation.util.UsersUtil.asDto;
import static com.topjava.graduation.util.UsersUtil.createNewFromDto;
import static com.topjava.graduation.web.user.ProfileRestController.REST_URL;
import static com.topjava.graduation.web.user.UniqueMailValidator.EXCEPTION_DUPLICATE_EMAIL;
import static com.topjava.graduation.web.user.UserTestData.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProfileRestControllerTest extends AbstractControllerTest {
    @Autowired
    private UserService service;

    @Test
    @WithUserDetails(value = USER_1_MAIL)
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(user_1));
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    void register() throws Exception {
        UserDto newUserDto = asDto(getNew());
        User newUser = createNewFromDto(newUserDto);
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(newUserDto, newUserDto.getPassword())))
                .andExpect(status().isCreated())
                .andDo(print());

        User created = USER_MATCHER.readFromJson(action);
        int newId = created.id();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(service.get(newId), newUser);
    }

    @Test
    void registerInvalidName() throws Exception {
        User newUser = getNew();
        newUser.setName("");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(asDto(newUser), newUser.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    void registerInvalidEmail() throws Exception {
        User newUser = getNew();
        newUser.setEmail("invalid");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(asDto(newUser), newUser.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    void registerInvalidPassword() throws Exception {
        User newUser = getNew();
        newUser.setPassword("1234");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(asDto(newUser), newUser.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    void registerHtmlUnsafeName() throws Exception {
        User newUser = getNew();
        newUser.setName("<script>alert(123)</script>");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(asDto(newUser), newUser.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    void registerHtmlUnsafeEmail() throws Exception {
        User newUser = getNew();
        newUser.setEmail("<script>alert(123)</script>@gmail.com");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(asDto(newUser), newUser.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void registerDuplicateEmail() throws Exception {
        User newUser = getNew();
        newUser.setName(user_1.getName());
        newUser.setEmail(user_1.getEmail());
        newUser.setPassword(user_1.getPassword());
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(asDto(newUser), newUser.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title))
                .andExpect(content().string(containsString(EXCEPTION_DUPLICATE_EMAIL)));
    }

    @Test
    @WithUserDetails(value = USER_2_MAIL)
    void update() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(getUpdatedDto(), getUpdatedDto().getPassword())))
                .andExpect(status().isNoContent())
                .andDo(print());
        User updated = UsersUtil.updateFromDto(getNew(), getUpdatedDto());
        updated.setId(USER_2_ID);
        USER_MATCHER.assertMatch(service.get(USER_2_ID), updated);
    }

    @Test
    @WithUserDetails(value = USER_1_MAIL)
    void updateInvalidName() throws Exception {
        UserDto updated = getWrongUpdatedDto();
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(updated, updated.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = USER_1_MAIL)
    void updateInvalidEmail() throws Exception {
        User updated = getNew();
        updated.setEmail("invalid");
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(asDto(updated), updated.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = USER_1_MAIL)
    void updateInvalidPassword() throws Exception {
        User updated = getNew();
        updated.setPassword("1234");
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(asDto(updated), updated.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = USER_1_MAIL)
    void updateHtmlUnsafeName() throws Exception {
        User updated = getNew();
        updated.setName("<script>alert(123)</script>");
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(asDto(updated), updated.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @WithUserDetails(value = USER_1_MAIL)
    void updateHtmlUnsafeEmail() throws Exception {
        User updated = getNew();
        updated.setEmail("<script>alert(123)</script>@gmail.com");
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(asDto(updated), updated.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title));
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @WithUserDetails(value = USER_1_MAIL)
    void updateDuplicateEmail() throws Exception {
        User updated = getNew();
        updated.setEmail(user_2.getEmail());
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonWithPassword(asDto(updated), updated.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andDo(print())
                .andExpect(titleMessage(BAD_DATA.title))
                .andExpect(content().string(containsString(EXCEPTION_DUPLICATE_EMAIL)));
    }

    @Test
    @WithUserDetails(value = USER_1_MAIL)
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL))
                .andExpect(status().isNoContent())
                .andDo(print());
        USER_MATCHER.assertMatch(service.getAll(), admin, guest, user_2, user_3);
    }
}