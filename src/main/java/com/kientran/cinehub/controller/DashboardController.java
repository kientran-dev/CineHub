package com.kientran.cinehub.controller;

import com.kientran.cinehub.dto.response.DashboardChartDTO;
import com.kientran.cinehub.dto.response.DashboardResponse;
import com.kientran.cinehub.service.DashboardService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardController {
    
    DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardResponse> getDashboardStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    @GetMapping("/charts")
    public ResponseEntity<DashboardChartDTO> getChartData() {
        return ResponseEntity.ok(dashboardService.getChartData());
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsvReport() {
        String csvContent = dashboardService.exportCsvReport();
        byte[] bytes = csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", "bao-cao-doanh-thu.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(bytes);
    }
}
