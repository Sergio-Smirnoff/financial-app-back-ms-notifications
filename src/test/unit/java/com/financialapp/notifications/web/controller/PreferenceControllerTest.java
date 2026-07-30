package com.financialapp.notifications.web.controller;

import com.financialapp.notifications.domain.model.notification.NotificationCategory;
import com.financialapp.notifications.domain.model.notification.NotificationPreference;
import com.financialapp.notifications.domain.model.notification.UserNotificationPreference;
import com.financialapp.notifications.domain.usecase.preference.GetPreferenceUseCase;
import com.financialapp.notifications.domain.usecase.preference.GetPreferencesByCategoryUseCase;
import com.financialapp.notifications.domain.usecase.preference.UpdateCategoryPreferenceUseCase;
import com.financialapp.notifications.domain.usecase.preference.UpdatePreferenceUseCase;
import com.financialapp.notifications.domain.usecase.preference.command.GetPreferenceCommand;
import com.financialapp.notifications.domain.usecase.preference.command.GetPreferencesByCategoryCommand;
import com.financialapp.notifications.domain.usecase.preference.command.UpdateCategoryPreferenceCommand;
import com.financialapp.notifications.domain.usecase.preference.command.UpdatePreferenceCommand;
import com.financialapp.notifications.web.controller.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PreferenceControllerTest {

    @Mock
    private GetPreferenceUseCase getPreferenceUseCase;
    @Mock
    private UpdatePreferenceUseCase updatePreferenceUseCase;
    @Mock
    private GetPreferencesByCategoryUseCase getPreferencesByCategoryUseCase;
    @Mock
    private UpdateCategoryPreferenceUseCase updateCategoryPreferenceUseCase;

    @InjectMocks
    private PreferenceController preferenceController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(preferenceController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getPreferencesByCategory_returnsAllCategories() throws Exception {
        NotificationPreference p1 = new NotificationPreference(1L, 42L, NotificationCategory.PAYMENT_DUE, true, false, null, null);
        NotificationPreference p2 = new NotificationPreference(2L, 42L, NotificationCategory.SUMMARY, true, true, null, null);

        when(getPreferencesByCategoryUseCase.execute(any(GetPreferencesByCategoryCommand.class)))
                .thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/v1/notifications/preferences/by-category")
                        .header("X-User-Id", "42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].category").value("PAYMENT_DUE"))
                .andExpect(jsonPath("$.data[0].hasUiToggle").value(true))
                .andExpect(jsonPath("$.data[1].category").value("SUMMARY"))
                .andExpect(jsonPath("$.data[1].emailEnabled").value(true));
    }

    @Test
    void updateCategoryPreference_updatesCategory() throws Exception {
        NotificationPreference updated = new NotificationPreference(1L, 42L, NotificationCategory.BUDGET, true, true, null, null);

        when(updateCategoryPreferenceUseCase.execute(any(UpdateCategoryPreferenceCommand.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/v1/notifications/preferences/BUDGET")
                        .header("X-User-Id", "42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"inAppEnabled\": true, \"emailEnabled\": true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.category").value("BUDGET"))
                .andExpect(jsonPath("$.data.inAppEnabled").value(true))
                .andExpect(jsonPath("$.data.emailEnabled").value(true));
    }
}
