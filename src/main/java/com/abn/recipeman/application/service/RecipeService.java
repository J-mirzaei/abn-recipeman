package com.abn.recipeman.application.service;

import com.abn.recipeman.application.model.dto.FilterRecipeDto;
import com.abn.recipeman.application.model.dto.RecipeDTO;
import com.abn.recipeman.application.model.dto.mapper.RecipeMapper;
import com.abn.recipeman.domain.model.entity.Recipe;
import com.abn.recipeman.persistence.repository.AttributeRepository;
import com.abn.recipeman.persistence.repository.RecipeIngredientRepository;
import com.abn.recipeman.persistence.repository.RecipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link com.abn.recipeman.domain.model.entity.Recipe}.
 */
@Service
@Transactional
public class RecipeService {

    private final Logger log = LoggerFactory.getLogger(RecipeService.class);

    private final RecipeRepository recipeRepository;
    private final AttributeRepository attributeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;

    private final RecipeMapper recipeMapper;

    public RecipeService(RecipeRepository recipeRepository, AttributeRepository attributeRepository, RecipeIngredientRepository recipeIngredientRepository, RecipeMapper recipeMapper) {
        this.recipeRepository = recipeRepository;
        this.attributeRepository = attributeRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.recipeMapper = recipeMapper;
    }

    /**
     * Save a recipe.
     *
     * @param recipeDTO the entity to save.
     * @return the persisted entity.
     */
    public RecipeDTO save(RecipeDTO recipeDTO) {
        log.debug("Request to save Recipe : {}", recipeDTO);
        Recipe recipe = recipeMapper.toEntity(recipeDTO);
        recipe = recipeRepository.save(recipe);
        return recipeMapper.toDto(recipe);
    }

    /**
     * Update a recipe.
     *
     * @param recipeDTO the entity to save.
     * @return the persisted entity.
     */
    public RecipeDTO update(RecipeDTO recipeDTO) {
        log.debug("Request to save Recipe : {}", recipeDTO);
        Recipe recipe = recipeMapper.toEntity(recipeDTO);
        recipe = recipeRepository.save(recipe);
        return recipeMapper.toDto(recipe);
    }

    /**
     * Partially update a recipe.
     *
     * @param recipeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RecipeDTO> partialUpdate(RecipeDTO recipeDTO) {
        log.debug("Request to partially update Recipe : {}", recipeDTO);

        return recipeRepository
                .findById(recipeDTO.getId())
                .map(existingRecipe -> {
                    recipeMapper.partialUpdate(existingRecipe, recipeDTO);

                    return existingRecipe;
                })
                .map(recipeRepository::save)
                .map(recipeMapper::toDto);
    }

    /**
     * Get all the recipes.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<RecipeDTO> findAll() {
        log.debug("Request to get all Recipes");
        return recipeRepository
                .findAllWithEagerRelationships()
                .stream()
                .map(recipeMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the recipes with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<RecipeDTO> findAllWithEagerRelationships(Pageable pageable) {
        return recipeRepository.findAllWithEagerRelationships(pageable).map(recipeMapper::toDto);
    }

    /**
     * Get one recipe by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RecipeDTO> findOne(Long id) {
        log.debug("Request to get Recipe : {}", id);
        return recipeRepository.findOneWithEagerRelationships(id).map(recipeMapper::toDto);
    }

    /**
     * Delete the recipe by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Recipe : {}", id);
        recipeRepository.deleteById(id);
    }

    /**
     * Get all the recipes.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<RecipeDTO> inquiryRecipes(FilterRecipeDto filterRecipeDto, Pageable pageable) {
        log.debug("Request to inquiry Recipes");
        Set<Long> filteredRecipeIds = new HashSet<>();
        if (filterRecipeDto.getAttributes() != null && filterRecipeDto.getAttributes().size() > 0) {
            filteredRecipeIds.addAll(attributeRepository.findAllByRecipeAttributeTypeIn(filterRecipeDto.getAttributes()).stream().flatMap(att -> att.getRecipes().stream().map(Recipe::getId)).collect(Collectors.toSet()));
        }
        if (filterRecipeDto.getExcludeIngredients() != null && filterRecipeDto.getExcludeIngredients().size() > 0) {
            filteredRecipeIds.addAll(recipeIngredientRepository.findAllByIngredientNames(filterRecipeDto.getExcludeIngredients()).stream().map(recIng -> recIng.getRecipe().getId()).collect(Collectors.toSet()));
        }
        if (filterRecipeDto.getIncludeIngredients() != null && filterRecipeDto.getIncludeIngredients().size() > 0) {
            filteredRecipeIds.addAll(recipeIngredientRepository.findAllByIngredientNames(filterRecipeDto.getIncludeIngredients()).stream().map(recIng -> recIng.getRecipe().getId()).collect(Collectors.toSet()));
        }

        return recipeRepository
                .inquiryAllByIdsWithEagerRelationships(
                        filteredRecipeIds.isEmpty() ? null: filteredRecipeIds,
                        filterRecipeDto.getServingNumber(),
                        filterRecipeDto.getFilterInstruction(),
                        pageable
                )
                .stream()
                .map(recipeMapper::toDto)
                .collect(Collectors.toCollection(LinkedList::new));
    }

}
