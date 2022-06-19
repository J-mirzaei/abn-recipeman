package com.abn.recipeman.application.model.dto.mapper;

import com.abn.recipeman.application.model.dto.IngredientDTO;
import com.abn.recipeman.domain.model.entity.Ingredient;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Ingredient} and its DTO {@link IngredientDTO}.
 */
@Mapper(componentModel = "spring")
public interface IngredientMapper extends EntityMapper<IngredientDTO, Ingredient> {
}

