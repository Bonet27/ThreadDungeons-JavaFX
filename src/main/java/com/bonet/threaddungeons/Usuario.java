package com.bonet.threaddungeons;

public class Usuario {
    private Integer id;
    private String login;
    private String password;
    private String nombre;
    private String imagen;
    public Usuario() {}

    public Usuario(Integer id, String login, String password, String nombre, String imagen){
        this.id = id;
        this.login = login;
        this.password = password;
        this.nombre = nombre;
        this.imagen = imagen;
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

    public String getNombre() {
        return nombre;
    }

    public String getImagen() {
        return imagen;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", nombre='" + nombre + '\'' +
                ", imagen='" + imagen + '\'' +
                "}\n";
    }
}
