package com.topjava.graduation.web.user;

import com.topjava.graduation.dto.UserDto;
import com.topjava.graduation.model.User;
import com.topjava.graduation.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.util.List;

import static com.topjava.graduation.util.validation.ValidationUtil.assureIdConsistent;
import static com.topjava.graduation.util.validation.ValidationUtil.checkNew;

@Slf4j
public abstract class AbstractUserController {
    @Autowired
    private UserService service;

    @Autowired
    private UniqueMailValidator emailValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(emailValidator);
    }

    public List<User> getAll() {
        log.info("getAll");
        return service.getAll();
    }

    public User get(int id) {
        log.info("get {}", id);
        return service.get(id);
    }

    public User getByMail(String email) {
        log.info("getByEmail {}", email);
        return service.getByEmail(email);
    }

    public User create(User user) {
        log.info("create {}", user);
        checkNew(user);
        return service.save(user);
    }

    public User create(UserDto userDto) {
        log.info("create {}", userDto);
        checkNew(userDto);
        return service.save(userDto);
    }

    public void update(User user, int id) {
        log.info("update {} with id={}", user, id);
        assureIdConsistent(user, id);
        service.save(user);
    }

    public void update(UserDto userDto, int id) {
        log.info("update {} with id={}", userDto, id);
        assureIdConsistent(userDto, id);
        service.save(userDto);
    }

    public void delete(int id) {
        log.info("delete {}", id);
        service.delete(id);
    }

    public void enable(int id, boolean enabled) {
        log.info(enabled ? "enable {}" : "disable {}", id);
        service.enable(id, enabled);
    }
}