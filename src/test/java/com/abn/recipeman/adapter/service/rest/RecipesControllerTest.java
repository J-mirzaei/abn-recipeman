package com.abn.recipeman.adapter.service.rest;

import com.abn.recipeman.IntegrationTest;
import com.abn.recipeman.application.model.dto.FilterRecipeDto;
import com.abn.recipeman.application.model.dto.RecipeDTO;
import com.abn.recipeman.application.model.dto.RecipeIngredientDTO;
import com.abn.recipeman.application.model.dto.mapper.RecipeMapper;
import com.abn.recipeman.application.service.RecipeAttributeType;
import com.abn.recipeman.application.service.RecipeIngredientService;
import com.abn.recipeman.application.service.RecipeService;
import com.abn.recipeman.domain.model.entity.Attribute;
import com.abn.recipeman.domain.model.entity.Ingredient;
import com.abn.recipeman.domain.model.entity.Recipe;
import com.abn.recipeman.domain.model.entity.RecipeIngredient;
import com.abn.recipeman.persistence.repository.AttributeRepository;
import com.abn.recipeman.persistence.repository.RecipeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@IntegrationTest
class RecipesControllerTest {

    private static final String DEFAULT_NAME = "RECIPE DEFAULT NAME";
    private static final String UPDATED_NAME = "RECIPE UPDATE NAME";

    private static final Integer DEFAULT_SERVING_NUMBER = 1;
    private static final Integer UPDATED_SERVING_NUMBER = 2;

    private static final String DEFAULT_INSTRUCTION = "RECIPE DEFAULT INSTRUCTION";
    private static final String UPDATED_INSTRUCTION = "RECIPE UPDATED INSTRUCTION";

    private static final String ENTITY_API_URL = "/api/recipes";
    private static final String INQUIRY_API_URL = "/api/recipes/inquiry";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String APPLE_PIE_INSTRUCTION = "To make the crust: In a medium bowl, whisk together the flour and salt.\n" +
            "\n" +
            "Work in the shortening until the mixture is evenly crumbly. Dice the butter into 1/2\" pieces, and cut into the mixture until you have flakes of butter the size of a dime.\n" +
            "\n" +
            "Add the water 2 tablespoons at a time, mixing with a fork as you sprinkle the water into the dough. When the dough is moist enough to hold together when you squeeze it, transfer it to a piece of wax or parchment paper. It's OK if there are some dry spots in the pile. Use a spray bottle of water to lightly spritz these places; that way you'll add just enough water to bring the dough together without creating a wet spot.\n" +
            "\n" +
            "Fold the dough over on itself three or four times to bring it together, then divide it into two pieces, one about twice as large as the other. The larger piece will be the bottom crust; the smaller piece, the top crust. Pat each piece of dough into a disk about 3/4\" thick.\n" +
            "\n" +
            "Perfect your technique\n" +
            "Apple Pie Bakealong via @kingarthurflour\n" +
            "BLOG\n" +
            "Apple Pie Bakealong\n" +
            "BY PJ HAMEL\n" +
            "\n" +
            "Roll each disk on its edge, like a wheel, to smooth out the edges. This step will ensure your dough will roll out evenly, without a lot of cracks and splits at the edges later. Wrap in plastic and refrigerate for 30 minutes before rolling.\n" +
            "\n" +
            "Preheat the oven to 425°F.\n" +
            "\n" +
            "Lightly grease a 9\" pie pan that's at least 2\" deep. This will make serving the pie easier after it's baked.\n" +
            "\n" +
            "To make the filling: Combine the sliced apples and lemon juice in a large mixing bowl.\n" +
            "\n" +
            "In a small bowl, whisk together the sugar, flour, cornstarch, salt, and spices. Sprinkle the mixture over the apples, and stir to coat them. Stir in the boiled cider (or apple juice concentrate) and the vanilla.\n" +
            "\n" +
            "To assemble the pie: Roll the larger piece of pastry into a 13\" circle. Transfer it to the prepared pan, and trim the edges so they overlap the rim of the pan by an inch all the way around.\n" +
            "\n" +
            "Spoon the apple filling into the pan. Dot the top with the diced butter.\n" +
            "\n" +
            "Roll out the remaining pastry to an 11\" circle. Carefully place the pastry over the apples. Bring the overhanging bottom crust up and over the top crust, pinching to seal the two and making a decorative crimp. Prick the crust all over with a fork, to allow steam to escape. Or cut decorative vent holes, if desired. Alternatively, you can weave a lattice.\n" +
            "\n" +
            "For extra crunch and shine, brush the top crust with milk (or an egg white beaten with 1 tablespoon of water), and sprinkle with coarse sparkling sugar. Place the pie in the refrigerator for 10 minutes to firm up the crust while the oven finishes heating.\n" +
            "\n" +
            "Place the pie on a parchment-lined baking sheet. Bake the pie for 20 minutes, then reduce the oven temperature to 375°F and bake for 40 minutes more, until you see the filling bubbling inside the pie (and perhaps dripping onto the parchment). Check the pie after half an hour of baking time, and cover the edges with foil or a pie shield to keep them from browning too quickly, if necessary.\n" +
            "\n" +
            "When the pie is done — you should see the filling bubbling vigorously, either around the edges, or via any decorative vents — remove it from the oven.\n" +
            "\n" +
            "Cool the pie completely before slicing — really. Cutting any fruit pie that's still warm is a messy business. The filling continues to thicken as the pie cools, and if you cut it too soon it will run out all over the place. It's better to bake the pie in advance, cool it completely, then warm each slice as needed after it's been cut.\n" +
            "\n" +
            "Store any leftover pie, lightly covered, at room temperature for several days. Freeze for longer storage.\n" +
            "\n";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private AttributeRepository attributeRepository;
    @Autowired
    private RecipeIngredientService recipeIngredientService;

