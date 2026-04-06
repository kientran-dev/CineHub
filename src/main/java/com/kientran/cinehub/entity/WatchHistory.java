package com.kientran.cinehub.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "watch_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class WatchHistory extends BaseEntity{
    @Column(name = "watch_time")
    Integer watchTime; // in seconds

    @Column(name = "current_episode")
    Integer currentEpisode;

    @Column(name = "watch_date")
    LocalDateTime watchDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_version_id", nullable = false)
    EpisodeVersion episodeVersion;
}