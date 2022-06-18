package com.abn.recipeman.application.model.dto.mapper;


import com.abn.recipeman.application.model.dto.AttributeDTO;
import com.abn.recipeman.domain.model.entity.Attribute;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link com.abn.recipeman.domain.model.entity.Attribute} and its DTO {@link AttributeDTO}.
 */
@Mapper(componentModel = "spring")
public interface AttributeMapper extends EntityMapper<AttributeDTO, Attribute> {}
