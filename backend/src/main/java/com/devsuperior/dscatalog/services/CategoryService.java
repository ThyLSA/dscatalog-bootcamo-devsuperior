package com.devsuperior.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseIntegrityException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

//Classe da camada de servico, usar a anotacao Service
@Service
public class CategoryService {
	
	//Anotacao para o framework cuidar de instanciar esse objeto
	@Autowired
	private CategoryRepository repository;
	
	//Transactional -> pro metodo executar como uma transação no banco de dados
	//readyOnly -> para nao travar o banco de dados em uma operação de leitura
	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaged(PageRequest pageRequest){
		Page<Category> list = repository.findAll(pageRequest);
		return list.map(x -> new CategoryDTO(x));
	}
	
	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id){
		//Optional - para evitar trabalhar com valor null. O repository retorna o Optional
		Optional<Category> obj = repository.findById(id);
		//OrElse - se nao tiver o objeto, ele retorna outra coisa
		Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
		return new CategoryDTO(entity);
	}
	
	@Transactional
	public CategoryDTO include(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.getName());
		//O save nao eh void
		entity = repository.save(entity);
		return new CategoryDTO(entity);
	}
	
	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		try {
			//getOne: não mexe no banco de dados e cria uma entity provisoria
			Category entity = repository.getOne(id);
			entity.setName(dto.getName());
			entity = repository.save(entity);
			return new CategoryDTO(entity);
		}
		catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id "+id+" not found ");
		}
	}
	
	//Nao usar o Transactional em operacoes delete
	public void delete(Long id) {
		try{
			repository.deleteById(id);
		}
		//Caso o id nao seja encontrado
		catch(EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id "+id+" not found");
		}
		//Caso a tabela tenha relacionamento de um pra muitos
		catch(DataIntegrityViolationException e) {
			throw new DatabaseIntegrityException("Integrity violation");
		}
	}
}
