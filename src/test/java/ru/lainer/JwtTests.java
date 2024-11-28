package ru.lainer;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.build.Jwt;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
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

  @Test
  @Order(3)
  void testOnlyAdminRoleIsAllowed(){
    Response response = given().auth()
        .oauth2(generateValidUserToken())//Разрешен только Admin, но передали User
        .when()
        .get("/secured/roles-allowed-admin").andReturn();

    response.then().statusCode(403);
  }

  @Test
  @Order(4)
  void testOnlyAdminRoleIsAllowed2(){
    Response response = given().auth()
        .oauth2(generateValidAdminToken())//Передаем токен с Админом
        .when()
        .get("/secured/roles-allowed-admin").andReturn();

    response.then()
        .statusCode(200)
        .body(containsString(
            "hello test_admin@yandex.ru, authScheme: Bearer, nickname: test_source"));
  }

  String generateValidAdminToken(){
    return Jwt
        .issuer("https://lainer.ru/issuer")
        .upn("test_admin@yandex.ru")
        .groups("Admin")
        .claim(Claims.nickname, "test_source")
        .sign();
  }

  @Test
  @Order(5)
  public void testExpiredToken() {
    Response response = given().auth()
        .oauth2(generateExpiredToken())
        .when()
        .get("/secured/roles-allowed").andReturn();

    response.then().statusCode(401);
  }

  //Истекло время существования токена
  String generateExpiredToken() {
    return Jwt
        .upn("test@yandex.ru")
        .issuer("https://lainer.ru/issuer")
        .groups(new HashSet<>(Arrays.asList("User", "Admin")))
        .expiresAt(Instant.now().minusSeconds(10))
        .sign();
  }

  //Токен модифицирован
  @Test
  @Order(6)
  public void testModifiedToken() {
    Response response = given().auth()
        .oauth2(generateValidUserToken() + "1")
        .when()
        .get("/secured/roles-allowed").andReturn();

    response.then().statusCode(401);
  }
}
