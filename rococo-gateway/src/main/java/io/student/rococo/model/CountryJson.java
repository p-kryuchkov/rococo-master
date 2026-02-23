package io.student.rococo.model;

import io.student.rococo.grpc.CountryResponse;

import java.util.UUID;

public record CountryJson(UUID id, String name) {
    public static CountryJson fromGrpcMessage(CountryResponse countryResponse) {
        return new CountryJson(UUID.fromString(countryResponse.getId()), countryResponse.getName());
    }
}
