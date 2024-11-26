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

@Path("/secured")
@RequestScoped//Область действия bean-а:: запрос
public class TokenSecuredResource {

  //Сюда автоматически внедряется JWT токен (Bearer) из POST-запроса
  @Inject
  JsonWebToken jwt;

  @Inject
  @Claim(standard = Claims.nickname)//Claims - это утверждение
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

  private String getResponseString(SecurityContext ctx) {
    String name;
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
