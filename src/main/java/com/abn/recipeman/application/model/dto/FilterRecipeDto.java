package com.abn.recipeman.application.model.dto;

import com.abn.recipeman.application.service.RecipeAttributeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * A DTO for the {@link com.abn.recipeman.domain.model.entity.RecipeIngredient} entity.
 */
@Builder
@Data
@Schema(name = "FilterRecipeDto", description = "The filter object to inquiry Recipes")
public class FilterRecipeDto implements Serializable {
    @Schema(name = "vegetarian", description = "is vegetarian")
    private Set<RecipeAttributeType> attributes;
    private Integer servingNumber;
    private Set<String> includeIngredients;
    private Set<String> excludeIngredients;
    private String filterInstruction;

}
