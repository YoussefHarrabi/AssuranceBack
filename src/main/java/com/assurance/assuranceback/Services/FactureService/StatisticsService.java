package com.assurance.assuranceback.Services.FactureService;

import com.assurance.assuranceback.Repository.FactureRepository.FactureRepos;
import com.assurance.assuranceback.dto.SocieteStatsDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@AllArgsConstructor
public class StatisticsService implements IStatisticsServices {

    private final FactureRepos factureRepos;

    @Override
    public SocieteStatsDTO getStatistiquesSociete() {
        return factureRepos.getStatistiquesSociete();
    }
}