    @Mock
    private RecipeRepository recipeRepositoryMock;

    @Autowired
    private RecipeMapper recipeMapper;

    @Mock
    private RecipeService recipeServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRecipeMockMvc;

    private Recipe recipe;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Recipe createEntity(EntityManager em) {
        Recipe recipe = new Recipe().name(DEFAULT_NAME).servingNumber(DEFAULT_SERVING_NUMBER).instruction(DEFAULT_INSTRUCTION);
        return recipe;
    }

    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Recipe createUpdatedEntity(EntityManager em) {
        Recipe recipe = new Recipe().name(UPDATED_NAME).servingNumber(UPDATED_SERVING_NUMBER).instruction(UPDATED_INSTRUCTION);
        return recipe;
    }

    @BeforeEach
    public void initTest() {
        recipe = createEntity(em);
    }

    @Test
    @Transactional
    void filterRecipe() throws Exception {
        int databaseSizeBeforeCreate = recipeRepository.findAll().size();
        // Create the Recipe
        RecipeDTO recipeDTO = recipeMapper.toDto(recipe);
        restRecipeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipeDTO)))
                .andExpect(status().isCreated());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeCreate + 1);
        Recipe testRecipe = recipeList.get(recipeList.size() - 1);
        assertThat(testRecipe.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRecipe.getServingNumber()).isEqualTo(DEFAULT_SERVING_NUMBER);
        assertThat(testRecipe.getInstruction()).isEqualTo(DEFAULT_INSTRUCTION);
    }

    @Test
    @Transactional
    void createRecipeWithExistingId() throws Exception {
        // Create the Recipe with an existing ID
        recipe.setId(1L);
        RecipeDTO recipeDTO = recipeMapper.toDto(recipe);

        int databaseSizeBeforeCreate = recipeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecipeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipeDTO)))
                .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = recipeRepository.findAll().size();
        // set the field null
        recipe.setName(null);

        // Create the Recipe, which fails.
        RecipeDTO recipeDTO = recipeMapper.toDto(recipe);

        restRecipeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipeDTO)))
                .andExpect(status().isBadRequest());

        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkServingNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = recipeRepository.findAll().size();
        // set the field null
        recipe.setServingNumber(null);

        // Create the Recipe, which fails.
        RecipeDTO recipeDTO = recipeMapper.toDto(recipe);

        restRecipeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipeDTO)))
                .andExpect(status().isBadRequest());

        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRecipes() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipeList
        restRecipeMockMvc
                .perform(get(ENTITY_API_URL + "?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(recipe.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
                .andExpect(jsonPath("$.[*].servingNumber").value(hasItem(DEFAULT_SERVING_NUMBER)))
                .andExpect(jsonPath("$.[*].instruction").value(hasItem(DEFAULT_INSTRUCTION.toString())));
    }

    @SuppressWarnings({"unchecked"})
    void getAllRecipesWithEagerRelationshipsIsEnabled() throws Exception {
        when(recipeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRecipeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(recipeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    void getAllRecipesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(recipeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRecipeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(recipeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getRecipe() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get the recipe
        restRecipeMockMvc
                .perform(get(ENTITY_API_URL_ID, recipe.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(recipe.getId().intValue()))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
                .andExpect(jsonPath("$.servingNumber").value(DEFAULT_SERVING_NUMBER))
                .andExpect(jsonPath("$.instruction").value(DEFAULT_INSTRUCTION));
    }

    @Test
    @Transactional
    void getNonExistingRecipe() throws Exception {
        // Get the recipe
        restRecipeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewRecipe() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();

        // Update the recipe
        Recipe updatedRecipe = recipeRepository.findById(recipe.getId()).get();
        // Disconnect from session so that the updates on updatedRecipe are not directly saved in db
        em.detach(updatedRecipe);
        updatedRecipe.name(UPDATED_NAME).servingNumber(UPDATED_SERVING_NUMBER).instruction(UPDATED_INSTRUCTION);
        RecipeDTO recipeDTO = recipeMapper.toDto(updatedRecipe);

        restRecipeMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, recipeDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(recipeDTO))
                )
                .andExpect(status().isOk());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        Recipe testRecipe = recipeList.get(recipeList.size() - 1);
        assertThat(testRecipe.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRecipe.getServingNumber()).isEqualTo(UPDATED_SERVING_NUMBER);
        assertThat(testRecipe.getInstruction()).isEqualTo(UPDATED_INSTRUCTION);
    }

    @Test
    @Transactional
    void putNonExistingRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        recipe.setId(count.incrementAndGet());

        // Create the Recipe
        RecipeDTO recipeDTO = recipeMapper.toDto(recipe);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecipeMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, recipeDTO.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(recipeDTO))
                )
                .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        recipe.setId(count.incrementAndGet());

        // Create the Recipe
        RecipeDTO recipeDTO = recipeMapper.toDto(recipe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeMockMvc
                .perform(
                        put(ENTITY_API_URL_ID, count.incrementAndGet())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(TestUtil.convertObjectToJsonBytes(recipeDTO))
                )
                .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        recipe.setId(count.incrementAndGet());

        // Create the Recipe
        RecipeDTO recipeDTO = recipeMapper.toDto(recipe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeMockMvc
                .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(recipeDTO)))
                .andExpect(status().isMethodNotAllowed());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRecipeWithPatch() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();

        // Update the recipe using partial update
        Recipe partialUpdatedRecipe = new Recipe();
        partialUpdatedRecipe.setId(recipe.getId());

        partialUpdatedRecipe.name(UPDATED_NAME).servingNumber(UPDATED_SERVING_NUMBER);

        restRecipeMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedRecipe.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRecipe))
                )
                .andExpect(status().isOk());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        Recipe testRecipe = recipeList.get(recipeList.size() - 1);
        assertThat(testRecipe.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRecipe.getServingNumber()).isEqualTo(UPDATED_SERVING_NUMBER);
        assertThat(testRecipe.getInstruction()).isEqualTo(DEFAULT_INSTRUCTION);
    }

    @Test
    @Transactional
    void fullUpdateRecipeWithPatch() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();

        // Update the recipe using partial update
        Recipe partialUpdatedRecipe = new Recipe();
        partialUpdatedRecipe.setId(recipe.getId());

        partialUpdatedRecipe.name(UPDATED_NAME).servingNumber(UPDATED_SERVING_NUMBER).instruction(UPDATED_INSTRUCTION);

        restRecipeMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, partialUpdatedRecipe.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRecipe))
                )
                .andExpect(status().isOk());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
        Recipe testRecipe = recipeList.get(recipeList.size() - 1);
        assertThat(testRecipe.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRecipe.getServingNumber()).isEqualTo(UPDATED_SERVING_NUMBER);
        assertThat(testRecipe.getInstruction()).isEqualTo(UPDATED_INSTRUCTION);
    }

    @Test
    @Transactional
    void patchNonExistingRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        recipe.setId(count.incrementAndGet());

        // Create the Recipe
        RecipeDTO recipeDTO = recipeMapper.toDto(recipe);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecipeMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, recipeDTO.getId())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(recipeDTO))
                )
                .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        recipe.setId(count.incrementAndGet());

        // Create the Recipe
        RecipeDTO recipeDTO = recipeMapper.toDto(recipe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeMockMvc
                .perform(
                        patch(ENTITY_API_URL_ID, count.incrementAndGet())
                                .contentType("application/merge-patch+json")
                                .content(TestUtil.convertObjectToJsonBytes(recipeDTO))
                )
                .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRecipe() throws Exception {
        int databaseSizeBeforeUpdate = recipeRepository.findAll().size();
        recipe.setId(count.incrementAndGet());

        // Create the Recipe
        RecipeDTO recipeDTO = recipeMapper.toDto(recipe);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeMockMvc
                .perform(
                        patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(recipeDTO))
                )
                .andExpect(status().isMethodNotAllowed());

        // Validate the Recipe in the database
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRecipe() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        int databaseSizeBeforeDelete = recipeRepository.findAll().size();

        // Delete the recipe
        restRecipeMockMvc
                .perform(delete(ENTITY_API_URL_ID, recipe.getId()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Recipe> recipeList = recipeRepository.findAll();
        assertThat(recipeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    void inquiryRecipe() throws Exception {
        // Create the Recipe with an existing ID
        recipe.setInstruction(APPLE_PIE_INSTRUCTION);
        FilterRecipeDto filterRecipeDto = FilterRecipeDto.builder().filterInstruction("2 tablespoons").build();

        Recipe savedRecipe = recipeRepository.saveAndFlush(recipe);

        // An entity with an existing ID cannot be created, so this API call must fail
        MvcResult mvcResult = restRecipeMockMvc
                .perform(post(INQUIRY_API_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(filterRecipeDto))
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andReturn();

        // Validate the Recipe in the database
        List<RecipeDTO> recipeList = TestUtil.serialize(mvcResult.getResponse().getContentAsString(), new TypeReference<List<RecipeDTO>>() {
        });
        assertThat(recipeList
        ).hasSize(1);
        assertThat(recipeList.get(0).getId()).isEqualTo(savedRecipe.getId());
    }


    @Test
    @Transactional
    void inquiryRecipeNotExist() throws Exception {
        // Create the Recipe with an existing ID
        recipe.setInstruction(APPLE_PIE_INSTRUCTION);
        Attribute attribute = new Attribute();
//        attribute.setId(1L);
        attribute.setRecipeAttributeType(RecipeAttributeType.VEGETARIAN);
        recipe.setAttributes(Collections.singleton(attribute));
        FilterRecipeDto filterRecipeDto = FilterRecipeDto.builder().filterInstruction("JAFAR!!!").build();

        Recipe savedRecipe = recipeRepository.saveAndFlush(recipe);
//        Attribute savedAttribute = attributeRepository.save(attribute);

        // An entity with an existing ID cannot be created, so this API call must fail
        MvcResult mvcResult = restRecipeMockMvc
                .perform(post(INQUIRY_API_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(filterRecipeDto))
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andReturn();

        // Validate the Recipe in the database
        List<RecipeDTO> recipeList = TestUtil.serialize(mvcResult.getResponse().getContentAsString(), new TypeReference<List<RecipeDTO>>() {
        });
        assertThat(recipeList
        ).hasSize(0);
    }

    @Test
    @Transactional
    void inquiryRecipe_is_vegetarian() throws Exception {
        // Create the Recipe with an existing ID
        recipe.setInstruction(DEFAULT_INSTRUCTION);
        Attribute attribute = new Attribute();
        attribute.setRecipeAttributeType(RecipeAttributeType.VEGETARIAN);
        recipe.setAttributes(Collections.singleton(attribute));

        Recipe savedRecipe = recipeRepository.saveAndFlush(recipe);
        FilterRecipeDto filterRecipeDto = FilterRecipeDto.builder().attributes(Set.of(RecipeAttributeType.VEGETARIAN)).build();

        MvcResult mvcResult = restRecipeMockMvc
                .perform(post(INQUIRY_API_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(filterRecipeDto))
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andReturn();

        // Validate the Recipe in the database
        List<RecipeDTO> recipeList = TestUtil.serialize(mvcResult.getResponse().getContentAsString(), new TypeReference<List<RecipeDTO>>() {
        });
        assertThat(recipeList
        ).hasSize(1);
        assertThat(recipeList.get(0).getId()).isEqualTo(savedRecipe.getId());
        assertThat(recipeList.get(0).getAttributes().size()).isEqualTo(1);
    }
    @Test
    @Transactional
    void inquiryRecipe_with_ingradient() throws Exception {
        // Create the Recipe with an existing ID
        recipe.setInstruction(DEFAULT_INSTRUCTION);
        Ingredient ingredient = new Ingredient();
        ingredient.setName("rice");
        RecipeIngredient recipeIngredient=new RecipeIngredient();
        recipeIngredient.setAmount(100);
        recipeIngredient.setIngredient(ingredient);
        recipe.setRecipeIngredients(Set.of(recipeIngredient));

        Recipe savedRecipe = recipeRepository.saveAndFlush(recipe);
        FilterRecipeDto filterRecipeDto = FilterRecipeDto.builder().includeIngredients(Set.of(ingredient.getName())).build();

        MvcResult mvcResult = restRecipeMockMvc
                .perform(post(INQUIRY_API_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(filterRecipeDto))
                        .param("page", "0")
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andReturn();

        List<RecipeDTO> recipeList = TestUtil.serialize(mvcResult.getResponse().getContentAsString(), new TypeReference<List<RecipeDTO>>() {
        });
        assertThat(recipeList
        ).hasSize(1);
        assertThat(recipeList.get(0).getId()).isEqualTo(savedRecipe.getId());
        Recipe recipe1 = new Recipe();
        List<RecipeIngredientDTO> byRecipeId = recipeIngredientService.findByRecipeId(savedRecipe.getId());
        assertThat(byRecipeId.size()).isEqualTo(1);
    }
}