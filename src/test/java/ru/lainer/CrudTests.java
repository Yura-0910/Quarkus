package ru.lainer;

import io.quarkus.test.junit.QuarkusTest;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import ru.lainer.entity.Users;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
class CrudTests {

  @Test
  @Order(1)
  void testHelloController() {
    given()
        .when().get("/hello/TestMessage")
        .then()
        .statusCode(200)
        .body(is("TestMessage Hello world!"));
  }

  @Test
  @Order(2)
  void testGetAllUsers() {
    given()
        .when().get("/api/getAllUsers")
        .then()
        .statusCode(200)
        .body("size()", is(3));
  }

  @Test
  @Order(3)
  void testGetAllUsers2() {
    Users user1 = new Users();
    user1.setId(1L);
    user1.setLogin("user1");
    user1.setPassword("pwd1");

    List<Users> listOfUsers = Arrays.stream(given()
        .when().get("/api/getAllUsers")
        .then()
        .statusCode(200)
        .extract()
        .as(Users[].class)).toList();

    assertThat(listOfUsers.size(),is(3));
    assertThat(listOfUsers, hasItem(user1));
  }

  @Test
  @Order(4)
  void testGetById(){
    Users user = given()
        .when().get("/api/getById/3")
        .then()
        .statusCode(200)
        .extract()
        .as(Users.class);

    assertThat(user.getId(),is(3L));
    assertThat(user.getLogin(),is("user3"));
    assertThat(user.getPassword(),is("pwd3"));
  }

  @Test
  @Order(5)
  void testSaveUser(){
    Users user = new Users();
    user.setLogin("R2-D2");
    user.setPassword("pwd4");

    given()
        .body(user)
        .contentType("application/json")
        .when()
        .post("/api/saveNewUsr")
        .then()
        .statusCode(201)
        .body(is("User сохранен в БД"));;
  }
}