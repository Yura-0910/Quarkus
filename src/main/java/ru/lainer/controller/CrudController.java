package ru.lainer.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import ru.lainer.entity.Users;

@Path("/api")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class CrudController {

  @Inject
  EntityManager entityManager;

  /**
   * Находим всех Users с помощью пользовательского запроса
   *
   * @return список Users
   */
  @GET
  @Path("/getAllUsers")
  public List<Users> getAllUsers() {
    return entityManager.createNamedQuery("Users.findAll", Users.class).getResultList();
  }

  /**
   * Находим User-a по id
   *
   * @param id пользователя, которого нужно найти
   * @return найденного по id User-а
   */
  @GET
  @Path("/getById/{id}")
  public Users getById(Long id) {
    Users user = entityManager.find(Users.class, id);
    if (user == null) {
      String message = "User с id = " + id + " не найден";
      throw new WebApplicationException(generateResponse(message, 404));
    }
    return user;
  }

  /**
   * Сохраняем User-a в БД
   * @return статус 201, если удалось сохранить
   */
  @POST
  @Path("/saveNewUsr")
  @Transactional
  public Response saveUser(Users user) {
    if (user.getId() != null) {
      String message = "Это добавление нового User:: ID должен быть null";
      throw new WebApplicationException(generateResponse(message, 422));
    }
    entityManager.persist(user);
    return Response.ok().status(201).entity("User сохранен в БД").build();
  }

  /**
   * Генерирует ответ
   */
  public Response generateResponse(String message, int status) {
    return Response.status(status)
        .entity(message)
        .type(MediaType.TEXT_PLAIN_TYPE)
        .build();
  }
}
