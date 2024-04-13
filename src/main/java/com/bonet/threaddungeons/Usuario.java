package com.bonet.threaddungeons;

public class Usuario {
    private Integer id;
    private String login;
    private String password;
    private String nombre;
    private String imagen;

    public Usuario() {
    }

    public Usuario(String login, String password, String nombre, String imagen){
        this.login = login;
        this.password = password;
        this.nombre = nombre;
        this.imagen = imagen;
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
