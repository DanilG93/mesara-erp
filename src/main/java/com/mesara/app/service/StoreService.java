package com.mesara.app.service;

import com.mesara.app.domain.Store;
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
    public void saveStore(Store store) {
        storeRepository.save(store);
    }

    public Store getById(Long id) {
        return storeRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deleteStore(Long id) {
        storeRepository.deleteById(id);
    }

    @Transactional
    public void softDelete(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prodavnica nije nađena"));
        store.setActive(false); // Postavljamo na 0
        storeRepository.save(store);
    }
}