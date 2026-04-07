package com.kientran.cinehub.controller;

import com.kientran.cinehub.dto.request.PremiumPackageRequest;
import com.kientran.cinehub.dto.response.PremiumPackageResponse;
import com.kientran.cinehub.service.PremiumPackageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/premium-packages")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PremiumPackageController {

    PremiumPackageService premiumPackageService;

    @GetMapping
    public ResponseEntity<List<PremiumPackageResponse>> getAllPackages() {
        return ResponseEntity.ok(premiumPackageService.getAllPackages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PremiumPackageResponse> getPackageById(@PathVariable Long id) {
        return ResponseEntity.ok(premiumPackageService.getPackageById(id));
    }

    @PostMapping
    public ResponseEntity<PremiumPackageResponse> createPackage(@RequestBody PremiumPackageRequest request) {
        return new ResponseEntity<>(premiumPackageService.createPackage(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PremiumPackageResponse> updatePackage(@PathVariable Long id, @RequestBody PremiumPackageRequest request) {
        return ResponseEntity.ok(premiumPackageService.updatePackage(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePackage(@PathVariable Long id) {
        premiumPackageService.deletePackage(id);
        return ResponseEntity.noContent().build();
    }
}
