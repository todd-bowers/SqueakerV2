package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Squeak;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface SqueakRepositoryWithBagRelationships {
    Optional<Squeak> fetchBagRelationships(Optional<Squeak> squeak);

    List<Squeak> fetchBagRelationships(List<Squeak> squeaks);

    Page<Squeak> fetchBagRelationships(Page<Squeak> squeaks);
}
