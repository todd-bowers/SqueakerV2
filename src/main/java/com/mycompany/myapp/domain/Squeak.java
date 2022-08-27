package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Squeak.
 */
@Entity
@Table(name = "squeak")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Squeak implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "content", nullable = false)
    private String content;

    @Lob
    @Column(name = "image")
    private byte[] image;

    @Column(name = "image_content_type")
    private String imageContentType;

    @Column(name = "created")
    private Instant created;

    @Column(name = "likes")
    private Long likes;

    @ManyToOne
    private User user;

    @ManyToMany
    @JoinTable(name = "rel_squeak__tag", joinColumns = @JoinColumn(name = "squeak_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "squeaks" }, allowSetters = true)
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_squeak__mentions",
        joinColumns = @JoinColumn(name = "squeak_id"),
        inverseJoinColumns = @JoinColumn(name = "mentions_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "squeaks" }, allowSetters = true)
    private Set<Mentions> mentions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Squeak id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public Squeak content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getImage() {
        return this.image;
    }

    public Squeak image(byte[] image) {
        this.setImage(image);
        return this;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return this.imageContentType;
    }

    public Squeak imageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
        return this;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public Instant getCreated() {
        return this.created;
    }

    public Squeak created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Long getLikes() {
        return this.likes;
    }

    public Squeak likes(Long likes) {
        this.setLikes(likes);
        return this;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Squeak user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Squeak tags(Set<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public Squeak addTag(Tag tag) {
        this.tags.add(tag);
        tag.getSqueaks().add(this);
        return this;
    }

    public Squeak removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getSqueaks().remove(this);
        return this;
    }

    public Set<Mentions> getMentions() {
        return this.mentions;
    }

    public void setMentions(Set<Mentions> mentions) {
        this.mentions = mentions;
    }

    public Squeak mentions(Set<Mentions> mentions) {
        this.setMentions(mentions);
        return this;
    }

    public Squeak addMentions(Mentions mentions) {
        this.mentions.add(mentions);
        mentions.getSqueaks().add(this);
        return this;
    }

    public Squeak removeMentions(Mentions mentions) {
        this.mentions.remove(mentions);
        mentions.getSqueaks().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Squeak)) {
            return false;
        }
        return id != null && id.equals(((Squeak) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Squeak{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", image='" + getImage() + "'" +
            ", imageContentType='" + getImageContentType() + "'" +
            ", created='" + getCreated() + "'" +
            ", likes=" + getLikes() +
            "}";
    }
}
