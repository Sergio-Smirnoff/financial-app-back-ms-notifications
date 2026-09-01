package com.financialapp.notifications.application.usecase.preference;

import com.financialapp.notifications.application.usecase.preference.impl.GetPreferencesByCategoryUseCaseImpl;
import com.financialapp.notifications.domain.model.notification.NotificationCategory;
import com.financialapp.notifications.domain.model.notification.NotificationPreference;
import com.financialapp.notifications.domain.repository.NotificationPreferenceRepository;
import com.financialapp.notifications.domain.usecase.preference.command.GetPreferencesByCategoryCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetPreferencesByCategoryUseCaseImplTest {

    @Mock
    private NotificationPreferenceRepository preferenceRepository;

    @InjectMocks
    private GetPreferencesByCategoryUseCaseImpl useCase;

    @Test
    void execute_returnsAllSevenCategoriesWithDefaultsWhenAbsent() {
        NotificationPreference customSummary = new NotificationPreference(
                1L, 42L, NotificationCategory.SUMMARY, true, false, null, null);

        // Default stub for all categories — returns empty so defaults() kicks in
        lenient().when(preferenceRepository.findByUserIdAndCategory(anyLong(), any()))
                .thenReturn(Optional.empty());

        // Override for SUMMARY — return the custom preference
        when(preferenceRepository.findByUserIdAndCategory(eq(42L), eq(NotificationCategory.SUMMARY)))
                .thenReturn(Optional.of(customSummary));

        List<NotificationPreference> result = useCase.execute(new GetPreferencesByCategoryCommand(42L));

        assertThat(result).hasSize(7);
        assertThat(result).extracting(NotificationPreference::category)
                .containsExactlyInAnyOrder(NotificationCategory.values());

        NotificationPreference summaryRes = result.stream()
                .filter(p -> p.category() == NotificationCategory.SUMMARY)
                .findFirst().orElseThrow();
        assertThat(summaryRes.emailEnabled()).isFalse();

        NotificationPreference paymentDueRes = result.stream()
                .filter(p -> p.category() == NotificationCategory.PAYMENT_DUE)
                .findFirst().orElseThrow();
        assertThat(paymentDueRes.inAppEnabled()).isTrue();
        assertThat(paymentDueRes.emailEnabled()).isFalse();
    }
}
