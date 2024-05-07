package com.topjava.graduation.service;

import com.topjava.graduation.dto.RestaurantViewDto;
import com.topjava.graduation.dto.RestaurantWithDishesViewDto;
import com.topjava.graduation.dto.RestaurantWithNumVoicesViewDto;
import com.topjava.graduation.model.Restaurant;
import com.topjava.graduation.model.Voice;
import com.topjava.graduation.repository.RestaurantRepository;
import com.topjava.graduation.repository.VoiceRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static com.topjava.graduation.util.RestaurantUtil.*;

@Service
@AllArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final VoiceRepository voiceRepository;

    public List<RestaurantViewDto> getAll() {
        return asViewDtos(restaurantRepository.findAll());
    }

    @Cacheable("restaurants")
    public List<RestaurantWithDishesViewDto> getAllWithTodayDishes() {
        return asWithDishesViewDtos(restaurantRepository.findAllWithTodayDishes(LocalDate.now()));
    }

    public List<RestaurantWithNumVoicesViewDto> getAllWithTodayNumberVoices() {
        List<RestaurantWithNumVoicesViewDto> restaurants =
                restaurantRepository.findAllWithTodayNumberVoices(LocalDate.now());
        restaurants.sort(Comparator.comparing(RestaurantWithNumVoicesViewDto::getVoices).reversed());
        return restaurants;
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
    @CacheEvict(value = "restaurants", allEntries = true)
    public Restaurant save(Restaurant restaurant) {
        Assert.notNull(restaurant, "restaurant must not be null");
        return restaurantRepository.save(restaurant);
    }

    @CacheEvict(value = "restaurants", allEntries = true)
    public void delete(int id) {
        restaurantRepository.deleteExisted(id);
    }
}