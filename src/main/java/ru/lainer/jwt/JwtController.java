package ru.lainer.jwt;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.InternalServerErrorException;

/**
 * 1) Это JWT RBAC (Role-Based Access Control)
 * 2) библиотека SmallRye JWT:: используется для верификации JSON:: в случае успеха получаем
 * "JsonWebToken"
 * 3) библиотека SmallRye JWT:: используется для защищенного доступа к "endpoint"-ам используя
 * "Bearer Token Authorization" и "RBAC(Role-Based Access Control)".
 *
 * 4) Библиотека "smallrye-jwt" представляет "bearer tokens" как "JsonWebToken".
 *
 * 5) То есть не используется аутентификация по логину\паролю
 *
 * 6) Работает так:: на URL\"endPoint" отправляется запрос с JWT -> из JWT, Quarkus, автоматически
 * извлекает "Role". Если, например у "endPoint"(@RolesAllowed({"User", "Admin"})) и в запросе
 * у JWT токена "Role" = "User" или "Admin", то в SecurityContext помещаются данные(логин, роль,
 * схема аутентификации) о успешной аутентификации (т.к. аутентификация происходит по ролям)
 * и к данному "endPoint" предоставляется доступ (т.е. успешно проходит еще и авторизация)::
 * при успешной авторизации - срабатывает тело метода
 *
 * То есть сначала происходит аутентификация по ролям в автоматическом режиме. Если аутентификация
 * прошла успешно, то дальше считается, что и авторизация прошла успешно.
 *
 * 7) Если, например в JWT:: "Role" = "User", а у "endPoint"(@RolesAllowed({"Admin"})),
 * то аутентификация считается,что не прошла (по ролям аутентификация идет в Quarkus),
 * и в SecurityContext поля заполняются "Null" и еще авторизация считается неудачной.
 *
 * 8) При успешной аутентификации по ролям:: данные, автоматически, помещаются в SecurityContext.
 * Дополнительно можно самому извлечь данные из JWT Token-а. Аутентификация по ролям - происходит
 * автоматически.
 *
 * 9) К каждому "endPoint"\запросу происходит своя аутентификация и авторизация,
 * так как используется @RequestScoped
 */
@Path("/secured")
@RequestScoped//Время действия bean-а:: запрос
public class JwtController {

  //Сюда автоматически внедряется JWT токен (Bearer) из POST-запроса
  @Inject
  JsonWebToken jwt;

  //Claims - это утверждение:: автоматически внедряется из JWT-токена (из запроса)
  @Inject
  @Claim(standard = Claims.nickname)
  String nickname;

  @GET
  @Path("/permit-all")
  @PermitAll//авторизация на уровне метода:: все роли могут вызывать метод
  @Produces(MediaType.TEXT_PLAIN)
  public String hello(@Context SecurityContext ctx) {
    return getResponseString(ctx);
  }

  @GET
  @Path("/roles-allowed")
  @RolesAllowed({"User", "Admin"})//Данный EndPoint можно вызывать только с ролью "User" или "Admin"
  @Produces(MediaType.TEXT_PLAIN)
  public String helloRolesAllowed(@Context SecurityContext ctx) {
    return getResponseString(ctx) + ", "
        + "nickname(из токена): " + jwt.getClaim("nickname").toString();
  }

  @GET
  @Path("/roles-allowed-admin")
  @RolesAllowed("Admin")
  @Produces(MediaType.TEXT_PLAIN)
  public String helloRolesAllowedAdmin(@Context SecurityContext ctx) {
    return getResponseString(ctx) + ", nickname: " + nickname;
  }

  @GET
  @Path("/deny-all")
  @DenyAll
  @Produces(MediaType.TEXT_PLAIN)
  public String helloShouldDeny(@Context SecurityContext ctx) {
    throw new InternalServerErrorException("This method must not be invoked");
  }

  private String getResponseString(SecurityContext ctx) {
    String name;
    /*
     * 1) Метод "getUserPrincipal()" из "SecurityContext" возвращает "Principal".
     * 2) "Principal" содержит "name of the current authenticated user".
     * Если user не был аутентифицирован, то метод возвращает null.
     * 3) "Principal" содержит "name" "user"-а делающего этот request или содержит "null"
     *  если "user" не был "authenticated"
     *
     * От себя:: "getUserPrincipal()" содержит "name" -> "current"(текущего) аутентифицированного
     * пользователя (сделавшего запрос), а "jwt.getName()" содержит "name" из JWT, то есть
     * может возникнуть ситуация, когда в "SecurityContext" записались данные из одного запроса
     * с успешной аутентификацией, а в "JWT-Token"-е - другой "name"
     */
    if (ctx.getUserPrincipal() == null) {
      name = "anonymous";
    } else if (!ctx.getUserPrincipal().getName().equals(jwt.getName())) {
      String message = "login из SecurityContext не совпадает с login-ом из JsonWebToken "
          + "(то есть из JWT)";
      throw new InternalServerErrorException(message);
    } else {
      name = ctx.getUserPrincipal().getName();
    }
    return String.format("hello %s," + " authScheme: %s", name, ctx.getAuthenticationScheme());
  }

}
