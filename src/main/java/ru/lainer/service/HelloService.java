package ru.lainer.service;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HelloService {
  public String hello(String name) {
    return name + " Hello world!";
  }
}
