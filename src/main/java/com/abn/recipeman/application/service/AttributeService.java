package com.abn.recipeman.application.service;

import com.abn.recipeman.application.model.dto.AttributeDTO;
import com.abn.recipeman.application.model.dto.mapper.AttributeMapper;
import com.abn.recipeman.domain.model.entity.Attribute;
import com.abn.recipeman.persistence.repository.AttributeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link com.abn.recipeman.domain.model.entity.Attribute}.
 */
@Service
@Transactional
public class AttributeService {

    private final Logger log = LoggerFactory.getLogger(AttributeService.class);

    private final AttributeRepository attributeRepository;

    private final AttributeMapper attributeMapper;

    public AttributeService(AttributeRepository attributeRepository, AttributeMapper attributeMapper) {
        this.attributeRepository = attributeRepository;
        this.attributeMapper = attributeMapper;
    }

    /**
     * Save a attribute.
     *
     * @param attributeDTO the entity to save.
     * @return the persisted entity.
     */
    public AttributeDTO save(AttributeDTO attributeDTO) {
        log.debug("Request to save Attribute : {}", attributeDTO);
        Attribute attribute = attributeMapper.toEntity(attributeDTO);
        attribute = attributeRepository.save(attribute);
        return attributeMapper.toDto(attribute);
    }

    /**
     * Update a attribute.
     *
     * @param attributeDTO the entity to save.
     * @return the persisted entity.
     */
    public AttributeDTO update(AttributeDTO attributeDTO) {
        log.debug("Request to save Attribute : {}", attributeDTO);
        Attribute attribute = attributeMapper.toEntity(attributeDTO);
        attribute = attributeRepository.save(attribute);
        return attributeMapper.toDto(attribute);
    }

    /**
     * Partially update a attribute.
     *
     * @param attributeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AttributeDTO> partialUpdate(AttributeDTO attributeDTO) {
        log.debug("Request to partially update Attribute : {}", attributeDTO);

        return attributeRepository
                .findById(attributeDTO.getId())
                .map(existingAttribute -> {
                    attributeMapper.partialUpdate(existingAttribute, attributeDTO);

                    return existingAttribute;
                })
                .map(attributeRepository::save)
                .map(attributeMapper::toDto);
    }

    /**
     * Get all the attributes.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AttributeDTO> findAll() {
        log.debug("Request to get all Attributes");
        return attributeRepository.findAll().stream().map(attributeMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one attribute by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AttributeDTO> findOne(Long id) {
        log.debug("Request to get Attribute : {}", id);
        return attributeRepository.findById(id).map(attributeMapper::toDto);
    }

    /**
     * Delete the attribute by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Attribute : {}", id);
        attributeRepository.deleteById(id);
    }
}
