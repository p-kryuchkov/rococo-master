package io.student.rococo.test;

import io.student.rococo.model.*;
import io.student.rococo.service.ArtistClient;
import io.student.rococo.service.MuseumClient;
import io.student.rococo.service.PaintingClient;
import io.student.rococo.service.UserClient;
import io.student.rococo.service.db.ArtistDbClient;
import io.student.rococo.service.db.MuseumDbClient;
import io.student.rococo.service.db.PaintingDbClient;
import io.student.rococo.service.db.UserDbClient;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class FakeTest {
    @Test
    public  void fakeTest(){
        ArtistClient artistClient = new ArtistDbClient();
        MuseumClient museumClient = new MuseumDbClient();
        PaintingClient paintingClient = new PaintingDbClient();
        ArtistJson artist = artistClient.createArtist(new ArtistJson(null, "pippttupupfffff", "pepepep", null));
        MuseumJson museum = museumClient.createMuseum(new MuseumJson(null, "papfffapap", "popopopfffop", null,
                new GeoJson("city", new CountryJson(UUID.fromString("11f12889-ecd3-ffea-8383-06851c9de74b"), "Россия"))));
        paintingClient.createPainting(new PaintingJson(null, "hjdsgfhjdsghjf", "fdgdfgdf", null, artist, museum));
    }

    @Test
    public  void fakeusTest(){
        UserClient userClient = new UserDbClient();
        userClient.createUser("psisisi", "12345");
    }
}
