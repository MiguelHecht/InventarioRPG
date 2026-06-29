package model;

public class ItemEquipavel extends Item {
    private int poderDefesa; // Pode ser ataque, defesa, ou o que o RPG precisar

    public ItemEquipavel(String nome, String tipo, int quantidade, int poderDefesa) {
        super(nome, tipo, quantidade);
        this.poderDefesa = Math.max(0, poderDefesa); // Nada de valores negativos
    }

    public int getPoderDefesa() { return poderDefesa; }
    public void setPoderDefesa(int poderDefesa) { 
        this.poderDefesa = Math.max(0, poderDefesa);
    }

    @Override
    public String toCSV() {
        // "Sim" aqui quer dizer que é equipável, e o último número é o poder
        return getNome() + "," + getTipo() + "," + getQuantidade() + 
               "," + SIM + "," + poderDefesa;
    }
    
    @Override
    public String toString() {
        return super.toString() + " [Poder: " + poderDefesa + "]";
    }
}