package com.dicap.service;

import com.dicap.model.ImageEntity;
import com.dicap.repository.ImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImageService {

    private  final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public List<ImageEntity> findAll() {
        return imageRepository.findAll();
    }

    public Optional<ImageEntity> findById(Long id) {
        return imageRepository.findById(id);
    }

    public ImageEntity save(ImageEntity image) {
        return imageRepository.save(image);
    }

    public void deleteById(Long id) {
        imageRepository.deleteById(id);
    }

    public Long count(){ return imageRepository.count(); }

    public void like(ImageEntity imageEntity){
        imageEntity.setLikes(imageEntity.getLikes()+1L);
        imageRepository.save(imageEntity);
    }
}
