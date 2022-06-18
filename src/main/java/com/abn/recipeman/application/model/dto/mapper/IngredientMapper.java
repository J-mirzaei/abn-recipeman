package com.abn.recipeman.application.model.dto.mapper;

import com.abn.recipeman.application.model.dto.IngredientDTO;
import com.abn.recipeman.application.model.dto.RecipeIngredientDTO;
import com.abn.recipeman.domain.model.entity.Ingredient;
import com.abn.recipeman.domain.model.entity.RecipeIngredient;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link Ingredient} and its DTO {@link IngredientDTO}.
 */
@Mapper(componentModel = "spring")
public interface IngredientMapper extends EntityMapper<IngredientDTO, Ingredient> {}

