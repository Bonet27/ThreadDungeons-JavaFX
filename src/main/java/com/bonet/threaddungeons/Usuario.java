package com.bonet.threaddungeons;

public class Usuario {
    private Integer id;
    private String login;
    private String password;
    private String email;
    public Usuario() {}

    public Usuario(Integer id, String login, String password, String email){
        this.id = id;
        this.login = login;
        this.password = password;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                "}\n";
    }
}
