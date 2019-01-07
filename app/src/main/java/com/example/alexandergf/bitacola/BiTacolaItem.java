package com.example.alexandergf.bitacola;

public class BiTacolaItem {
    private String name;
    private String id;
    private String autor;
    private String fecha;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BiTacolaItem(String name, String id, String autor, String fecha){
        this.name = name;
        this.id= id;
        this.autor = autor;
        this.fecha = fecha;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
