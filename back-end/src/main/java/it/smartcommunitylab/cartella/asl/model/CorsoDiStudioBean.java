package it.smartcommunitylab.cartella.asl.model;

public class CorsoDiStudioBean {
    private String courseId;
    private String nome;


    public CorsoDiStudioBean(String courseId, String nome) {
        this.courseId = courseId;
        this.nome = nome;
    }

    public CorsoDiStudioBean() {

    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }



}
