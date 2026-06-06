package com.kientran.cinehub.service;

import com.kientran.cinehub.dto.response.ActorResponse;
import com.kientran.cinehub.entity.Actor;
import com.kientran.cinehub.repository.ActorRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActorService {

    ActorRepository actorRepository;

    public List<ActorResponse> getAllActors() {
        return actorRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ActorResponse createActor(String fullName, String imageUrl) {
        Actor actor = Actor.builder()
                .fullName(fullName)
                .imageUrl(imageUrl)
                .build();
        actor = actorRepository.save(actor);
        return mapToResponse(actor);
    }

    public void deleteActor(Long id) {
        if (!actorRepository.existsById(id)) {
            throw new RuntimeException("Actor not found");
        }
        actorRepository.deleteById(id);
    }

    private ActorResponse mapToResponse(Actor actor) {
        return ActorResponse.builder()
                .id(actor.getId())
                .fullName(actor.getFullName())
                .imageUrl(actor.getImageUrl())
                .build();
    }
}
