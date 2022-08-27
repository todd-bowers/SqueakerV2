package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Tag.
 */
@Entity
@Table(name = "tag")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 2)
    @Column(name = "hashtag", nullable = false)
    private String hashtag;

    @ManyToMany(mappedBy = "tags")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "tags", "mentions" }, allowSetters = true)
    private Set<Squeak> squeaks = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Tag id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHashtag() {
        return this.hashtag;
    }

    public Tag hashtag(String hashtag) {
        this.setHashtag(hashtag);
        return this;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public Set<Squeak> getSqueaks() {
        return this.squeaks;
    }

    public void setSqueaks(Set<Squeak> squeaks) {
        if (this.squeaks != null) {
            this.squeaks.forEach(i -> i.removeTag(this));
        }
        if (squeaks != null) {
            squeaks.forEach(i -> i.addTag(this));
        }
        this.squeaks = squeaks;
    }

    public Tag squeaks(Set<Squeak> squeaks) {
        this.setSqueaks(squeaks);
        return this;
    }

    public Tag addSqueak(Squeak squeak) {
        this.squeaks.add(squeak);
        squeak.getTags().add(this);
        return this;
    }

    public Tag removeSqueak(Squeak squeak) {
        this.squeaks.remove(squeak);
        squeak.getTags().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tag)) {
            return false;
        }
        return id != null && id.equals(((Tag) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tag{" +
            "id=" + getId() +
            ", hashtag='" + getHashtag() + "'" +
            "}";
    }
}
