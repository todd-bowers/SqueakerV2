package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Squeak;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class SqueakRepositoryWithBagRelationshipsImpl implements SqueakRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Squeak> fetchBagRelationships(Optional<Squeak> squeak) {
        return squeak.map(this::fetchTags).map(this::fetchMentions);
    }

    @Override
    public Page<Squeak> fetchBagRelationships(Page<Squeak> squeaks) {
        return new PageImpl<>(fetchBagRelationships(squeaks.getContent()), squeaks.getPageable(), squeaks.getTotalElements());
    }

    @Override
    public List<Squeak> fetchBagRelationships(List<Squeak> squeaks) {
        return Optional.of(squeaks).map(this::fetchTags).map(this::fetchMentions).orElse(Collections.emptyList());
    }

    Squeak fetchTags(Squeak result) {
        return entityManager
            .createQuery("select squeak from Squeak squeak left join fetch squeak.tags where squeak is :squeak", Squeak.class)
            .setParameter("squeak", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Squeak> fetchTags(List<Squeak> squeaks) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, squeaks.size()).forEach(index -> order.put(squeaks.get(index).getId(), index));
        List<Squeak> result = entityManager
            .createQuery("select distinct squeak from Squeak squeak left join fetch squeak.tags where squeak in :squeaks", Squeak.class)
            .setParameter("squeaks", squeaks)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    Squeak fetchMentions(Squeak result) {
        return entityManager
            .createQuery("select squeak from Squeak squeak left join fetch squeak.mentions where squeak is :squeak", Squeak.class)
            .setParameter("squeak", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Squeak> fetchMentions(List<Squeak> squeaks) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, squeaks.size()).forEach(index -> order.put(squeaks.get(index).getId(), index));
        List<Squeak> result = entityManager
            .createQuery("select distinct squeak from Squeak squeak left join fetch squeak.mentions where squeak in :squeaks", Squeak.class)
            .setParameter("squeaks", squeaks)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
