package com.management.tab.domain.common;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AuditTimestampsTest {

    @Test
    void 생성시각과_수정시각이_같은_AuditTimestamps를_초기화한다() {
        // when
        AuditTimestamps actual = AuditTimestamps.now();

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getCreatedAt()).isNotNull(),
                () -> assertThat(actual.getUpdatedAt()).isNotNull(),
                () -> assertThat(actual.getCreatedAt()).isEqualTo(actual.getUpdatedAt())
        );
    }

    @Test
    void 지정한_시각으로_AuditTimestamps를_생성할_수_있다() {
        // given
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 2, 15, 30);

        // when
        AuditTimestamps actual = AuditTimestamps.create(createdAt, updatedAt);

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getCreatedAt()).isEqualTo(createdAt),
                () -> assertThat(actual.getUpdatedAt()).isEqualTo(updatedAt)
        );
    }

    @Test
    void 생성시각은_유지되고_수정시각만_변경해_새로운_AuditTimestamps를_초기화한다() throws InterruptedException {
        // given
        AuditTimestamps original = AuditTimestamps.now();
        LocalDateTime originalCreatedAt = original.getCreatedAt();
        LocalDateTime originalUpdatedAt = original.getUpdatedAt();

        Thread.sleep(10);

        // when
        AuditTimestamps actual = original.updateTimestamp();

        // then
        assertAll(
                () -> assertThat(actual.getCreatedAt()).isEqualTo(originalCreatedAt),
                () -> assertThat(actual.getUpdatedAt()).isAfter(originalUpdatedAt),
                () -> assertThat(actual.getCreatedAt()).isBefore(actual.getUpdatedAt())
        );
    }

    @Test
    void 같은_시각을_가진_AuditTimestamps는_동등하다() {
        // given
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 2, 15, 30);
        AuditTimestamps timestamps1 = AuditTimestamps.create(createdAt, updatedAt);
        AuditTimestamps timestamps2 = AuditTimestamps.create(createdAt, updatedAt);

        // when & then
        assertAll(
                () -> assertThat(timestamps1).isEqualTo(timestamps2),
                () -> assertThat(timestamps1).hasSameHashCodeAs(timestamps2)
        );
    }

    @Test
    void 다른_시각을_가진_AuditTimestamps는_동등하지_않다() throws InterruptedException {
        // given
        AuditTimestamps timestamps1 = AuditTimestamps.now();

        Thread.sleep(10);

        AuditTimestamps timestamps2 = AuditTimestamps.now();

        // when & then
        assertAll(
                () -> assertThat(timestamps1).isNotEqualTo(timestamps2),
                () -> assertThat(timestamps1).doesNotHaveSameHashCodeAs(timestamps2)
        );
    }

    @Test
    void updateTimestamp_결과는_원본과_동등하지_않다() throws InterruptedException {
        // given
        AuditTimestamps original = AuditTimestamps.now();

        Thread.sleep(10);

        // when
        AuditTimestamps actual = original.updateTimestamp();

        // then
        assertAll(
                () -> assertThat(original).isNotEqualTo(actual),
                () -> assertThat(original).doesNotHaveSameHashCodeAs(actual)
        );
    }
}
