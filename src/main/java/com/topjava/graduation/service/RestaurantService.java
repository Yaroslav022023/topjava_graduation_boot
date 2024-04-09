package com.topjava.graduation.service;

import com.topjava.graduation.dto.RestaurantViewDto;
import com.topjava.graduation.dto.RestaurantVotedByUserDto;
import com.topjava.graduation.dto.RestaurantWithNumberVoicesDto;
import com.topjava.graduation.model.Restaurant;
import com.topjava.graduation.model.Voice;
import com.topjava.graduation.repository.RestaurantRepository;
import com.topjava.graduation.repository.UserRepository;
import com.topjava.graduation.repository.VoiceRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.topjava.graduation.util.RestaurantUtil.convertToViewDtos;
import static com.topjava.graduation.util.RestaurantUtil.convertToVotedByUserDto;
import static com.topjava.graduation.util.VoiceUtil.isAvailableUpdate;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final VoiceRepository voiceRepository;
    private final UserRepository userRepository;

    public RestaurantService(RestaurantRepository restaurantRepository,
                             VoiceRepository voiceRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.voiceRepository = voiceRepository;
        this.userRepository = userRepository;
    }

    public List<Restaurant> getAll() {
        return restaurantRepository.findAll();
    }

    @Cacheable("restaurants")
    public List<RestaurantViewDto> getAllWithMealsForToday() {
        return convertToViewDtos(restaurantRepository.findAllWithMealsForToday(LocalDate.now()));
    }

    public List<RestaurantWithNumberVoicesDto> getAllWithNumberVoicesForToday() {
        return restaurantRepository.findAllWithNumberVoicesForToday(LocalDate.now());
    }

    public Restaurant get(int id) {
        return restaurantRepository.get(id);
    }

    public RestaurantVotedByUserDto getVotedByUserForToday(int userId) {
        return convertToVotedByUserDto(voiceRepository.getBelonged(userId, LocalDate.now()).getRestaurant());
    }

    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public Restaurant save(Restaurant restaurant) {
        Assert.notNull(restaurant, "restaurant must not be null");
        return restaurant.isNew() || get(restaurant.id()) != null ? restaurantRepository.save(restaurant) : null;
    }

    @CacheEvict(value = "restaurants", allEntries = true)
    public void delete(int id) {
        restaurantRepository.delete(restaurantRepository.get(id));
    }

    @Transactional
    public void vote(int userId, int restaurantId) {
        Optional<Voice> existing = voiceRepository.get(userId, LocalDate.now());
        existing.ifPresent(voice -> {
            if (isAvailableUpdate(voice)) {
                voice.setRestaurant(restaurantRepository.getReferenceById(restaurantId));
                voice.setTime(LocalTime.now());
            }
        });
        if (existing.isEmpty()) {
            Voice voice = new Voice();
            voice.setUser(userRepository.getReferenceById(userId));
            voice.setRestaurant(restaurantRepository.getReferenceById(restaurantId));
            voiceRepository.save(voice);
        }
    }
}