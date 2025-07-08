package br.com.ifce.so.projectdeadlockdetection.models;

public class Recurso {
    private final Integer id;
    private final String nome;
    private final Integer quantidadeInstancias;

    public Recurso(Integer id, String nome, Integer totalInstancias) {
        this.id = id;
        this.nome = nome;
        this.quantidadeInstancias = totalInstancias;
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Integer getQuantidadeInstancias() {
        return quantidadeInstancias;
    }

    @Override
    public String toString() {
        return "Recurso{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", totalInstancias=" + quantidadeInstancias +
                '}';
    }
}
