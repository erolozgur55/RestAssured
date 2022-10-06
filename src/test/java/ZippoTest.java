import POJO.Location;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.List;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

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

    @Test
    public void checkStateInResponseBody() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .body("country", equalTo("United States"))// body.country = = United States ?
                .statusCode(200)
        ;

        // body.country -->body("country")
        //body.'post code'=-->body("post code") kelime arasında bosluk var ise tek tırnak içine alınır
        //body.'country abbreviation' -->body("country abbreviation")
        //body.places[0].'place name'-->body("body.place[0].'place name'")
        // body.place[0].state -->body("places[0].state")
    }

    @Test
    public void bodyJsonPathTest2() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .body("places[0].state", equalTo("California"))// body.country = = United States ?
                .statusCode(200)
        ;
    }

    @Test
    public void bodyJsonPathTest3() {
        given()
                .when()
                .get("http://api.zippopotam.us/tr/01000")
                .then()
                .log().body()
                .body("places.'place name'", hasItem("Büyükçildirim Köyü"))// bir index verilmezse dizinin bütün elemanları aranır
                .statusCode(200)
        //"places.'place name'" bu bilgiler "çaputçu köyü" bu iteme sahip mi hasItem
        ;
    }

    @Test
    public void bodyArrayHasSizeTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .body("places", hasSize(1))// verilen bir pathdeki listin size yani Array kontrol etmektedir.
                .statusCode(200)
        ;
    }

    @Test
    public void combiningTest() {

        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .body("places", hasSize(1))// verilen bir pathdeki listin size yani Array kontrol etmektedir.
                .body("places.state", hasItem("California"))
                .body("places[0].'place name'", equalTo("Beverly Hills"))
                .statusCode(200)
        ;
    }

    @Test
    public void pathParamTest() {
        given()
                .pathParam("Country", "us")
                .pathParam("Zipcode", 90210)
                .log().uri()
                .when()
                .get("http://api.zippopotam.us/{Country}/{Zipcode}")
                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test
    public void pathParamTest2() {
        // 90210 ile 90213 kadar test sonuçlarında places in size nın hepsinde 1 geldiğini test ediniz ?
        for (int i = 90210; i <= 90213; i++)

            given()
                    .pathParam("Country", "us")
                    .pathParam("Zipcode", i)
                    .log().uri()

                    .when()
                    .get("http://api.zippopotam.us/{Country}/{Zipcode}")

                    .then()
                    .log().body()
                    .body("places", hasSize(1))
                    .statusCode(200)
                    ;
    }

    @Test
    public void queryParamTest() {
//	https://gorest.co.in/public/v1/users?page=1 (soru işareti ile gelen parametreyi param olarak gelir)
        given()
                .param("page", 1)

                .log().uri()//request link

                .when()
                .get("https://gorest.co.in/public/v1/users")

                .then()
                .log().body()
                .body("meta.pagination.page", equalTo(1))
                .statusCode(200)
        ;
    }

    @Test
    public void queryParamTest1() {
        for (int pageNo = 1; pageNo <= 10; pageNo++)
            given()
                    .param("page", pageNo)

                    .log().uri()

                    .when()
                    .get("https://gorest.co.in/public/v1/users")

                    .then()
                    .log().body()
                    .body("meta.pagination.page", equalTo(pageNo))
                    .statusCode(200)
                    ;
    }

    RequestSpecification requestSpecs;
    ResponseSpecification responseSpecs;

    @BeforeClass
    void Setup() {
        baseURI = "https://gorest.co.in/public/v1";
        requestSpecs = new RequestSpecBuilder()
                .log(LogDetail.URI)
                .setAccept(ContentType.JSON)
                .build();
        responseSpecs = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.BODY)
                .build();
    }

    @Test
    public void requestResponseSpecification() {
//	https://gorest.co.in/public/v1/users?page=1
        given()
                .param("page", 1)
                .spec(requestSpecs)
                .when()
                .get("/users")//url nin başında http yoksa baseurideki değer otomatik olarak gelir
                .then()

                .body("meta.pagination.page", equalTo(1))
                .spec(responseSpecs)
        ;
    }

    @Test
    public void extractingJsonPath() {
        String placeName =
                given()
                        .when()
                        .get("http://api.zippopotam.us/us/90210")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract().path("places[0].'place name'");
        // extract metodu ile given ile başlayan satır , bir değer döndürür hale getirdi en sonda extract olmalı
        System.out.println("placeName = " + placeName);

    }

    @Test
    public void extractingJsonPathInt() {
        int limit =
                given()
                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //   .log().body()
                        .statusCode(200)
                        .extract().path("meta.pagination.limit") // Extract Path ile bilgileri dışarıya almaktır. Extract path ile yolunu işleyerek sonuç alıyoruz.
                ;
        System.out.println("limit = " + limit);
        Assert.assertEquals(limit, 10, "Test Pass");

    }

    @Test
    public void extractingJsonPathInt1() {
        int id =
                given()
                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //   .log().body()
                        .statusCode(200)
                        .extract().path("data[2].id");
        System.out.println("id = " + id);


    }

    @Test
    public void extractingJsonPathIntList() {
        List<Integer> idler =
                given()
                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //   .log().body()
                        .statusCode(200)
                        .extract().path("data.id"); //Datadaki bütün idleri Integer cinsinden list şeklinde verir
        System.out.println("idler = " + idler);
        Assert.assertTrue(idler.contains(3045));
    }

    @Test
    public void extractingJsonPathStringList() {
        List<String> names =
                given()
                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //   .log().body()
                        .statusCode(200)
                        .extract().path("data.name"); //Datadaki bütün nameleri String olarak  list şeklinde verir
        System.out.println("names = " + names);

    }

    @Test
    public void extractingJsonPathResponseAll() {
        Response response =//Dönen değer ne ise hepsini alır.(response)
                given()
                        .when()
                        .get("https://gorest.co.in/public/v1/users")
                        .then()
                        //   .log().body()
                        .statusCode(200)
                        .extract().response();
        List<Integer> idler = response.path("data.id");
        List<String> names = response.path("data.name");
        int limit = response.path("meta.pagination.limit");
        System.out.println("limit = " + limit);
        System.out.println("names = " + names);
        System.out.println("idler = " + idler);

    }

    @Test
    public void extractingJsonPOJO() {// POJO : JSon Objesi  (Plain Old Java Object) anlamındadır.
        Location location=
                when()
                        .get("http://api.zippopotam.us/us/90210")

                        .then()
                        .extract().as(Location.class); // Location şablonu
        System.out.println("location. = " + location);
        System.out.println("location.getCountry() = " + location.getCountry());
        System.out.println("location.getPlaces().get(0).getPlacename() = " +
                location.getPlaces().get(0).getPlacename());

        //"post code": "90210",
        //    "country": "United States",
        //    "country abbreviation": "US",
        //
        //    "places": [
        //        {
        //            "place name": "Beverly Hills",
        //            "longitude": "-118.4065",
        //            "state": "California",
        //            "state abbreviation": "CA",
        //            "latitude": "34.0901"
        //        }
        //    ]
        //
        //   class Location{
        //       String postcode;
        //       String country;
        //       String countryabbreviation;
        //       ArrayList<Place> places
        //   }
        //
        //   class Place{
        //       String placename;
        //       String longitude;
        //       String state;
        //       String stateabbreviation
        //       String latitude;
        //   }
    }
}