package com.dicap.service;

import com.dicap.model.Photographer;
import com.dicap.repository.PhotographerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class PhotographerService  {
    private final PhotographerRepository photographerRepository;

    public List<Photographer> getAll(){
        return  photographerRepository.findAll();
    }

    public Optional<Photographer> get(Long id){
        return photographerRepository.findById(id);
    }

    public Photographer save(Photographer photographer){
        return   photographerRepository.save(photographer);
    }

    public void delete(Photographer photographer){
        photographerRepository.deleteById(photographer.getId());
    }

    public  Long count(){
        return photographerRepository.count();
    }


}
