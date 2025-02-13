package t2.cms;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import static t2.cms.OrdenarClientes.adicionarCliente;
import static t2.cms.OrdenarClientes.ordenarArquivoExterno;

public class ClienteGUI {

    private BufferDeClientes bufferDeClientes;
    private JTable tabelaClientes;
    private DefaultTableModel modeloTabela;
    private String nomeArquivo;

    public ClienteGUI() {
        bufferDeClientes = new BufferDeClientes();
        criarInterface();
    }

    private void criarInterface() {
        JFrame frame = new JFrame("Gerenciador de Clientes");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Painel de botões principais
        JPanel painelBotoes = new JPanel();
        JButton btnListar = new JButton("Listar Clientes");
        JButton btnPesquisar = new JButton("Pesquisar Clientes");
        JButton btnInserir = new JButton("Inserir Cliente");
        JButton btnRemover = new JButton("Remover Cliente");
        painelBotoes.add(btnListar);
        painelBotoes.add(btnPesquisar);
        painelBotoes.add(btnInserir);
        painelBotoes.add(btnRemover);

        // Adicionar os dois painéis ao frame
        JPanel painelSuperior = new JPanel(new BorderLayout());
        painelSuperior.add(painelBotoes, BorderLayout.NORTH);
        frame.add(painelSuperior, BorderLayout.NORTH);

        // Modelo da tabela
        modeloTabela = new DefaultTableModel(new Object[]{"#", "Nome", "Sobrenome", "Endereço", "Telefone", "CreditScore"}, 0);
        tabelaClientes = new JTable(modeloTabela) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Impede a edição das células
            }
        };

        // Ajusta a largura da primeira coluna
        tabelaClientes.getColumnModel().getColumn(0).setPreferredWidth(30);

        JScrollPane scrollPane = new JScrollPane(tabelaClientes);
        frame.add(scrollPane, BorderLayout.CENTER);

        btnListar.addActionListener(e -> {
            try {
                listarClientes();
            } catch (ClassNotFoundException | IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });
        btnPesquisar.addActionListener(e -> pesquisarClientes());
        btnInserir.addActionListener(e -> inserirCliente());
        btnRemover.addActionListener(e -> removerCliente());

        frame.setVisible(true);
    }

    private void carregarClientes(String nomeArq) {

        
        if (nomeArq == null){
            lerNomeArquivo();
            nomeArq = this.nomeArquivo;
        }

        if (nomeArq != null && !nomeArq.trim().isEmpty()) {
            // Inicializa o buffer e carrega os dados usando ArquivoCliente
            bufferDeClientes.associaBuffer(new ArquivoCliente());
            bufferDeClientes.inicializaBuffer("leitura", nomeArq);

            modeloTabela.setRowCount(0); // Limpa a tabela antes de carregar novos dados

            // Lê os clientes do buffer e adiciona à tabela
            Cliente cliente;
            int contador = 1; // Contador de linhas
            while ((cliente = bufferDeClientes.proximoCliente()) != null) {
                modeloTabela.addRow(new Object[]{contador++, cliente.getNome(), cliente.getSobrenome(), cliente.getEndereco(), cliente.getTelefone(), cliente.getCreditScore()});
            }

            // Fecha o buffer
            bufferDeClientes.fechaBuffer();
        } else {
            JOptionPane.showMessageDialog(null, "Nome do arquivo não pode ser vazio.");
        }
    }

    private void listarClientes() throws ClassNotFoundException, IOException {
        //Ordenar e listar clientes
        if (this.nomeArquivo == null) {
            lerNomeArquivo();
        }
        ordenarArquivoExterno(this.nomeArquivo);
        carregarClientes("clientes_ordenados.dat");
        
    }

    private void pesquisarClientes() {
        // Lógica para pesquisar clientes
        JOptionPane.showMessageDialog(null, "Pesquisar Clientes");
    }

    private void inserirCliente() {
        //inserir cliente
        JOptionPane.showMessageDialog(null, "Inserir um novo cliente");
        cadastrarCliente(this.nomeArquivo);
    }

    private void removerCliente() {
        JOptionPane.showMessageDialog(null, "Remover um cliente");
        // Lógica para remover cliente
    }

    /*public static void main(String[] args) {
        SwingUtilities.invokeLater(ClienteGUI::new);
    }*/

    private void lerNomeArquivo() {
        this.nomeArquivo = JOptionPane.showInputDialog(null, "Digite o nome do arquivo de clientes:");
    }

    public static void cadastrarCliente(String nomeArquivo) {
        // Campos para entrada de dados do cliente
        JTextField campoNome = new JTextField();
        JTextField campoSobrenome = new JTextField();
        JTextField campoTelefone = new JTextField();
        JTextField campoEndereco = new JTextField();
        JTextField campoCreditScore = new JTextField();

        // Organizar os campos no painel
        JPanel painel = new JPanel(new GridLayout(5, 2, 10, 10));
        painel.add(new JLabel("Nome:"));
        painel.add(campoNome);
        painel.add(new JLabel("Sobrenome:"));
        painel.add(campoSobrenome);
        painel.add(new JLabel("Telefone:"));
        painel.add(campoTelefone);
        painel.add(new JLabel("Endereço:"));
        painel.add(campoEndereco);
        painel.add(new JLabel("Credit Score:"));
        painel.add(campoCreditScore);

        // Mostrar o painel em um JOptionPane
        int resultado = JOptionPane.showConfirmDialog(null, painel, "Cadastrar Cliente", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        // Verificar se o usuário clicou em OK
        if (resultado == JOptionPane.OK_OPTION) {
            try {
                // Obter os valores dos campos
                String nome = campoNome.getText();
                String sobrenome = campoSobrenome.getText();
                String telefone = campoTelefone.getText();
                String endereco = campoEndereco.getText();
                int creditScore = Integer.parseInt(campoCreditScore.getText());

                // Criar o objeto Cliente
                Cliente novoCliente = new Cliente(nome, sobrenome, telefone, endereco, creditScore);

                // Adicionar o cliente no arquivo
                adicionarCliente(nomeArquivo, novoCliente);

                JOptionPane.showMessageDialog(null, "Cliente cadastrado com sucesso!");
            }   catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Erro: Credit Score deve ser um número.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
            }   catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Erro ao cadastrar cliente: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
        }   else {
                JOptionPane.showMessageDialog(null, "Cadastro cancelado.");
            }
    }
}
