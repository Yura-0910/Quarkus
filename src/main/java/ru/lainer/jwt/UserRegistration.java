package ru.lainer.jwt;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashSet;
import ru.lainer.controller.CrudController;
import ru.lainer.entity.Users;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.jwt.Claims;


@ApplicationScoped
@Path("/auth")
@Consumes("application/json")
public class UserRegistration {

  @Inject
  private CrudController crudController;
  @Inject
  private EntityManager entityManager;

  /**
   * Сохраняем в БД нового пользователя и генерируем для него токен
   */
  @POST
  @Path("/addUser")
  @Transactional
  public Response addUserToDB(Users user) {
    entityManager.persist(user);
    String token = Jwt.issuer("https://lainer.ru/issuer")
        .upn("mazurovyura09@yandex.ru")
        .groups(new HashSet<>(Arrays.asList(user.getRole())))
        .claim(Claims.nickname, user.getLogin())
        .sign();

    String message = "User с ID = " + user.getId() + " успешно сохранен в БД."
        + " Ваш токен:: " + token;
    return crudController.generateResponse(message, 200);
  }
}
