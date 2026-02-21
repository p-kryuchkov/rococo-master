package io.student.rococo.service.db;

import io.student.rococo.data.entity.CountryEntity;
import io.student.rococo.data.repository.CountryRepository;
import io.student.rococo.exception.FieldValidationException;
import io.student.rococo.exception.CountryNotFoundException;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CountryDbService {

    private final CountryRepository countryRepository;
@Autowired
    public CountryDbService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public Page<CountryEntity> getAll(Pageable pageable) {
        return countryRepository.findAll(pageable);
    }
}