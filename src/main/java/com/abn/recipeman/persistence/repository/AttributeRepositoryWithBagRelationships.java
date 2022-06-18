package com.abn.recipeman.persistence.repository;

import com.abn.recipeman.domain.model.entity.Attribute;
import com.abn.recipeman.domain.model.entity.Recipe;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface AttributeRepositoryWithBagRelationships {
    Optional<Attribute> fetchBagRelationships(Optional<Attribute> attributes);

    List<Attribute> fetchBagRelationships(List<Attribute> attributes);

    Page<Attribute> fetchBagRelationships(Page<Attribute> attributes);
}
