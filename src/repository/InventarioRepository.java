package repository;

import model.Item;
import model.ItemEquipavel;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class InventarioRepository {
    private static final String ARQUIVO = "inventario.csv";
    
    // Índices das colunas no CSV, mais fácil de lembrar do que números mágicos
    private static final int COL_NOME = 0;
    private static final int COL_TIPO = 1;
    private static final int COL_QUANTIDADE = 2;
    private static final int COL_EQUIPAVEL = 3;
    private static final int COL_PODER = 4;

    // Guarda todos os itens no arquivinho CSV
    public static void salvarItens(List<Item> itens) {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(ARQUIVO))) {
            for (Item item : itens) {
                escritor.write(item.toCSV());
                escritor.newLine();
            }
        } catch (IOException e) {
            System.err.println("Ops! Não consegui salvar os dados: " + e.getMessage());
        }
    }

    // Busca os itens que estavam salvos
    public static List<Item> carregarItens() {
        List<Item> itens = new ArrayList<>();
        File arquivo = new File(ARQUIVO);
        
        // Se não tem arquivo, começa do zero
        if (!arquivo.exists()) {
            return itens;
        }

        try (BufferedReader leitor = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                Item item = criarItemDaLinha(linha);
                if (item != null) {
                    itens.add(item);
                }
            }
        } catch (IOException e) {
            System.err.println("Putz, deu erro ao carregar os dados: " + e.getMessage());
        }
        return itens;
    }
    
    // Converte uma linha do CSV de volta pra um objeto Item
    private static Item criarItemDaLinha(String linha) {
        String[] dados = linha.split(",");
        if (dados.length < 5) return null; // Linha mal formatada, ignorar
        
        try {
            String nome = dados[COL_NOME];
            String tipo = dados[COL_TIPO];
            int qtd = Integer.parseInt(dados[COL_QUANTIDADE]);
            String ehEquipavel = dados[COL_EQUIPAVEL];
            int poder = Integer.parseInt(dados[COL_PODER]);

            if (ehEquipavel.equalsIgnoreCase(Item.SIM)) {
                return new ItemEquipavel(nome, tipo, qtd, poder);
            } else {
                return new Item(nome, tipo, qtd);
            }
        } catch (NumberFormatException e) {
            System.err.println("Erro ao ler número na linha: " + linha);
            return null;
        }
    }
}