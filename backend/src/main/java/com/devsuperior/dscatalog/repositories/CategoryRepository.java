package com.devsuperior.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsuperior.dscatalog.entities.Category;

//Classe da camada de acesso a dados
//Usar a annotation Repository e extender JpaRepository, passando Entity e o tipo da primary_key
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{

}
