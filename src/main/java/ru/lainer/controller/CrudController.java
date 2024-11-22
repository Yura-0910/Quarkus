package ru.lainer.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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
}
