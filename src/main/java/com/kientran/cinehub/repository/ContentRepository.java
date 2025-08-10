package com.kientran.cinehub.repository;

import com.kientran.cinehub.entity.Content;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentRepository extends MongoRepository<Content,String> {

    @Override
    Optional<Content> findById(String s);
}
