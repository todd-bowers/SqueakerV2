package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Mentions;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Mentions entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MentionsRepository extends JpaRepository<Mentions, Long> {}
