package com.bonet.threaddungeons;

public class Usuario {
    private Integer id;
    private String login;
    private String password;
    private String nombre;
    private String apellidos;
    private String imagen;

    public Usuario() {
    }

    public Usuario(String login, String password, String nombre, String apellidos, String imagen){
        this.login = login;
        this.password = password;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.imagen = imagen;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }


    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", imagen='" + imagen + '\'' +
                "}\n";
    }
}
