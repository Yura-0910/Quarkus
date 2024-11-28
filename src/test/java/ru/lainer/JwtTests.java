package ru.lainer;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

/**
 * Тестируем endPoint-ы из JwtController
 */
@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class JwtTests {

  @Test
  @Order(1)
  void testPermitAll(){
    Response response = given()
        .when()
        .get("/secured/permit-all")
        .andReturn();

    response.then()
        .statusCode(200)
        .body(containsString("hello anonymous, authScheme: null"));
  }
}
