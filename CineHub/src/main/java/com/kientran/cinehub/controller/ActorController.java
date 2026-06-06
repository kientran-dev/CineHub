package com.kientran.cinehub.controller;

import com.kientran.cinehub.dto.response.ActorResponse;
import com.kientran.cinehub.service.ActorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/actors")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ActorController {

    ActorService actorService;

    @GetMapping
    public ResponseEntity<List<ActorResponse>> getAllActors() {
        return ResponseEntity.ok(actorService.getAllActors());
    }

    @PostMapping
    public ResponseEntity<ActorResponse> createActor(@RequestBody Map<String, String> request) {
        String fullName = request.get("fullName");
        String imageUrl = request.get("imageUrl");
        return new ResponseEntity<>(actorService.createActor(fullName, imageUrl), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActor(@PathVariable Long id) {
        actorService.deleteActor(id);
        return ResponseEntity.noContent().build();
    }
}
