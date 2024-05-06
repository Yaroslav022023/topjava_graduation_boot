package com.topjava.graduation.web.voice;

import com.topjava.graduation.dto.VoiceDto;
import com.topjava.graduation.dto.VoiceViewDto;
import com.topjava.graduation.service.VoiceService;
import com.topjava.graduation.web.AuthUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static com.topjava.graduation.web.voice.VoiceController.REST_URL;

@RestController
@RequestMapping(value = REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class VoiceController {
    static final String REST_URL = "/api/voices";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final VoiceService service;

    public VoiceController(VoiceService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<VoiceViewDto> get(@AuthenticationPrincipal AuthUser authUser) {
        log.info("get voice of user={}", authUser.id());
        VoiceViewDto voice = service.get(authUser.id());
        return voice != null ? ResponseEntity.ok(voice) : ResponseEntity.noContent().build();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VoiceViewDto> createWithLocation(@RequestBody VoiceDto voiceDto,
                                                           @AuthenticationPrincipal AuthUser authUser) {
        log.info("create voice for user={}", authUser.id());
        VoiceViewDto created = service.save(authUser.id(), voiceDto);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL).build().toUri();

        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody VoiceDto voiceDto, @AuthenticationPrincipal AuthUser authUser) {
        log.info("update voice of user={}", authUser.id());
        service.save(authUser.id(), voiceDto);
    }
}