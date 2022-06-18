package com.abn.recipeman.adapter.service.rest;

import com.abn.recipeman.IntegrationTest;
import com.abn.recipeman.application.model.dto.AttributeDTO;
import com.abn.recipeman.application.model.dto.mapper.AttributeMapper;
import com.abn.recipeman.application.service.RecipeAttributeType;
import com.abn.recipeman.domain.model.entity.Attribute;
import com.abn.recipeman.persistence.repository.AttributeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@IntegrationTest
class AttributeControllerTest {

    private static final RecipeAttributeType DEFAULT_NAME = RecipeAttributeType.VEGETARIAN;
    private static final RecipeAttributeType UPDATED_NAME = RecipeAttributeType.HILAL;

    private static final String ENTITY_API_URL = "/api/attributes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private AttributeMapper attributeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAttributeMockMvc;

    private Attribute attribute;

    /**
     * Create a default entity for this test.
     */
    public static Attribute createEntity(EntityManager em) {
        Attribute attribute = new Attribute().recipeAttributeType(DEFAULT_NAME);
        return attribute;
    }

    /**
     * Create an updated entity for this test.
     */
    public static Attribute createUpdatedEntity(EntityManager em) {
        Attribute attribute = new Attribute().recipeAttributeType(UPDATED_NAME);
        return attribute;
    }

    @BeforeEach
    public void initTest() {
        attribute = createEntity(em);
    }

