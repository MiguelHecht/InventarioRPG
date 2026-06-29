package view;

import model.Item;
import model.ItemEquipavel;
import repository.InventarioRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventarioFrame extends JFrame {
    private List<Item> inventario; // A lista que guarda tudo
    
    // Os componentes que o usuário vê e interage
    private JTextField campoNome;
    private JComboBox<String> comboTipo;
    private JSpinner spinnerQuantidade;
    private JCheckBox checkEquipavel;
    private JTextField campoPoder;
    private JTable tabela;
    private DefaultTableModel modelo;

    // Opções pro usuário escolher
    private static final String[] TIPOS = {"Arma", "Armadura", "Poção", "Acessório", "Outros"};
    private static final String[] COLUNAS = {"Nome", "Tipo", "Qtd", "Equipável", "Poder/Defesa"};

    public InventarioFrame() {
        // Vê se já tem itens salvos
        inventario = InventarioRepository.carregarItens();
        
        // Prepara a janela
        configurarJanela();
        criarComponentes();
        mostrarItensNaTabela();
    }

    private void configurarJanela() {
        setTitle("Gerenciador de Inventário RPG");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza na tela
        setLayout(new BorderLayout(10, 10));
    }

    private void criarComponentes() {
        add(criarPainelEntrada(), BorderLayout.NORTH);
        add(criarPainelTabela(), BorderLayout.CENTER);
        add(criarPainelBotoes(), BorderLayout.SOUTH);
    }

    // O formulário onde a gente preenche os dados do item
    private JPanel criarPainelEntrada() {
        JPanel painel = new JPanel(new GridLayout(3, 4, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        campoNome = new JTextField();
        comboTipo = new JComboBox<>(TIPOS);
        spinnerQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        checkEquipavel = new JCheckBox("É equipável?");
        campoPoder = new JTextField("0");
        campoPoder.setEnabled(false);

        // Quando marca "equipável", libera o campo de poder
        checkEquipavel.addActionListener(e -> campoPoder.setEnabled(checkEquipavel.isSelected()));

        // Montando o layout
        painel.add(new JLabel("Nome do item:"));
        painel.add(campoNome);
        painel.add(new JLabel("Tipo:"));
        painel.add(comboTipo);
        painel.add(new JLabel("Quantidade:"));
        painel.add(spinnerQuantidade);
        painel.add(checkEquipavel);
        painel.add(campoPoder);
        
        // Uns espaços pra deixar bonito
        painel.add(new JLabel());
        painel.add(new JLabel());
        painel.add(new JLabel());
        painel.add(new JLabel());

        return painel;
    }

    // A tabela que mostra todos os itens
    private JScrollPane criarPainelTabela() {
        modelo = new DefaultTableModel(COLUNAS, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false; // Não deixa editar direto na tabela
            }
        };
        tabela = new JTable(modelo);
        
        // Quando clica num item, preenche os campos pra editar
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherCamposComItemSelecionado();
            }
        });
        
        return new JScrollPane(tabela);
    }

    // Os botões: Adicionar, Atualizar e Remover
    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        JButton btnAdicionar = new JButton("Adicionar");
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnRemover = new JButton("Remover");

        btnAdicionar.addActionListener(e -> adicionarItem());
        btnAtualizar.addActionListener(e -> atualizarItem());
        btnRemover.addActionListener(e -> removerItem());

        painel.add(btnAdicionar);
        painel.add(btnAtualizar);
        painel.add(btnRemover);
        return painel;
    }

    // --- Ações dos botões ---

    private void adicionarItem() {
        if (!validarCampos()) return;
        
        Item novoItem = criarItemDosCampos();
        inventario.add(novoItem);
        InventarioRepository.salvarItens(inventario);
        atualizarTabela();
        limparCampos();
        JOptionPane.showMessageDialog(this, "Item adicionado com sucesso!");
    }

    private void atualizarItem() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um item na tabela primeiro!");
            return;
        }

        if (!validarCampos()) return;

        Item itemAtualizado = criarItemDosCampos();
        inventario.set(linha, itemAtualizado);
        InventarioRepository.salvarItens(inventario);
        atualizarTabela();
        limparCampos();
        JOptionPane.showMessageDialog(this, "Item atualizado com sucesso!");
    }

    private void removerItem() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um item na tabela primeiro!");
            return;
        }

        Item item = inventario.get(linha);
        int confirmacao = JOptionPane.showConfirmDialog(
            this, 
            "Tem certeza que quer remover \"" + item.getNome() + "\"?", 
            "Confirmacao", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirmacao == JOptionPane.YES_OPTION) {
            inventario.remove(linha);
            InventarioRepository.salvarItens(inventario);
            atualizarTabela();
            limparCampos();
            JOptionPane.showMessageDialog(this, "Item removido com sucesso!");
        }
    }

    // Pega os dados dos campos e cria um Item
    private Item criarItemDosCampos() {
        String nome = campoNome.getText().trim();
        String tipo = (String) comboTipo.getSelectedItem();
        int qtd = (int) spinnerQuantidade.getValue();

        if (checkEquipavel.isSelected()) {
            int poder = Integer.parseInt(campoPoder.getText().trim());
            return new ItemEquipavel(nome, tipo, qtd, poder);
        }
        return new Item(nome, tipo, qtd);
    }

    // Verifica se os campos estão preenchidos direitinho
    private boolean validarCampos() {
        if (campoNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome do item é obrigatório!");
            return false;
        }
        
        if (checkEquipavel.isSelected()) {
            try {
                int poder = Integer.parseInt(campoPoder.getText().trim());
                if (poder < 0) {
                    JOptionPane.showMessageDialog(this, "O poder não pode ser negativo!");
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Digite um número válido para o poder!");
                return false;
            }
        }
        return true;
    }

    // Quando clica na tabela, preenche os campos com os dados do item
    private void preencherCamposComItemSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) return;

        Item item = inventario.get(linha);
        campoNome.setText(item.getNome());
        comboTipo.setSelectedItem(item.getTipo());
        spinnerQuantidade.setValue(item.getQuantidade());

        if (item instanceof ItemEquipavel) {
            checkEquipavel.setSelected(true);
            campoPoder.setEnabled(true);
            campoPoder.setText(String.valueOf(((ItemEquipavel) item).getPoderDefesa()));
        } else {
            checkEquipavel.setSelected(false);
            campoPoder.setEnabled(false);
            campoPoder.setText("0");
        }
    }

    // Mostra todos os itens na tabela
    private void mostrarItensNaTabela() {
        modelo.setRowCount(0);
        for (Item item : inventario) {
            adicionarItemNaTabela(item);
        }
    }

    private void adicionarItemNaTabela(Item item) {
        String ehEquipavel = "Nao";
        int poder = 0;
        
        if (item instanceof ItemEquipavel) {
            ehEquipavel = "Sim";
            poder = ((ItemEquipavel) item).getPoderDefesa();
        }

        modelo.addRow(new Object[]{
            item.getNome(),
            item.getTipo(),
            item.getQuantidade(),
            ehEquipavel,
            poder
        });
    }

    // Atualiza a tabela depois de qualquer mudança
    private void atualizarTabela() {
        modelo.setRowCount(0);
        for (Item item : inventario) {
            adicionarItemNaTabela(item);
        }
    }

    // Limpa os campos pra próxima ação
    private void limparCampos() {
        campoNome.setText("");
        comboTipo.setSelectedIndex(0);
        spinnerQuantidade.setValue(1);
        checkEquipavel.setSelected(false);
        campoPoder.setText("0");
        campoPoder.setEnabled(false);
        tabela.clearSelection();
    }

    // Ponto de partida do programa
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InventarioFrame().setVisible(true));
    }
}