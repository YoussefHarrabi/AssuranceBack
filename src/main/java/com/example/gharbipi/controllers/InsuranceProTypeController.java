package com.example.gharbipi.controllers;

import com.example.gharbipi.entities.InsuranceProType;
import com.example.gharbipi.services.InsuranceProTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/insuranceProTypes")
@CrossOrigin(origins = "http://localhost:4200")
public class InsuranceProTypeController {

        @Autowired
        private InsuranceProTypeService insuranceProTypeService;

        @GetMapping
        public List<InsuranceProType> getAllInsuranceProTypes() {
                return insuranceProTypeService.getAllInsuranceProTypes();
        }

        @GetMapping("/{id}")
        public Optional<InsuranceProType> getInsuranceProTypeById(@PathVariable Long id) {
                return insuranceProTypeService.getInsuranceProTypeById(id);
        }

        @PostMapping
        public InsuranceProType createInsuranceProType(@RequestBody InsuranceProType insuranceProType) {
                return insuranceProTypeService.createInsuranceProType(insuranceProType);
        }

        @PutMapping
        public InsuranceProType updateInsuranceProType(@RequestBody InsuranceProType insuranceProType) {
                return insuranceProTypeService.updateInsuranceProType(insuranceProType);
        }

        @DeleteMapping("/{id}")
        public void deleteInsuranceProType(@PathVariable Long id) {
                insuranceProTypeService.deleteInsuranceProType(id);
        }
}

