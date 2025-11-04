package com.management.tab.domain.user;

import com.management.tab.domain.common.AuditTimestamps;
import com.management.tab.domain.user.vo.Nickname;
import com.management.tab.domain.user.vo.UserId;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = "id")
public class User {

    private final UserId id;
    private final Nickname nickname;
    private final AuditTimestamps timestamps;

    public static User create(String name) {
        return new User(UserId.EMPTY_USER_ID, Nickname.create(name), AuditTimestamps.now());
    }

    private User(UserId id, Nickname nickname, AuditTimestamps timestamps) {
        this.id = id;
        this.nickname = nickname;
        this.timestamps = timestamps;
    }

    public User updateAssignedId(Long id) {
        return new User(UserId.create(id), this.nickname, this.timestamps);
    }

    public User changeNickname(String changedNickname) {
        return new User(this.id, Nickname.create(changedNickname), this.timestamps);
    }

    public UserId id() {
        return this.id;
    }

    public String getNickname() {
        return nickname.getValue();
    }

    public LocalDateTime getCreatedAt() {
        return timestamps.getCreatedAt();
    }

    public LocalDateTime getUpdatedAt() {
        return timestamps.getUpdatedAt();
    }
}
