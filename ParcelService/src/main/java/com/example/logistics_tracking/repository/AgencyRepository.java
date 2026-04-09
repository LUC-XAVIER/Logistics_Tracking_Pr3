package com.example.logistics_tracking.repository;

import com.example.logistics_tracking.entity.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface AgencyRepository extends JpaRepository<Agency, UUID> {

    List<Agency> findByCountry(String country);
    List<Agency> findByCountryAndTown(String country, String town);
}