    @Test
    @Transactional
    void createAttribute() throws Exception {
        int databaseSizeBeforeCreate = attributeRepository.findAll().size();
        // Create the Attribute
        AttributeDTO attributeDTO = attributeMapper.toDto(attribute);
        restAttributeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(attributeDTO)))
                .andExpect(status().isCreated());

        // Validate the Attribute in the database
        List<Attribute> attributeList = attributeRepository.findAll();
        assertThat(attributeList).hasSize(databaseSizeBeforeCreate + 1);
        Attribute testAttribute = attributeList.get(attributeList.size() - 1);
        assertThat(testAttribute.getRecipeAttributeType()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createAttributeWithExistingId() throws Exception {
        // Create the Attribute with an existing ID
        attribute.setId(1L);
        AttributeDTO attributeDTO = attributeMapper.toDto(attribute);

        int databaseSizeBeforeCreate = attributeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAttributeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(attributeDTO)))
                .andExpect(status().isBadRequest());

        // Validate the Attribute in the database
        List<Attribute> attributeList = attributeRepository.findAll();
        assertThat(attributeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = attributeRepository.findAll().size();
        // set the field null
        attribute.setRecipeAttributeType(null);

        // Create the Attribute, which fails.
        AttributeDTO attributeDTO = attributeMapper.toDto(attribute);

        restAttributeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(attributeDTO)))
                .andExpect(status().isBadRequest());

        List<Attribute> attributeList = attributeRepository.findAll();
        assertThat(attributeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAttributes() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get all the attributeList
        restAttributeMockMvc
                .perform(get(ENTITY_API_URL + "?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(attribute.getId().intValue())))
                .andExpect(jsonPath("$.[*].recipeAttributeType").value(hasItem(DEFAULT_NAME.name())));
    }

    @Test
    @Transactional
    void getAttribute() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        // Get the attribute
        restAttributeMockMvc
                .perform(get(ENTITY_API_URL_ID, attribute.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(attribute.getId().intValue()))
                .andExpect(jsonPath("$.recipeAttributeType").value(DEFAULT_NAME.name()));
    }

    @Test
    @Transactional
    void getNonExistingAttribute() throws Exception {
        // Get the attribute
        restAttributeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAttribute() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        int databaseSizeBeforeUpdate = attributeRepository.findAll().size();

        // Update the attribute
        Attribute updatedAttribute = attributeRepository.findById(attribute.getId()).get();
        // Disconnect from session so that the updates on updatedAttribute are not directly saved in db
        em.detach(updatedAttribute);
        updatedAttribute.recipeAttributeType(UPDATED_NAME);
        AttributeDTO attributeDTO = attributeMapper.toDto(updatedAttribute);

        restAttributeMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, attributeDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(attributeDTO))
                )
                .andExpect(status().isOk());

        // Validate the Attribute in the database
        List<Attribute> attributeList = attributeRepository.findAll();
        assertThat(attributeList).hasSize(databaseSizeBeforeUpdate);
        Attribute testAttribute = attributeList.get(attributeList.size() - 1);
        assertThat(testAttribute.getRecipeAttributeType()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingAttribute() throws Exception {
        int databaseSizeBeforeUpdate = attributeRepository.findAll().size();
        attribute.setId(count.incrementAndGet());

        // Create the Attribute
        AttributeDTO attributeDTO = attributeMapper.toDto(attribute);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAttributeMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, attributeDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(attributeDTO))
                )
                .andExpect(status().isBadRequest());

        // Validate the Attribute in the database
        List<Attribute> attributeList = attributeRepository.findAll();
        assertThat(attributeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAttribute() throws Exception {
        int databaseSizeBeforeUpdate = attributeRepository.findAll().size();
        attribute.setId(count.incrementAndGet());

        // Create the Attribute
        AttributeDTO attributeDTO = attributeMapper.toDto(attribute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttributeMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, count.incrementAndGet())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(attributeDTO))
                )
                .andExpect(status().isBadRequest());

        // Validate the Attribute in the database
        List<Attribute> attributeList = attributeRepository.findAll();
        assertThat(attributeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAttribute() throws Exception {
        int databaseSizeBeforeUpdate = attributeRepository.findAll().size();
        attribute.setId(count.incrementAndGet());

        // Create the Attribute
        AttributeDTO attributeDTO = attributeMapper.toDto(attribute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttributeMockMvc
                .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(attributeDTO)))
                .andExpect(status().isMethodNotAllowed());

        // Validate the Attribute in the database
        List<Attribute> attributeList = attributeRepository.findAll();
        assertThat(attributeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAttributeWithPatch() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        int databaseSizeBeforeUpdate = attributeRepository.findAll().size();

        // Update the attribute using partial update
        Attribute partialUpdatedAttribute = new Attribute();
        partialUpdatedAttribute.setId(attribute.getId());

        partialUpdatedAttribute.recipeAttributeType(UPDATED_NAME);

        restAttributeMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedAttribute.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAttribute))
                )
                .andExpect(status().isOk());

        // Validate the Attribute in the database
        List<Attribute> attributeList = attributeRepository.findAll();
        assertThat(attributeList).hasSize(databaseSizeBeforeUpdate);
        Attribute testAttribute = attributeList.get(attributeList.size() - 1);
        assertThat(testAttribute.getRecipeAttributeType()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void fullUpdateAttributeWithPatch() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        int databaseSizeBeforeUpdate = attributeRepository.findAll().size();

        // Update the attribute using partial update
        Attribute partialUpdatedAttribute = new Attribute();
        partialUpdatedAttribute.setId(attribute.getId());

        partialUpdatedAttribute.recipeAttributeType(UPDATED_NAME);

        restAttributeMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedAttribute.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAttribute))
                )
                .andExpect(status().isOk());

        // Validate the Attribute in the database
        List<Attribute> attributeList = attributeRepository.findAll();
        assertThat(attributeList).hasSize(databaseSizeBeforeUpdate);
        Attribute testAttribute = attributeList.get(attributeList.size() - 1);
        assertThat(testAttribute.getRecipeAttributeType()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingAttribute() throws Exception {
        int databaseSizeBeforeUpdate = attributeRepository.findAll().size();
        attribute.setId(count.incrementAndGet());

        // Create the Attribute
        AttributeDTO attributeDTO = attributeMapper.toDto(attribute);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAttributeMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, attributeDTO.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(attributeDTO))
                )
                .andExpect(status().isBadRequest());

        // Validate the Attribute in the database
        List<Attribute> attributeList = attributeRepository.findAll();
        assertThat(attributeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAttribute() throws Exception {
        int databaseSizeBeforeUpdate = attributeRepository.findAll().size();
        attribute.setId(count.incrementAndGet());

        // Create the Attribute
        AttributeDTO attributeDTO = attributeMapper.toDto(attribute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttributeMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, count.incrementAndGet())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(attributeDTO))
                )
                .andExpect(status().isBadRequest());

        // Validate the Attribute in the database
        List<Attribute> attributeList = attributeRepository.findAll();
        assertThat(attributeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAttribute() throws Exception {
        int databaseSizeBeforeUpdate = attributeRepository.findAll().size();
        attribute.setId(count.incrementAndGet());

        // Create the Attribute
        AttributeDTO attributeDTO = attributeMapper.toDto(attribute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttributeMockMvc
                .perform(
                        patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(attributeDTO))
                )
                .andExpect(status().isMethodNotAllowed());

        // Validate the Attribute in the database
        List<Attribute> attributeList = attributeRepository.findAll();
        assertThat(attributeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAttribute() throws Exception {
        // Initialize the database
        attributeRepository.saveAndFlush(attribute);

        int databaseSizeBeforeDelete = attributeRepository.findAll().size();

        // Delete the attribute
        restAttributeMockMvc
                .perform(delete(ENTITY_API_URL_ID, attribute.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Attribute> attributeList = attributeRepository.findAll();
        assertThat(attributeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}