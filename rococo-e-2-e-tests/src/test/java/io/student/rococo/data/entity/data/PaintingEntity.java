package io.student.rococo.data.entity.data;

import io.student.rococo.model.PaintingJson;
import jakarta.persistence.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.UUID;

import static io.student.rococo.utils.Base64Utils.decodeImageFromB64ToBytes;
import static jakarta.persistence.GenerationType.AUTO;

@Entity
@Table(name = "painting")
public class PaintingEntity {
    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id;
    private String description;
    @Column(name = "title", nullable = false)
    private String title;
    private byte[] content;
    @ManyToOne
    @JoinColumn(name = "artist_id", referencedColumnName = "id")
    private ArtistEntity artist;
    @ManyToOne(optional = false)
    @JoinColumn(name = "museum_id", referencedColumnName = "id")
    private MuseumEntity museum;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public ArtistEntity getArtist() {
        return artist;
    }

    public void setArtist(ArtistEntity artist) {
        this.artist = artist;
    }

    public MuseumEntity getMuseum() {
        return museum;
    }

    public void setMuseum(MuseumEntity museum) {
        this.museum = museum;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        PaintingEntity that = (PaintingEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
    public static PaintingEntity fromJson(PaintingJson paintingJson) {
        PaintingEntity paintingEntity = new PaintingEntity();
        paintingEntity.setId(paintingJson.id());
        paintingEntity.setTitle(paintingJson.title());
        paintingEntity.setDescription(paintingJson.description());
        paintingEntity.setContent(paintingJson.content() == null || paintingJson.content().isBlank()
                ? null
                : decodeImageFromB64ToBytes(paintingJson.content()));
        paintingEntity.setArtist(paintingJson.artist() == null
                ? null
                : ArtistEntity.fromJson(paintingJson.artist()));
        paintingEntity.setMuseum(paintingJson.museum() == null
                ? null
                : MuseumEntity.fromJson(paintingJson.museum()));
        return paintingEntity;
    }
}
