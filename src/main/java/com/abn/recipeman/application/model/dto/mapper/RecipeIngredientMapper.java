package com.abn.recipeman.application.model.dto.mapper;

import com.abn.recipeman.application.model.dto.IngredientDTO;
import com.abn.recipeman.application.model.dto.RecipeDTO;
import com.abn.recipeman.application.model.dto.RecipeIngredientDTO;
import com.abn.recipeman.domain.model.entity.Ingredient;
import com.abn.recipeman.domain.model.entity.Recipe;
import com.abn.recipeman.domain.model.entity.RecipeIngredient;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link RecipeIngredient} and its DTO {@link RecipeIngredientDTO}.
 */
@Mapper(componentModel = "spring")
public interface RecipeIngredientMapper extends EntityMapper<RecipeIngredientDTO, RecipeIngredient> {
    @Mapping(target = "ingredient", source = "ingredient", qualifiedByName = "ingredientId")
    @Mapping(target = "recipe", source = "recipe", qualifiedByName = "recipeId")
    RecipeIngredientDTO toDto(RecipeIngredient s);

    @Named("ingredientId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    IngredientDTO toDtoIngredientId(Ingredient ingredient);

    @Named("recipeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RecipeDTO toDtoRecipeId(Recipe recipe);
}

