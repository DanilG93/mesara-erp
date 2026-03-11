package com.mesara.app.mapper;

import com.mesara.app.domain.Store;
import com.mesara.app.dto.StoreRequest;
import com.mesara.app.dto.StoreResponse;
import org.springframework.stereotype.Component;

@Component
public class StoreMapper {

    public Store toEntity(StoreRequest request) {
        Store store = new Store();
        store.setName(request.name());
        store.setAddress(request.address());
        store.setLocation(request.location());
        store.setActive(true);
        return store;
    }

    public StoreResponse toResponse(Store store) {
        return new StoreResponse(
                store.getId(),
                store.getName(),
                store.getAddress(),
                store.getLocation(),
                store.isActive()
        );
    }

    public void updateEntity(Store store, StoreRequest request) {
        store.setName(request.name());
        store.setAddress(request.address());
        store.setLocation(request.location());
    }
}
