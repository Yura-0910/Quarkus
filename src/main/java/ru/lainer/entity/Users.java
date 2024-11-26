package ru.lainer.entity;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.QueryHint;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
@NamedQuery(name = "Users.findAll", query = "SELECT u FROM Users u ORDER BY u.login",
    hints = @QueryHint(name = "org.hibernate.cacheable", value = "true"))
@Cacheable
public class Users {

  @Id
  //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_gen")
  //@SequenceGenerator(name = "role_gen", sequenceName = "roles_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id_pk")
  private Long id;

  @Column(name = "login")
  private String login;

  @Column(name = "email")
  private String email;

  @Column(name = "password")
  private String password;

  @Column(name = "role")
  private String role;
}
