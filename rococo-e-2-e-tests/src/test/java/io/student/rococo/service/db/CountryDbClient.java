package io.student.rococo.service.db;

import io.student.rococo.config.Config;
import io.student.rococo.data.entity.data.CountryEntity;
import io.student.rococo.data.repository.data.CountryRepository;
import io.student.rococo.data.tpl.XaTransactionTemplate;
import io.student.rococo.model.CountryJson;
import io.student.rococo.service.CountryClient;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class CountryDbClient implements CountryClient {
    private static final Config CFG = Config.getInstance();
    private final CountryRepository countryRepository = new CountryRepository();
    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    @Override
    public CountryJson createCountry(CountryJson countryJson) {
        return requireNonNull(xaTransactionTemplate.execute(() -> {
            CountryEntity countryEntity = new CountryEntity();
            countryEntity.setName(countryJson.name());
            CountryEntity resultEntity = countryRepository.createCountry(countryEntity);
            return new CountryJson(resultEntity.getId(), resultEntity.getName());
        }));
    }

    public Optional<CountryEntity> findByName(String name){
        return countryRepository.findByName(name);
    }
}
