package ru.lainer.jwt;

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
 * Это JWT RBAC (Role-Based Access Control)
 * библиотека SmallRye JWT:: используется для верификации JSON:: в случае успеха получаем
 * "JsonWebToken"
 * библиотека SmallRye JWT:: используется для защищенного доступа к "endpoint"-ам используя
 * "Bearer Token Authorization" и "RBAC(Role-Based Access Control)".
 *
 * Библиотека "smallrye-jwt" представляет "bearer tokens" как "JsonWebToken".
 *
 * То есть не используется аутентификация по логину\паролю
 */
@Path("/secured")
@RequestScoped//Область действия bean-а:: запрос
public class TokenSecuredResource {

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


  private String getResponseString(SecurityContext ctx) {
    String name;
    /*
     * 1) Метод "getUserPrincipal()" из "SecurityContext" возвращает "Principal".
     * 2) "Principal" содержит "name of the current authenticated user".
     * Если user не был аутентифицирован, то метод возвращает null.
     * 3) "Principal" содержит "name" "user"-а делающего этот request или содержит "null"
     *  если "user" не был "authenticated"
     */
    if (ctx.getUserPrincipal() == null) {
      name = "anonymous";
    } else if (!ctx.getUserPrincipal().getName().equals(jwt.getName())) {
      String message = "login из SecurityContext не совпадает с login-ом из JsonWebToken "
          + "(то есть из JWT)";
      throw new InternalServerErrorException("Principal and JsonWebToken names do not match");
    } else {
      name = ctx.getUserPrincipal().getName();
    }
    return String.format("hello %s," + " authScheme: %s", name, ctx.getAuthenticationScheme());
  }

}
