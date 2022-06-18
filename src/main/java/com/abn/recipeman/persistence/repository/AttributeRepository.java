package com.abn.recipeman.persistence.repository;

import com.abn.recipeman.application.service.RecipeAttributeType;
import com.abn.recipeman.domain.model.entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Spring Data SQL repository for the Attribute entity.
 */
@Repository
public interface AttributeRepository extends AttributeRepositoryWithBagRelationships, JpaRepository<Attribute, Long> {
    List<Attribute> findAllByRecipeAttributeType(String RecipeAttributeType);

    List<Attribute> findAllByRecipeAttributeTypeIn(Set<RecipeAttributeType> names);
}
