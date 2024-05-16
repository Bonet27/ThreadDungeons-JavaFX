package com.bonet.threaddungeons;

public class MensajeInicial {
    public String mensajeBienvenida;
    public String guiaJuego;

    public MensajeInicial(String mensajeBienvenida, String guiaJuego) {
        this.mensajeBienvenida = mensajeBienvenida;
        this.guiaJuego = guiaJuego;
    }

    public String getMensajeBienvenida() {
        return mensajeBienvenida;
    }

    public void setMensajeBienvenida(String mensajeBienvenida) {
        this.mensajeBienvenida = mensajeBienvenida;
    }

    public String getGuiaJuego() {
        return guiaJuego;
    }

    public void setGuiaJuego(String guiaJuego) {
        this.guiaJuego = guiaJuego;
    }
}
