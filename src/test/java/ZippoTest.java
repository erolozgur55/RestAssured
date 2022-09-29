import io.restassured.http.ContentType;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
public class ZippoTest {

    @Test
    public void test() {

        given()
                // hazırlık işlemlerini yapıcaz (token,send body,parametreler)

                .when()
                // linki ve metod verilmektedir.

                .then()
        // assert ve verileri ele alma extract (test kısmı)

        ;
    }

    @Test
    public void statusCodeTest() {

        given()


                .when()
                .get("http://api.zippopotam.us/us/90210")


                .then()
                .log().body() // log.All() bütün respons u gösterir.
                .statusCode(200) // status code kontrolü


        ;
    }

    @Test
    public void contentTypeTest() {

        given()


                .when()
                .get("http://api.zippopotam.us/us/90210")


                .then()
                .log().body() // log.All() bütün respons u gösterir.
                .statusCode(200) // status code kontrolü
                .contentType(ContentType.JSON)// HATALI DURUM KONTROLÜ

        ;
    }
}