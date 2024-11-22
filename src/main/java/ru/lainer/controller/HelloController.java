package ru.lainer.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import ru.lainer.service.HelloService;

@Path("/hello")
public class HelloController {

  @Inject
  private HelloService helloService;

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/{nameUrl}")
  public String hello(@PathParam("nameUrl") String name) {
    return helloService.hello(name);
  }
}
