package com.devsuperior.dscatalog.resources;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.services.CategoryService;

//Classe da camada de controle
//Usar a annotation RestController
//RequestMapping - o argumento value recebe a url
@RestController
@RequestMapping(value = "/categories")
public class CategoryResource {
	
	@Autowired
	private CategoryService service;
	
	@GetMapping
	public ResponseEntity<List<CategoryDTO>> findAll(){
		List<CategoryDTO> list = service.findAll();
		//ok: resposta 200
		return ResponseEntity.ok().body(list);
	}
	
	//A url recebe o id como argumento. O parametro do metodo precisa ter o mesmo nome e a annotation PathVariable
	@GetMapping(value = "/{id}")
	public ResponseEntity<CategoryDTO> findById(@PathVariable Long id){
		CategoryDTO dto = service.findById(id);
		return ResponseEntity.ok().body(dto);
	}
	
	//Para inserir, é o POST, usando o PostMapping
	@PostMapping
	public ResponseEntity<CategoryDTO> include(@RequestBody CategoryDTO dto){
		dto = service.include(dto);
		//Comando para retornar a resposta 201 com a informação do que foi inserido
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
		//created: resposta 201
		return ResponseEntity.created(uri).body(dto);
	}
	
	//Para atualizar
	@PutMapping(value = "/{id}")
	public ResponseEntity<CategoryDTO> include(@PathVariable Long id, @RequestBody CategoryDTO dto){
		dto = service.update(id, dto);
		return ResponseEntity.ok().body(dto);
	}
}
