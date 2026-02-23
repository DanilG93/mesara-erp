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
}