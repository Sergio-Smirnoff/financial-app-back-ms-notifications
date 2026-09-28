package com.financialapp.notifications.domain.repository;

import java.time.YearMonth;

public interface MonthlySummarySentRepository {

    boolean claim(Long userId, YearMonth month);

    void release(Long userId, YearMonth month);
}
