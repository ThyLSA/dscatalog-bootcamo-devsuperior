package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.factory.Factory;

@DataJpaTest
public class ProductRepositoryTests {
	
	@Autowired
	private ProductRepository repository;
	
	private long existingId;
	private long nonExistingId;
	private long countTotalProducts;
	
	@BeforeEach
	void setUp() throws Exception{
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;
	}
	
	@Test
	public void findByIdShouldReturnNotEmptyOptionalWhenIdExists() {
		Optional<Product> optional = repository.findById(existingId);
		Assertions.assertTrue(optional.isPresent());
	}
	
	@Test
	public void findByIdShouldReturnEmptyOptionalWhenIdNotExists() {
		Optional<Product> optional = repository.findById(nonExistingId);
		Assertions.assertFalse(optional.isPresent());
	}
	
	@Test
	public void saveShouldPersistWithAutoincrementWhenIdIsNull() {
		Product product = Factory.createProduct();
		product.setId(null);
		
		product = repository.save(product);
		
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts+1, product.getId());
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		repository.deleteById(existingId);
		Optional<Product> result = repository.findById(existingId);
		Assertions.assertFalse(result.isPresent());
	}
	
	@Test
	public void deleteShouldThrowEmptyResultNotFoundExeptionWhenIdDoesNotExist() {
		Assertions.assertThrows(EmptyResultDataAccessException.class, ()->{
			repository.deleteById(nonExistingId);
		});
	}
}
