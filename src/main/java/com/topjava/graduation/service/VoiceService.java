package com.topjava.graduation.service;

import com.topjava.graduation.dto.VoiceDto;
import com.topjava.graduation.dto.VoiceViewDto;
import com.topjava.graduation.model.Voice;
import com.topjava.graduation.repository.RestaurantRepository;
import com.topjava.graduation.repository.UserRepository;
import com.topjava.graduation.repository.VoiceRepository;
import com.topjava.graduation.util.VoiceUtil;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static com.topjava.graduation.util.VoiceUtil.asDto;
import static com.topjava.graduation.util.VoiceUtil.isAvailableToSave;

@Service
@AllArgsConstructor
public class VoiceService {
    private final VoiceRepository voiceRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    @Transactional
    public VoiceViewDto get(int userId) {
        return Optional.ofNullable(voiceRepository.get(userId, LocalDate.now()))
                .map(VoiceUtil::asDto)
                .orElse(null);
    }

    @Transactional
    @CacheEvict(value = "restaurantsWithNumberVoices", allEntries = true)
    public VoiceViewDto save(int userId, VoiceDto voiceDto) {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

        if (isAvailableToSave()) {
            Voice voice = voiceRepository.get(userId, date);
            if (voice == null) {
                voice = new Voice();
                voice.setUser(userRepository.getReferenceById(userId));
                voice.setRestaurant(restaurantRepository.getReferenceById(voiceDto.getRestaurantId()));
                voiceRepository.save(voice);
            } else {
                voice.setRestaurant(restaurantRepository.getReferenceById(voiceDto.getRestaurantId()));
                voice.setDate(date);
                voice.setTime(time);
            }
            return asDto(voice);
        }
        return null;
    }
}