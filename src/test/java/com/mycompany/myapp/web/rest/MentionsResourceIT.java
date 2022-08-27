package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Mentions;
import com.mycompany.myapp.repository.MentionsRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link MentionsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MentionsResourceIT {

    private static final String DEFAULT_HANDLE = "AAAAAAAAAA";
    private static final String UPDATED_HANDLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mentions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MentionsRepository mentionsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMentionsMockMvc;

    private Mentions mentions;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mentions createEntity(EntityManager em) {
        Mentions mentions = new Mentions().handle(DEFAULT_HANDLE);
        return mentions;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mentions createUpdatedEntity(EntityManager em) {
        Mentions mentions = new Mentions().handle(UPDATED_HANDLE);
        return mentions;
    }

    @BeforeEach
    public void initTest() {
        mentions = createEntity(em);
    }

    @Test
    @Transactional
    void createMentions() throws Exception {
        int databaseSizeBeforeCreate = mentionsRepository.findAll().size();
        // Create the Mentions
        restMentionsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mentions)))
            .andExpect(status().isCreated());

        // Validate the Mentions in the database
        List<Mentions> mentionsList = mentionsRepository.findAll();
        assertThat(mentionsList).hasSize(databaseSizeBeforeCreate + 1);
        Mentions testMentions = mentionsList.get(mentionsList.size() - 1);
        assertThat(testMentions.getHandle()).isEqualTo(DEFAULT_HANDLE);
    }

    @Test
    @Transactional
    void createMentionsWithExistingId() throws Exception {
        // Create the Mentions with an existing ID
        mentions.setId(1L);

        int databaseSizeBeforeCreate = mentionsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMentionsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mentions)))
            .andExpect(status().isBadRequest());

        // Validate the Mentions in the database
        List<Mentions> mentionsList = mentionsRepository.findAll();
        assertThat(mentionsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkHandleIsRequired() throws Exception {
        int databaseSizeBeforeTest = mentionsRepository.findAll().size();
        // set the field null
        mentions.setHandle(null);

        // Create the Mentions, which fails.

        restMentionsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mentions)))
            .andExpect(status().isBadRequest());

        List<Mentions> mentionsList = mentionsRepository.findAll();
        assertThat(mentionsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMentions() throws Exception {
        // Initialize the database
        mentionsRepository.saveAndFlush(mentions);

        // Get all the mentionsList
        restMentionsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mentions.getId().intValue())))
            .andExpect(jsonPath("$.[*].handle").value(hasItem(DEFAULT_HANDLE)));
    }

    @Test
    @Transactional
    void getMentions() throws Exception {
        // Initialize the database
        mentionsRepository.saveAndFlush(mentions);

        // Get the mentions
        restMentionsMockMvc
            .perform(get(ENTITY_API_URL_ID, mentions.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(mentions.getId().intValue()))
            .andExpect(jsonPath("$.handle").value(DEFAULT_HANDLE));
    }

    @Test
    @Transactional
    void getNonExistingMentions() throws Exception {
        // Get the mentions
        restMentionsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewMentions() throws Exception {
        // Initialize the database
        mentionsRepository.saveAndFlush(mentions);

        int databaseSizeBeforeUpdate = mentionsRepository.findAll().size();

        // Update the mentions
        Mentions updatedMentions = mentionsRepository.findById(mentions.getId()).get();
        // Disconnect from session so that the updates on updatedMentions are not directly saved in db
        em.detach(updatedMentions);
        updatedMentions.handle(UPDATED_HANDLE);

        restMentionsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMentions.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedMentions))
            )
            .andExpect(status().isOk());

        // Validate the Mentions in the database
        List<Mentions> mentionsList = mentionsRepository.findAll();
        assertThat(mentionsList).hasSize(databaseSizeBeforeUpdate);
        Mentions testMentions = mentionsList.get(mentionsList.size() - 1);
        assertThat(testMentions.getHandle()).isEqualTo(UPDATED_HANDLE);
    }

    @Test
    @Transactional
    void putNonExistingMentions() throws Exception {
        int databaseSizeBeforeUpdate = mentionsRepository.findAll().size();
        mentions.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMentionsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, mentions.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(mentions))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mentions in the database
        List<Mentions> mentionsList = mentionsRepository.findAll();
        assertThat(mentionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMentions() throws Exception {
        int databaseSizeBeforeUpdate = mentionsRepository.findAll().size();
        mentions.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMentionsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(mentions))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mentions in the database
        List<Mentions> mentionsList = mentionsRepository.findAll();
        assertThat(mentionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMentions() throws Exception {
        int databaseSizeBeforeUpdate = mentionsRepository.findAll().size();
        mentions.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMentionsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(mentions)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Mentions in the database
        List<Mentions> mentionsList = mentionsRepository.findAll();
        assertThat(mentionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMentionsWithPatch() throws Exception {
        // Initialize the database
        mentionsRepository.saveAndFlush(mentions);

        int databaseSizeBeforeUpdate = mentionsRepository.findAll().size();

        // Update the mentions using partial update
        Mentions partialUpdatedMentions = new Mentions();
        partialUpdatedMentions.setId(mentions.getId());

        partialUpdatedMentions.handle(UPDATED_HANDLE);

        restMentionsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMentions.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMentions))
            )
            .andExpect(status().isOk());

        // Validate the Mentions in the database
        List<Mentions> mentionsList = mentionsRepository.findAll();
        assertThat(mentionsList).hasSize(databaseSizeBeforeUpdate);
        Mentions testMentions = mentionsList.get(mentionsList.size() - 1);
        assertThat(testMentions.getHandle()).isEqualTo(UPDATED_HANDLE);
    }

    @Test
    @Transactional
    void fullUpdateMentionsWithPatch() throws Exception {
        // Initialize the database
        mentionsRepository.saveAndFlush(mentions);

        int databaseSizeBeforeUpdate = mentionsRepository.findAll().size();

        // Update the mentions using partial update
        Mentions partialUpdatedMentions = new Mentions();
        partialUpdatedMentions.setId(mentions.getId());

        partialUpdatedMentions.handle(UPDATED_HANDLE);

        restMentionsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMentions.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMentions))
            )
            .andExpect(status().isOk());

        // Validate the Mentions in the database
        List<Mentions> mentionsList = mentionsRepository.findAll();
        assertThat(mentionsList).hasSize(databaseSizeBeforeUpdate);
        Mentions testMentions = mentionsList.get(mentionsList.size() - 1);
        assertThat(testMentions.getHandle()).isEqualTo(UPDATED_HANDLE);
    }

    @Test
    @Transactional
    void patchNonExistingMentions() throws Exception {
        int databaseSizeBeforeUpdate = mentionsRepository.findAll().size();
        mentions.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMentionsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, mentions.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(mentions))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mentions in the database
        List<Mentions> mentionsList = mentionsRepository.findAll();
        assertThat(mentionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMentions() throws Exception {
        int databaseSizeBeforeUpdate = mentionsRepository.findAll().size();
        mentions.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMentionsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(mentions))
            )
            .andExpect(status().isBadRequest());

        // Validate the Mentions in the database
        List<Mentions> mentionsList = mentionsRepository.findAll();
        assertThat(mentionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMentions() throws Exception {
        int databaseSizeBeforeUpdate = mentionsRepository.findAll().size();
        mentions.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMentionsMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(mentions)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Mentions in the database
        List<Mentions> mentionsList = mentionsRepository.findAll();
        assertThat(mentionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMentions() throws Exception {
        // Initialize the database
        mentionsRepository.saveAndFlush(mentions);

        int databaseSizeBeforeDelete = mentionsRepository.findAll().size();

        // Delete the mentions
        restMentionsMockMvc
            .perform(delete(ENTITY_API_URL_ID, mentions.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Mentions> mentionsList = mentionsRepository.findAll();
        assertThat(mentionsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
