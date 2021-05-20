package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
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
	public List<CategoryDTO> findAll(){
		List<Category> list = repository.findAll();
		return list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
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
			throw new ResourceNotFoundException("Id not found "+id);
		}
	}
}
