package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Squeak;
import com.mycompany.myapp.repository.SqueakRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Squeak}.
 */
@Service
@Transactional
public class SqueakService {

    private final Logger log = LoggerFactory.getLogger(SqueakService.class);

    private final SqueakRepository squeakRepository;

    public SqueakService(SqueakRepository squeakRepository) {
        this.squeakRepository = squeakRepository;
    }

    /**
     * Save a squeak.
     *
     * @param squeak the entity to save.
     * @return the persisted entity.
     */
    public Squeak save(Squeak squeak) {
        log.debug("Request to save Squeak : {}", squeak);
        return squeakRepository.save(squeak);
    }

    /**
     * Update a squeak.
     *
     * @param squeak the entity to save.
     * @return the persisted entity.
     */
    public Squeak update(Squeak squeak) {
        log.debug("Request to save Squeak : {}", squeak);
        return squeakRepository.save(squeak);
    }

    /**
     * Partially update a squeak.
     *
     * @param squeak the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Squeak> partialUpdate(Squeak squeak) {
        log.debug("Request to partially update Squeak : {}", squeak);

        return squeakRepository
            .findById(squeak.getId())
            .map(existingSqueak -> {
                if (squeak.getContent() != null) {
                    existingSqueak.setContent(squeak.getContent());
                }
                if (squeak.getImage() != null) {
                    existingSqueak.setImage(squeak.getImage());
                }
                if (squeak.getImageContentType() != null) {
                    existingSqueak.setImageContentType(squeak.getImageContentType());
                }
                if (squeak.getCreated() != null) {
                    existingSqueak.setCreated(squeak.getCreated());
                }
                if (squeak.getLikes() != null) {
                    existingSqueak.setLikes(squeak.getLikes());
                }

                return existingSqueak;
            })
            .map(squeakRepository::save);
    }

    /**
     * Get all the squeaks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Squeak> findAll(Pageable pageable) {
        log.debug("Request to get all Squeaks");
        return squeakRepository.findAll(pageable);
    }

    /**
     * Get all the squeaks with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Squeak> findAllWithEagerRelationships(Pageable pageable) {
        return squeakRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one squeak by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Squeak> findOne(Long id) {
        log.debug("Request to get Squeak : {}", id);
        return squeakRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the squeak by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Squeak : {}", id);
        squeakRepository.deleteById(id);
    }
}
