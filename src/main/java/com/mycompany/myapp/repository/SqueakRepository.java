package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Squeak;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Squeak entity.
 *
 * When extending this class, extend SqueakRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface SqueakRepository extends SqueakRepositoryWithBagRelationships, JpaRepository<Squeak, Long> {
    @Query("select squeak from Squeak squeak where squeak.user.login = ?#{principal.username}")
    List<Squeak> findByUserIsCurrentUser();

    default Optional<Squeak> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<Squeak> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<Squeak> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select distinct squeak from Squeak squeak left join fetch squeak.user",
        countQuery = "select count(distinct squeak) from Squeak squeak"
    )
    Page<Squeak> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct squeak from Squeak squeak left join fetch squeak.user")
    List<Squeak> findAllWithToOneRelationships();

    @Query("select squeak from Squeak squeak left join fetch squeak.user where squeak.id =:id")
    Optional<Squeak> findOneWithToOneRelationships(@Param("id") Long id);
}
