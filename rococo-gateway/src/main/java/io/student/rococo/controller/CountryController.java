package io.student.rococo.controller;

import io.student.rococo.model.CountryJson;
import io.student.rococo.service.grpc.GrpcCountryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/country")
public class CountryController {
    private final GrpcCountryClient countryClient;

    @Autowired
    public CountryController(GrpcCountryClient countryClient) {
        this.countryClient = countryClient;
    }

    @GetMapping
    public Page<CountryJson> getAllCountries(@PageableDefault Pageable pageable) {
        return countryClient.getAllCountries(pageable);
    }
}
