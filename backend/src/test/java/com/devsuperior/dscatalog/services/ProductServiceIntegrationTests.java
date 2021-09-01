package com.devsuperior.dscatalog.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class ProductServiceIntegrationTests {
	
	@Autowired
	private ProductService service;
	
	@Autowired
	private ProductRepository repository;
	
	private Long existingId;
	private Long nonExistingId;
	private Long countTotalProducts;
	private String nameDefault;
	private Long categoryIdDefault;
	
	@BeforeEach
	void setUp() throws Exception{
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;
		nameDefault = "";
		categoryIdDefault = 0L;
	}
	
	@Test
	public void deleteShouldDeleteResourceWhenIdExists() throws Exception{
		service.delete(existingId);
		
		Assertions.assertEquals(countTotalProducts - 1, repository.count());
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() throws Exception{	
		assertThrows(ResourceNotFoundException.class, ()->{
			service.delete(nonExistingId);

		});
	}
	
	@Test
	public void findAllPagedShouldReturnPageWhenPage0size10() {
		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<ProductDTO> result = service.findAll(categoryIdDefault, nameDefault, pageRequest);
		
		assertFalse(result.isEmpty());
		assertEquals(0, result.getNumber());
		assertEquals(10, result.getSize());
		assertEquals(countTotalProducts, result.getTotalElements());
	}
	
	@Test
	public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExists() {
		PageRequest pageRequest = PageRequest.of(50, 10);
		Page<ProductDTO> result = service.findAll(categoryIdDefault, nameDefault, pageRequest);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void findAllPagedShouldReturnSortedPageWhenSortbyName() {
		PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
		Page<ProductDTO> result = service.findAll(categoryIdDefault, nameDefault, pageRequest);
		
		assertFalse(result.isEmpty());
		assertEquals("Macbook Pro", result.getContent().get(0).getName());
		assertEquals("PC Gamer", result.getContent().get(1).getName());
		assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
	}
}
