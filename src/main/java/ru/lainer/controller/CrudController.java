package ru.lainer.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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
   * @return список Users
   */
  @GET
  @Path("/getAllUsers")
  public List<Users> getAllUsers() {
    return entityManager.createNamedQuery("Users.findAll",Users.class).getResultList();
  }

  /**
   * Находим User-a по id
   * @param id  пользователя, которого нужно найти
   * @return найденного по id User-а
   */
  @GET
  @Path("/getById/{id}")
    public Users getById(Long id) {
      Users user = entityManager.find(Users.class, id);
      if (user == null) {
        Response response = Response.status(Response.Status.NOT_FOUND)
            .entity("User not found")
            .type(MediaType.TEXT_PLAIN_TYPE)
            .build();
        throw new WebApplicationException(response);
      }
      return user;
    }
}
