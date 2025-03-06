package com.assurance.assuranceback.Controller.FactureController;

import com.assurance.assuranceback.Services.FactureService.IStatisticsServices;
import com.assurance.assuranceback.dto.SocieteStatsDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@AllArgsConstructor
public class StatisticsController {

    private final IStatisticsServices statisticsServices;

    @GetMapping("/societe")
    public ResponseEntity<SocieteStatsDTO> getStatistiquesSociete() {
        try {
            SocieteStatsDTO stats = statisticsServices.getStatistiquesSociete();
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
