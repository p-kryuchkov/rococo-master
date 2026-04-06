package io.student.rococo.data.entity.data;

import io.student.rococo.model.MuseumJson;
import jakarta.persistence.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.UUID;

import static io.student.rococo.utils.Base64Utils.decodeImageFromB64ToBytes;
import static jakarta.persistence.GenerationType.AUTO;

@Entity
@Table(name = "museum")
public class MuseumEntity {
    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id;
    private String description;
    @Column(name = "title", nullable = false, unique = true)
    private String title;
    private String city;
    private byte[] photo;
    @ManyToOne(optional = false)
    @JoinColumn(name = "country_id", nullable = false, referencedColumnName = "id")
    private CountryEntity country;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CountryEntity getCountry() {
        return country;
    }

    public void setCountry(CountryEntity country) {
        this.country = country;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        MuseumEntity that = (MuseumEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
    public static MuseumEntity fromJson(MuseumJson museumJson) {
        CountryEntity country = new CountryEntity();
        country.setName(museumJson.geo().country().name());
        country.setId(museumJson.geo().country().id());

        MuseumEntity museumEntity = new MuseumEntity();
        museumEntity.setId(museumJson.id());
        museumEntity.setTitle(museumJson.title());
        museumEntity.setDescription(museumJson.description());
        museumEntity.setPhoto(museumJson.photo() == null || museumJson.photo().isBlank()
                ? null
                : decodeImageFromB64ToBytes(museumJson.photo()));
        museumEntity.setCity(museumJson.geo().city());
        museumEntity.setCountry(country);
        return museumEntity;
    }
}
