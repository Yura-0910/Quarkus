package ru.lainer.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
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
   * @param user это данные из запроса, которые мы будем использовать для обновления User-а в БД
   * @param id User-а, который нужно обновить
   */
  @PUT
  @Path("/update/{id}")
  @Transactional
  public Response update(Users user, Long id) {
    if (id == null) {
      String message = "Это обновление существующего User-а:: ID не может быть null";
      throw new WebApplicationException(generateResponse(message, 422));
    }

    //Получаем из БД User-а по id
    Users userFromDB = entityManager.find(Users.class, id);
    if (userFromDB == null) {
      String message = "User с id = " + id + " не найден";
      throw new WebApplicationException(generateResponse(message, 404));
    }

    //Обновляем User-а в БД (обновляя userFromDB:: обновляются данные и в БД)
    userFromDB.setLogin(user.getLogin());
    userFromDB.setPassword(user.getPassword());
    //entityManager.merge(user);

    return Response.ok().status(200).entity("User с id = " + id +  ":: обновлен").build();
  }

  @DELETE
  @Path("/delete/{id}")
  @Transactional
  public Response delete(Long id) {
    try{
      //Users user = entityManager.find(Users.class, id);
      Users user = entityManager.getReference(Users.class, id);
      entityManager.remove(user);
    }
    catch (EntityNotFoundException entityNotFoundException){
      String message = "User с id = " + id + " не найден";
      throw new WebApplicationException(generateResponse(message, 404));
    }
    return Response.ok().status(200).entity("User с id = " + id + " удален").build();
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
