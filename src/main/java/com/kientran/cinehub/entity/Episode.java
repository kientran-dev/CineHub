package com.kientran.cinehub.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "episodes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Episode extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    Movie movie;

    @Column(name = "episode_number")
    Integer episodeNumber;

    @Column(name = "episode_name")
    String episodeName;

    @Column(name = "video_url")
    String videoUrl;

    @Column(name = "server_name")
    String serverName;

    @OneToMany(mappedBy = "episode", cascade = CascadeType.ALL, orphanRemoval = true)
    List<WatchHistory> watchHistories;
}