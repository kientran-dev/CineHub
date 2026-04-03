package com.kientran.cinehub.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Movie extends BaseEntity{
    @Column(name = "title")
    String title;
    
    @Column(name = "english_title")
    String englishTitle;

    @Column(name = "thumbnail")
    String thumbnail;

    @Column(name = "poster")
    String poster;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "director")
    String director;
    
    @Column(name = "release_year")
    Integer releaseYear;

    @Column(name = "duration")
    Integer duration;

    @Column(name = "country")
    String country;

    @Column(name = "status")
    String status;

    @Column(name = "type")
    String type;
    
    @Column(name = "imdb")
    Double imdb;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Episode> episodes;

    @ManyToMany
    @JoinTable(
        name = "movie_genre",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    Set<Genre> genres;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comment> comments;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Rating> ratings;
    
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Favorite> favorites;
}