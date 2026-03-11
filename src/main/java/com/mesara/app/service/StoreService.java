package com.mesara.app.service;

import com.mesara.app.domain.Store;
import com.mesara.app.exception.ResourceNotFoundException;
import com.mesara.app.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    public List<Store> getAllActive() {
        return storeRepository.findAllByActiveTrue();
    }

    @Transactional
    public Store save(Store store) {
       return storeRepository.save(store);
    }

    public Store getById(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with ID: " + id));
    }

    @Transactional
    public void deleteStore(Long id) {
        storeRepository.deleteById(id);
    }

    @Transactional
    public void softDelete(Long id) {
        Store store = getById(id);
        store.setActive(false);
        storeRepository.save(store);
    }
}