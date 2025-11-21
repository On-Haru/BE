package com.example.onharu.caregiver.presentation;

import com.example.onharu.caregiver.application.CaregiverLinkService;
import com.example.onharu.caregiver.application.dto.CaregiverLinkResult;
import com.example.onharu.caregiver.presentation.dto.CaregiverLinkCreateRequest;
import com.example.onharu.caregiver.presentation.dto.CaregiverLinkResponse;
import com.example.onharu.global.api.ApiResponse;
import com.example.onharu.global.api.ApiResponseFactory;
import com.example.onharu.global.jwt.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/caregiver-links")
@RequiredArgsConstructor
public class CaregiverLinkController {

    private final CaregiverLinkService caregiverLinkService;

    @PostMapping()
    public ApiResponse<CaregiverLinkResponse> createLink(@LoginUser Long userId,
            @Valid @RequestBody CaregiverLinkCreateRequest request) {
        CaregiverLinkResult result = caregiverLinkService.createLink(request.toCommand(userId));
        return ApiResponseFactory.success(CaregiverLinkResponse.from(result));
    }

    @GetMapping("/{linkId}")
    public ApiResponse<CaregiverLinkResponse> getLink(@PathVariable Long linkId) {
        CaregiverLinkResult result = caregiverLinkService.getLink(linkId);
        return ApiResponseFactory.success(CaregiverLinkResponse.from(result));
    }

    @GetMapping()
    public ApiResponse<?> getSeinorList(@LoginUser Long userId) {
        var result = caregiverLinkService.getSeniorList(userId);
        return ApiResponseFactory.success(result);
    }
}
