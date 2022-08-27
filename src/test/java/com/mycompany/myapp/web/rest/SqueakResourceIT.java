package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Squeak;
import com.mycompany.myapp.repository.SqueakRepository;
import com.mycompany.myapp.service.SqueakService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link SqueakResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SqueakResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final Instant DEFAULT_CREATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_LIKES = 1L;
    private static final Long UPDATED_LIKES = 2L;

    private static final String ENTITY_API_URL = "/api/squeaks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SqueakRepository squeakRepository;

    @Mock
    private SqueakRepository squeakRepositoryMock;

    @Mock
    private SqueakService squeakServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSqueakMockMvc;

    private Squeak squeak;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Squeak createEntity(EntityManager em) {
        Squeak squeak = new Squeak()
            .content(DEFAULT_CONTENT)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .created(DEFAULT_CREATED)
            .likes(DEFAULT_LIKES);
        return squeak;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Squeak createUpdatedEntity(EntityManager em) {
        Squeak squeak = new Squeak()
            .content(UPDATED_CONTENT)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .created(UPDATED_CREATED)
            .likes(UPDATED_LIKES);
        return squeak;
    }

    @BeforeEach
    public void initTest() {
        squeak = createEntity(em);
    }

    @Test
    @Transactional
    void createSqueak() throws Exception {
        int databaseSizeBeforeCreate = squeakRepository.findAll().size();
        // Create the Squeak
        restSqueakMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(squeak)))
            .andExpect(status().isCreated());

        // Validate the Squeak in the database
        List<Squeak> squeakList = squeakRepository.findAll();
        assertThat(squeakList).hasSize(databaseSizeBeforeCreate + 1);
        Squeak testSqueak = squeakList.get(squeakList.size() - 1);
        assertThat(testSqueak.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testSqueak.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testSqueak.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testSqueak.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testSqueak.getLikes()).isEqualTo(DEFAULT_LIKES);
    }

    @Test
    @Transactional
    void createSqueakWithExistingId() throws Exception {
        // Create the Squeak with an existing ID
        squeak.setId(1L);

        int databaseSizeBeforeCreate = squeakRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSqueakMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(squeak)))
            .andExpect(status().isBadRequest());

        // Validate the Squeak in the database
        List<Squeak> squeakList = squeakRepository.findAll();
        assertThat(squeakList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkContentIsRequired() throws Exception {
        int databaseSizeBeforeTest = squeakRepository.findAll().size();
        // set the field null
        squeak.setContent(null);

        // Create the Squeak, which fails.

        restSqueakMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(squeak)))
            .andExpect(status().isBadRequest());

        List<Squeak> squeakList = squeakRepository.findAll();
        assertThat(squeakList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSqueaks() throws Exception {
        // Initialize the database
        squeakRepository.saveAndFlush(squeak);

        // Get all the squeakList
        restSqueakMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(squeak.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].likes").value(hasItem(DEFAULT_LIKES.intValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSqueaksWithEagerRelationshipsIsEnabled() throws Exception {
        when(squeakServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSqueakMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(squeakServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSqueaksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(squeakServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSqueakMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(squeakRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSqueak() throws Exception {
        // Initialize the database
        squeakRepository.saveAndFlush(squeak);

        // Get the squeak
        restSqueakMockMvc
            .perform(get(ENTITY_API_URL_ID, squeak.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(squeak.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED.toString()))
            .andExpect(jsonPath("$.likes").value(DEFAULT_LIKES.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingSqueak() throws Exception {
        // Get the squeak
        restSqueakMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSqueak() throws Exception {
        // Initialize the database
        squeakRepository.saveAndFlush(squeak);

        int databaseSizeBeforeUpdate = squeakRepository.findAll().size();

        // Update the squeak
        Squeak updatedSqueak = squeakRepository.findById(squeak.getId()).get();
        // Disconnect from session so that the updates on updatedSqueak are not directly saved in db
        em.detach(updatedSqueak);
        updatedSqueak
            .content(UPDATED_CONTENT)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .created(UPDATED_CREATED)
            .likes(UPDATED_LIKES);

        restSqueakMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSqueak.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSqueak))
            )
            .andExpect(status().isOk());

        // Validate the Squeak in the database
        List<Squeak> squeakList = squeakRepository.findAll();
        assertThat(squeakList).hasSize(databaseSizeBeforeUpdate);
        Squeak testSqueak = squeakList.get(squeakList.size() - 1);
        assertThat(testSqueak.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testSqueak.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testSqueak.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testSqueak.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testSqueak.getLikes()).isEqualTo(UPDATED_LIKES);
    }

    @Test
    @Transactional
    void putNonExistingSqueak() throws Exception {
        int databaseSizeBeforeUpdate = squeakRepository.findAll().size();
        squeak.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSqueakMockMvc
            .perform(
                put(ENTITY_API_URL_ID, squeak.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(squeak))
            )
            .andExpect(status().isBadRequest());

        // Validate the Squeak in the database
        List<Squeak> squeakList = squeakRepository.findAll();
        assertThat(squeakList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSqueak() throws Exception {
        int databaseSizeBeforeUpdate = squeakRepository.findAll().size();
        squeak.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSqueakMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(squeak))
            )
            .andExpect(status().isBadRequest());

        // Validate the Squeak in the database
        List<Squeak> squeakList = squeakRepository.findAll();
        assertThat(squeakList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSqueak() throws Exception {
        int databaseSizeBeforeUpdate = squeakRepository.findAll().size();
        squeak.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSqueakMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(squeak)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Squeak in the database
        List<Squeak> squeakList = squeakRepository.findAll();
        assertThat(squeakList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSqueakWithPatch() throws Exception {
        // Initialize the database
        squeakRepository.saveAndFlush(squeak);

        int databaseSizeBeforeUpdate = squeakRepository.findAll().size();

        // Update the squeak using partial update
        Squeak partialUpdatedSqueak = new Squeak();
        partialUpdatedSqueak.setId(squeak.getId());

        partialUpdatedSqueak.image(UPDATED_IMAGE).imageContentType(UPDATED_IMAGE_CONTENT_TYPE).created(UPDATED_CREATED);

        restSqueakMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSqueak.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSqueak))
            )
            .andExpect(status().isOk());

        // Validate the Squeak in the database
        List<Squeak> squeakList = squeakRepository.findAll();
        assertThat(squeakList).hasSize(databaseSizeBeforeUpdate);
        Squeak testSqueak = squeakList.get(squeakList.size() - 1);
        assertThat(testSqueak.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testSqueak.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testSqueak.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testSqueak.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testSqueak.getLikes()).isEqualTo(DEFAULT_LIKES);
    }

    @Test
    @Transactional
    void fullUpdateSqueakWithPatch() throws Exception {
        // Initialize the database
        squeakRepository.saveAndFlush(squeak);

        int databaseSizeBeforeUpdate = squeakRepository.findAll().size();

        // Update the squeak using partial update
        Squeak partialUpdatedSqueak = new Squeak();
        partialUpdatedSqueak.setId(squeak.getId());

        partialUpdatedSqueak
            .content(UPDATED_CONTENT)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .created(UPDATED_CREATED)
            .likes(UPDATED_LIKES);

        restSqueakMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSqueak.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSqueak))
            )
            .andExpect(status().isOk());

        // Validate the Squeak in the database
        List<Squeak> squeakList = squeakRepository.findAll();
        assertThat(squeakList).hasSize(databaseSizeBeforeUpdate);
        Squeak testSqueak = squeakList.get(squeakList.size() - 1);
        assertThat(testSqueak.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testSqueak.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testSqueak.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testSqueak.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testSqueak.getLikes()).isEqualTo(UPDATED_LIKES);
    }

    @Test
    @Transactional
    void patchNonExistingSqueak() throws Exception {
        int databaseSizeBeforeUpdate = squeakRepository.findAll().size();
        squeak.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSqueakMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, squeak.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(squeak))
            )
            .andExpect(status().isBadRequest());

        // Validate the Squeak in the database
        List<Squeak> squeakList = squeakRepository.findAll();
        assertThat(squeakList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSqueak() throws Exception {
        int databaseSizeBeforeUpdate = squeakRepository.findAll().size();
        squeak.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSqueakMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(squeak))
            )
            .andExpect(status().isBadRequest());

        // Validate the Squeak in the database
        List<Squeak> squeakList = squeakRepository.findAll();
        assertThat(squeakList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSqueak() throws Exception {
        int databaseSizeBeforeUpdate = squeakRepository.findAll().size();
        squeak.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSqueakMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(squeak)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Squeak in the database
        List<Squeak> squeakList = squeakRepository.findAll();
        assertThat(squeakList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSqueak() throws Exception {
        // Initialize the database
        squeakRepository.saveAndFlush(squeak);

        int databaseSizeBeforeDelete = squeakRepository.findAll().size();

        // Delete the squeak
        restSqueakMockMvc
            .perform(delete(ENTITY_API_URL_ID, squeak.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Squeak> squeakList = squeakRepository.findAll();
        assertThat(squeakList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
