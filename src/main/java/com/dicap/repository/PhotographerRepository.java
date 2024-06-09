package com.dicap.repository;

import com.dicap.model.Photographer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotographerRepository extends JpaRepository<Photographer,Long>  {
    Photographer findByEmail(String email);
    Photographer findByNomComplet(String nomComplet);
}
