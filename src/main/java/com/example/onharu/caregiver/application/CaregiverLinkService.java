package com.example.onharu.caregiver.application;

import com.example.onharu.caregiver.application.dto.CaregiverLinkCreateCommand;
import com.example.onharu.caregiver.application.dto.CaregiverLinkResult;
import com.example.onharu.caregiver.domain.CaregiverLink;
import com.example.onharu.caregiver.domain.CaregiverLinkRepository;
import com.example.onharu.global.exception.BusinessException;
import com.example.onharu.global.exception.ErrorCode;
import com.example.onharu.user.domain.User;
import com.example.onharu.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaregiverLinkService {

    private final CaregiverLinkRepository caregiverLinkRepository;
    private final UserRepository userRepository;

    @Transactional
    public CaregiverLinkResult createLink(CaregiverLinkCreateCommand command) {
        User caregiver = userRepository.findById(command.caregiverId())
                .filter(User::isCaregiver)
                .orElseThrow(() -> new BusinessException(ErrorCode.CARE_RECEIVER_INFO_MISMATCH));

        User senior = isValidSeniorCode(command.phone(), command.code());

        validateSeniorNotLinked(caregiver.getId(), senior.getId());

        CaregiverLink link = CaregiverLink.create(caregiver, senior);
        return CaregiverLinkResult.from(caregiverLinkRepository.save(link));
    }

    public CaregiverLinkResult getLink(Long linkId) {
        return caregiverLinkRepository.findById(linkId)
                .map(CaregiverLinkResult::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.CARE_RECEIVER_NOT_FOUND));
    }

    public Object getSeniorList(Long caregiverId) {
        var seniorLinks = caregiverLinkRepository.findAllByCaregiverId(caregiverId);
        return seniorLinks.stream()
                .map(CaregiverLinkResult::from)
                .toList();
    }

    @Transactional
    public void unlink(Long caregiverId, Long linkId) {
        CaregiverLink link = caregiverLinkRepository.findById(linkId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CARE_RECEIVER_NOT_FOUND));

        if (!link.getCaregiver().getId().equals(caregiverId)) {
            throw new BusinessException(ErrorCode.CARE_LINK_ACCESS_DENIED);
        }

        caregiverLinkRepository.delete(link);
    }

    private User isValidSeniorCode(String phone, int code) {
        return userRepository.findByPhoneAndCode(phone, code)
                .filter(User::isSenior)
                .orElseThrow(() -> new BusinessException(ErrorCode.CARE_RECEIVER_INFO_MISMATCH));
    }

    private void validateSeniorNotLinked(Long caregiverId, Long seniorId) {
        boolean isLinked = caregiverLinkRepository.alreadyLinkExists(caregiverId, seniorId);
        if (isLinked) {
            throw new BusinessException(ErrorCode.CAREGIVER_ALREADY_REGISTERED);
        }
    }
    
    public boolean hasLink(Long userId) {
        return caregiverLinkRepository.existsByCaregiverId(userId);
    }
}
