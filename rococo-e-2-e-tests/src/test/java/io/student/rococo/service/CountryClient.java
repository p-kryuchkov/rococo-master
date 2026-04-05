package io.student.rococo.service;

import io.student.rococo.model.CountryJson;

public interface CountryClient {
    CountryJson createCountry(CountryJson countryJson);
}
