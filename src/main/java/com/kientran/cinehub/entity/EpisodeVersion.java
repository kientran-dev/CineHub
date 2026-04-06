package com.kientran.cinehub.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "episode_versions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class EpisodeVersion extends BaseEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", nullable = false)
    Episode episode;

    @Column(name = "video_url")
    String videoUrl;

    @Column(name = "type")
    String type;

    @OneToMany(mappedBy = "episodeVersion", cascade = CascadeType.ALL, orphanRemoval = true)
    List<WatchHistory> watchHistories;
}
