package com.kientran.cinehub.repository;


import com.kientran.cinehub.entity.Episode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EpisodeRepository extends MongoRepository<Episode,String> {
    @Override
    Optional<Episode> findById(String s);
}
