package com.example.alexandergf.bitacola;

public class BiTacolaItem {
    private String name;
    private String id;

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

    public BiTacolaItem(String name,String id){
        this.name = name;
        this.id= id;
    }



}
