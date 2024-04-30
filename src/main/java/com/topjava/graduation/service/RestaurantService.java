package com.topjava.graduation.service;

import com.topjava.graduation.dto.RestaurantViewDto;
import com.topjava.graduation.dto.RestaurantWithDishesViewDto;
import com.topjava.graduation.dto.RestaurantWithNumberVoicesDto;
import com.topjava.graduation.model.Restaurant;
import com.topjava.graduation.model.Voice;
import com.topjava.graduation.repository.RestaurantRepository;
import com.topjava.graduation.repository.VoiceRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.List;

import static com.topjava.graduation.util.RestaurantUtil.*;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final VoiceRepository voiceRepository;

    public RestaurantService(RestaurantRepository restaurantRepository, VoiceRepository voiceRepository) {
        this.restaurantRepository = restaurantRepository;
        this.voiceRepository = voiceRepository;
    }

    public List<RestaurantViewDto> getAll() {
        return asViewDtos(restaurantRepository.findAll());
    }

    @Cacheable("restaurants")
    public List<RestaurantWithDishesViewDto> getAllWithTodayDishes() {
        return asWithDishesViewDtos(restaurantRepository.findAllWithTodayDishes(LocalDate.now()));
    }

    @Cacheable("restaurantsWithNumberVoices")
    public List<RestaurantWithNumberVoicesDto> getAllWithTodayNumberVoices() {
        return restaurantRepository.findAllWithTodayNumberVoices(LocalDate.now());
    }

    public RestaurantViewDto get(int id) {
        Restaurant restaurant = restaurantRepository.get(id);
        return restaurant != null ? asViewDto(restaurant) : null;
    }

    public RestaurantViewDto getVotedByUserForToday(int userId) {
        Voice voice = voiceRepository.get(userId, LocalDate.now());
        return voice != null ? asViewDto(voice.getRestaurant()) : null;
    }

    @Transactional
    @CacheEvict(value = {"restaurants", "restaurantsWithNumberVoices"}, allEntries = true)
    public Restaurant save(Restaurant restaurant) {
        Assert.notNull(restaurant, "restaurant must not be null");
        return restaurant.isNew() || get(restaurant.id()) != null ? restaurantRepository.save(restaurant) : null;
    }

    @CacheEvict(value = {"restaurants", "restaurantsWithNumberVoices"}, allEntries = true)
    public void delete(int id) {
        restaurantRepository.deleteExisted(id);
    }
}