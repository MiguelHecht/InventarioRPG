package model;

public class Item {
    // Constantes que usamos no CSV, assim não ficam textos soltos pelo código
    public static final String SIM = "Sim";
    public static final String NAO = "Não";
    
    private String nome;
    private String tipo; // arma, poção, armadura, etc
    private int quantidade;

    public Item(String nome, String tipo, int quantidade) {
        this.nome = nome;
        this.tipo = tipo;
        this.quantidade = quantidade;
    }

    // Getters e Setters - o básico que todo mundo usa
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { 
        // Não deixa ficar negativo, pq ninguém quer -3 poções né
        this.quantidade = Math.max(0, quantidade);
    }

    // Formata o item pra salvar no arquivo CSV
    public String toCSV() {
        return nome + "," + tipo + "," + quantidade + "," + NAO + ",0";
    }
    
    // Útil pra debugar e ver o que tem no inventário
    @Override
    public String toString() {
        return nome + " (" + tipo + ") x" + quantidade;
    }
}