package ru.shtamov.apigateway.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.shtamov.apigateway.dto.ProfileResponse;
import ru.shtamov.apigateway.service.ProfileService;

@RestController
@RequestMapping(path = "/api/profile", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{userId}")
    public Mono<ProfileResponse> getProfile(@PathVariable String userId) {
        return profileService.getProfile(userId);
    }
}
