package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Mentions;
import com.mycompany.myapp.repository.MentionsRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Mentions}.
 */
@Service
@Transactional
public class MentionsService {

    private final Logger log = LoggerFactory.getLogger(MentionsService.class);

    private final MentionsRepository mentionsRepository;

    public MentionsService(MentionsRepository mentionsRepository) {
        this.mentionsRepository = mentionsRepository;
    }

    /**
     * Save a mentions.
     *
     * @param mentions the entity to save.
     * @return the persisted entity.
     */
    public Mentions save(Mentions mentions) {
        log.debug("Request to save Mentions : {}", mentions);
        return mentionsRepository.save(mentions);
    }

    /**
     * Update a mentions.
     *
     * @param mentions the entity to save.
     * @return the persisted entity.
     */
    public Mentions update(Mentions mentions) {
        log.debug("Request to save Mentions : {}", mentions);
        return mentionsRepository.save(mentions);
    }

    /**
     * Partially update a mentions.
     *
     * @param mentions the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Mentions> partialUpdate(Mentions mentions) {
        log.debug("Request to partially update Mentions : {}", mentions);

        return mentionsRepository
            .findById(mentions.getId())
            .map(existingMentions -> {
                if (mentions.getHandle() != null) {
                    existingMentions.setHandle(mentions.getHandle());
                }

                return existingMentions;
            })
            .map(mentionsRepository::save);
    }

    /**
     * Get all the mentions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Mentions> findAll(Pageable pageable) {
        log.debug("Request to get all Mentions");
        return mentionsRepository.findAll(pageable);
    }

    /**
     * Get one mentions by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Mentions> findOne(Long id) {
        log.debug("Request to get Mentions : {}", id);
        return mentionsRepository.findById(id);
    }

    /**
     * Delete the mentions by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Mentions : {}", id);
        mentionsRepository.deleteById(id);
    }
}
