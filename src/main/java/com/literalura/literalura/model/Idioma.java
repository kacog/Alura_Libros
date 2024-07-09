package com.literalura.literalura.model;

public enum Idioma {
    ES("es", "Español"),
    EN("en", "Inglés"),
    FR("fr", "Francés"),
    IT("it", "Italiano"),
    PT("pt", "Portugués");

    private  String idiomaMenu;
    private  String idiomaEspanol;

    Idioma(String idiomaMenu, String idiomaEspanol) {
        this.idiomaMenu = idiomaMenu;
        this.idiomaEspanol = idiomaEspanol;
    }

//    public String getIdiomaMenu() {
//        return idiomaMenu;
//    }
//
//    public String getIdiomaEspanol() {
//        return idiomaEspanol;
//    }

    public static String fromString(String idioma) {
        for (Idioma idiomaEnum : Idioma.values()) {
            if (idiomaEnum.idiomaMenu.equalsIgnoreCase(idioma)) {
                return idiomaEnum.idiomaEspanol;
            }
        }
        throw new IllegalArgumentException("Ninguna categoría encontrada para el idioma: " + idioma);
    }
}
