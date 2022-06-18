package com.abn.recipeman.application.model.dto.mapper;

import com.abn.recipeman.application.model.dto.AttributeDTO;
import com.abn.recipeman.application.model.dto.RecipeDTO;
import com.abn.recipeman.domain.model.entity.Attribute;
import com.abn.recipeman.domain.model.entity.Recipe;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for the entity {@link Recipe} and its DTO {@link RecipeDTO}.
 */
@Mapper(componentModel = "spring")
public interface RecipeMapper extends EntityMapper<RecipeDTO, Recipe> {
    @Mapping(target = "attributes", source = "attributes", qualifiedByName = "attributeIdSet")
    RecipeDTO toDto(Recipe s);

    @Mapping(target = "removeAttribute", ignore = true)
    Recipe toEntity(RecipeDTO recipeDTO);

    @Named("attributeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AttributeDTO toDtoAttributeId(Attribute attribute);

    @Named("attributeIdSet")
    default Set<AttributeDTO> toDtoAttributeIdSet(Set<Attribute> attribute) {
        return attribute.stream().map(this::toDtoAttributeId).collect(Collectors.toSet());
    }
}
