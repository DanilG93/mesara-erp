package com.mesara.app.service;

import com.mesara.app.domain.Product;
import com.mesara.app.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct1;
    private Product testProduct2;

    @BeforeEach
    void setUp() {
        testProduct1 = new Product();
        testProduct1.setId(1L);
        testProduct1.setName("Svinjski but");
        testProduct1.setActive(true);

        testProduct2 = new Product();
        testProduct2.setId(2L);
        testProduct2.setName("Goveđa rebra");
        testProduct2.setActive(false);
    }

    @Test
    void testGetAll() {

        when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct1, testProduct2));

        List<Product> result = productService.getAll();

        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetAllActive() {

        when(productRepository.findAllByActiveTrue()).thenReturn(Collections.singletonList(testProduct1));
        List<Product> result = productService.getAllActive();

        assertEquals(1, result.size());
        assertEquals("Svinjski but", result.getFirst().getName());
        verify(productRepository, times(1)).findAllByActiveTrue();
    }

    @Test
    void testGetById_Success() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct1));

        Product result = productService.getById(1L);

        assertNotNull(result);
        assertEquals("Svinjski but", result.getName());
    }

    @Test
    void testGetById_ThrowsException() {

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.getById(99L));

        assertEquals("Artikal nije pronađen sa ID-om: 99", exception.getMessage());
    }

    @Test
    void testSave() {

        productService.save(testProduct1);

        verify(productRepository, times(1)).save(testProduct1);
    }

    @Test
    void testDelete() {

        productService.delete(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testSoftDelete_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct1));

        productService.softDelete(1L);

        assertFalse(testProduct1.isActive(), "Status mora biti false");
        verify(productRepository, times(1)).save(testProduct1);
    }
}