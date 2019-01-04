package com.example.alexandergf.bitacola;

class BiTacolaFolder {
    private String id;
    private String title;
    private Double icon;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getIcon() {
        return icon;
    }

    public void setIcon(Double icon) {
        this.icon = icon;
    }

    public BiTacolaFolder(String id, String title, Double icon){
        this.id = id;

        this.title = title;
        this.icon = icon;
    }
}
