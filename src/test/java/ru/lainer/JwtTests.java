package ru.lainer;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.jwt.Claims;
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

  @Test
  @Order(2)
  void testUserRoleIsAllowed(){
    Response response = given().auth()
        .oauth2(generateValidUserToken())
        .when()
        .get("/secured/roles-allowed").andReturn();

    response.then()
        .statusCode(200)
        .body(containsString(
            "hello test_user@yandex.ru, authScheme: Bearer, nickname(из токена): test_source"));
  }

  String generateValidUserToken(){
    return Jwt
        .issuer("https://lainer.ru/issuer")
        .upn("test_user@yandex.ru")
        .groups("User")
        .claim(Claims.nickname, "test_source")
        .sign();
  }
}
