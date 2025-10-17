package com.campusnest.campusnest_platform.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPresenceEvent {
    private Long userId;
    private String email;
    private String status;
    private LocalDateTime timestamp;
    private String sessionId;
}
