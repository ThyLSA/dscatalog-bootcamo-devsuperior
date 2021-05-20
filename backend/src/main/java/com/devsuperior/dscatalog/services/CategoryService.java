package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;

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
		Category entity = obj.orElseThrow(() -> new EntityNotFoundException("Entity not found"));
		return new CategoryDTO(entity);
	}
}
