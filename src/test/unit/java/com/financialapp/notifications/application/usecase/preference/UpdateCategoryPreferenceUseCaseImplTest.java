package com.financialapp.notifications.application.usecase.preference;

import com.financialapp.notifications.application.usecase.preference.impl.UpdateCategoryPreferenceUseCaseImpl;
import com.financialapp.notifications.domain.exception.BusinessException;
import com.financialapp.notifications.domain.model.notification.NotificationCategory;
import com.financialapp.notifications.domain.model.notification.NotificationPreference;
import com.financialapp.notifications.domain.repository.NotificationPreferenceRepository;
import com.financialapp.notifications.domain.usecase.preference.command.UpdateCategoryPreferenceCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCategoryPreferenceUseCaseImplTest {

    @Mock
    private NotificationPreferenceRepository preferenceRepository;

    @InjectMocks
    private UpdateCategoryPreferenceUseCaseImpl useCase;

    @Test
    void execute_validCategory_upsertsPreference() {
        when(preferenceRepository.findByUserIdAndCategory(42L, NotificationCategory.PAYMENT_DUE))
                .thenReturn(Optional.empty());
        when(preferenceRepository.save(any(NotificationPreference.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        UpdateCategoryPreferenceCommand cmd = new UpdateCategoryPreferenceCommand(42L, "PAYMENT_DUE", true, true);
        NotificationPreference result = useCase.execute(cmd);

        assertThat(result.category()).isEqualTo(NotificationCategory.PAYMENT_DUE);
        assertThat(result.inAppEnabled()).isTrue();
        assertThat(result.emailEnabled()).isTrue();
        verify(preferenceRepository).save(any(NotificationPreference.class));
    }

    @Test
    void execute_invalidCategory_throwsBusinessException() {
        UpdateCategoryPreferenceCommand cmd = new UpdateCategoryPreferenceCommand(42L, "INVALID_CAT", true, true);

        assertThatThrownBy(() -> useCase.execute(cmd))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid notification category");
    }
}
