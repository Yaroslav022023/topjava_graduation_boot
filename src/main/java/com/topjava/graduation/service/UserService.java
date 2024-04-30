package com.topjava.graduation.service;

import com.topjava.graduation.dto.UserDto;
import com.topjava.graduation.model.User;
import com.topjava.graduation.repository.UserRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

import static com.topjava.graduation.util.UsersUtil.*;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserService {
    private static final Sort SORT_NAME_EMAIL = Sort.by(Sort.Direction.ASC, "name", "email");
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAll() {
        return userRepository.findAll(SORT_NAME_EMAIL);
    }

    public User get(int id) {
        return userRepository.get(id);
    }

    public User getByEmail(String email) {
        Assert.notNull(email, "email must not be null");
        return userRepository.getExistedByEmail(email);
    }

    @Transactional
    public User save(User user) {
        Assert.notNull(user, "user must not be null");
        return user.isNew() || get(user.id()) != null ? userRepository.save(prepareToSave(user, passwordEncoder)) : null;
    }

    @Transactional
    public User save(UserDto userDto) {
        Assert.notNull(userDto, "user must not be null");
        if (userDto.isNew()) {
            return userRepository.save(prepareToSave(createNewFromDto(userDto), passwordEncoder));
        }
        User user = get(userDto.id());
        if (user != null) {
            return prepareToSave(updateFromDto(user, userDto), passwordEncoder);
        }
        return null;
    }

    public void delete(int id) {
        userRepository.delete(userRepository.get(id));
    }

    @Transactional
    public void enable(int id, boolean enabled) {
        User user = get(id);
        user.setEnabled(enabled);
    }
}