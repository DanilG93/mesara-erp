package com.mesara.app.controller.api;

import com.mesara.app.domain.Store;
import com.mesara.app.dto.StoreRequest;
import com.mesara.app.dto.StoreResponse;
import com.mesara.app.mapper.StoreMapper;
import com.mesara.app.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreRestController {

    private final StoreService storeService;
    private final StoreMapper storeMapper;

    @GetMapping
    public ResponseEntity<List<StoreResponse>> getAllStores() {
        List<StoreResponse> stores = storeService.getAllActive().stream()
                .map(storeMapper::toResponse)
                .toList();
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreResponse> getStoreById(@PathVariable Long id) {
        Store store = storeService.getById(id);
        StoreResponse response = storeMapper.toResponse(store);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<StoreResponse> createStore(@Valid @RequestBody StoreRequest request) {
        Store store = storeMapper.toEntity(request);
        Store savedStore = storeService.save(store);
        StoreResponse response = storeMapper.toResponse(savedStore);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoreResponse> updateStore(@PathVariable Long id,
                                                     @Valid @RequestBody StoreRequest request) {
        Store store = storeService.getById(id);
        storeMapper.updateEntity(store, request);
        Store updatedStore = storeService.save(store);
        StoreResponse response = storeMapper.toResponse(updatedStore);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStore(@PathVariable Long id) {
        storeService.softDelete(id);

        return ResponseEntity.noContent().build();
    }
}