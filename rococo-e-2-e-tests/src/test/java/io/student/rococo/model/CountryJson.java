package io.student.rococo.model;

import io.student.rococo.data.entity.data.CountryEntity;
import io.student.rococo.grpc.CountryResponse;

import javax.annotation.Nonnull;
import java.util.UUID;

public record CountryJson(UUID id, String name) {
    public static @Nonnull CountryJson fromEntity(@Nonnull final CountryEntity countryEntity) {
        return new CountryJson(
                countryEntity.getId(),
                countryEntity.getName()
        );
    }
}
