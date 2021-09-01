package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.factory.Factory;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseIntegrityException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private String nameDefault;
	private Long categoryIdDefault;
	private PageImpl<Product> page;
	private Product product;
	private ProductDTO productDto;
	private Category category;
	
	@BeforeEach
	void setUp() throws Exception{
		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		nameDefault = "";
		categoryIdDefault = 0L;
		product = Factory.createProduct();
		page = new PageImpl<>(List.of(product));
		productDto = Factory.createProductDTO();
		category = Factory.createCategory();
		
		Mockito.when(repository.findAll((Pageable)any())).thenReturn(page);
		
		Mockito.when(repository.save(any())).thenReturn(product);
		
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		
		Mockito.when(repository.find(any(), any(), any())).thenReturn(page);
		
		Mockito.when(repository.getOne(existingId)).thenReturn(product);
		Mockito.doThrow(EntityNotFoundException.class).when(repository).getOne(nonExistingId);
		
		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
		
		Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
	}
	
	@Test
	public void findAllPagedShouldReturnPage() {
		
		Pageable pageable = PageRequest.of(0, 10);
		
		Page<ProductDTO> result = service.findAll(categoryIdDefault, nameDefault, pageable);
		
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() {
		ProductDTO dto = service.findById(existingId);
		Assertions.assertNotNull(dto);
		Mockito.verify(repository).findById(existingId);
	}
	
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			service.findById(nonExistingId);
		});
		Mockito.verify(repository).findById(nonExistingId);
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() {
		productDto.setId(null);
		ProductDTO dto = service.update(existingId, productDto);
		Assertions.assertNotNull(dto);
		Mockito.verify(repository).getOne(existingId);
		Mockito.verify(categoryRepository).getOne(dependentId);
		Mockito.verify(repository).save(product);
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		productDto.setId(null);
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			service.update(nonExistingId, productDto);
		});
		Mockito.verify(repository).getOne(nonExistingId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			service.delete(nonExistingId);
		});
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
	}
	
	@Test
	public void deleteShouldThrowDatabaseIntegrityExceptionWhenIdViolatesDatabase() {
		Assertions.assertThrows(DatabaseIntegrityException.class, ()->{
			service.delete(dependentId);
		});
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}
	
	@Test
	public void deleteShouldDoNothinWhenIdExists() {
		Assertions.assertDoesNotThrow(()->{
			service.delete(existingId);
		});
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}
}
