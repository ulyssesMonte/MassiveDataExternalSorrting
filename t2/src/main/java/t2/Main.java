package t2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import javax.swing.SwingUtilities;

import t2.cms.ArquivoCliente;
import t2.cms.Cliente;
import t2.cms.ClienteGUI2;

public class Main {
    public static String caminhoArquivo = "Clientes.dat";
    
        public void ordenarArquivoExterno(String nomeArquivo) throws IOException, ClassNotFoundException {
            // Passo 1: Dividir o arquivo em partes menores
            List<File> arquivosDivididos = dividirArquivo(nomeArquivo);
            
            // Passo 2: Ordenar cada bloco de clientes
            for (File arquivo : arquivosDivididos) {
                // Usando a classe ArquivoCliente para ler os clientes do arquivo dividido
                ArquivoCliente arquivoCliente = new ArquivoCliente();
                arquivoCliente.abrirArquivo(arquivo.getName(), "leitura", Cliente.class);
                
                // Lê os registros de clientes e ordena
                List<Cliente> clientes = arquivoCliente.leiaDoArquivo(100); // Lê até 100 clientes por vez
                while (clientes != null && !clientes.isEmpty()) {
                    clientes.sort(Comparator.comparing(Cliente::getNome));
                    escreverBloco(arquivo, clientes); // Escreve os clientes ordenados no bloco
                    clientes = arquivoCliente.leiaDoArquivo(100); // Continua lendo o próximo bloco
                }
                
                arquivoCliente.fechaArquivo(); // Fecha o arquivo após a leitura e escrita
            }
            
            // Passo 3: Mesclar os blocos ordenados em um único arquivo
            File arquivoFinal = new File("clientes_ordenados.dat");
            mesclarBlocos(arquivosDivididos, arquivoFinal);
        }
    
        public List<File> dividirArquivo(String nomeArquivo) throws IOException, ClassNotFoundException {
            List<File> arquivosDivididos = new ArrayList<>();
            ArquivoCliente arquivoCliente = new ArquivoCliente();
            arquivoCliente.abrirArquivo(nomeArquivo, "leitura", Cliente.class);
        
            List<Cliente> buffer = new ArrayList<>();
            int contador = 0;
        
            // Dividir os registros em blocos de 100 clientes
            while (true) {
                List<Cliente> clientes = arquivoCliente.leiaDoArquivo(100);
                if (clientes.isEmpty()) break;
        
                buffer.addAll(clientes);
                contador++;
        
                // Quando o buffer atingir 100 registros, escreve um novo arquivo de bloco
                if (buffer.size() >= 100) {
                    File novoArquivo = new File("bloco_" + contador + ".dat");
                    arquivosDivididos.add(novoArquivo);
                    escreverBloco(novoArquivo, buffer);
                    buffer.clear();
                }
            }
        
            arquivoCliente.fechaArquivo();
            return arquivosDivididos;
        }
             public void escreverBloco(File arquivo, List<Cliente> clientes) throws IOException {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(arquivo))) {
            for (Cliente cliente : clientes) {
                outputStream.writeObject(cliente);
            }
        }
    }
    
        public void mesclarBlocos(List<File> arquivos, File arquivoFinal) throws IOException, ClassNotFoundException {
        PriorityQueue<Cliente> pq = new PriorityQueue<>(Comparator.comparing(Cliente::getNome));
    
        // Abrir arquivos de blocos
        List<ObjectInputStream> streams = new ArrayList<>();
        for (File arquivo : arquivos) {
            ObjectInputStream stream = new ObjectInputStream(new FileInputStream(arquivo));
            streams.add(stream);
        }
    
        // Adicionar o primeiro cliente de cada bloco na fila de prioridade
        for (ObjectInputStream stream : streams) {
            Cliente cliente = (Cliente) stream.readObject();
            pq.add(cliente);
        }
    
        // Escrever no arquivo final
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(arquivoFinal))) {
            while (!pq.isEmpty()) {
                Cliente cliente = pq.poll();
                outputStream.writeObject(cliente);
    
                // Carregar o próximo cliente do arquivo se houver
                for (int i = 0; i < streams.size(); i++) {
                    if (streams.get(i).available() > 0) {
                        Cliente proximoCliente = (Cliente) streams.get(i).readObject();
                        pq.add(proximoCliente);
                    }
                }
            }
        }
    }
    
    public Cliente lerCliente(File arquivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(arquivo))) {
            return (Cliente) inputStream.readObject();
        }
    }
    
    public Cliente peekCliente(List<Cliente> clientes) {
        return clientes.isEmpty() ? null : clientes.get(0);
    }
    
    
        public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        ClienteGUI2 gui = new ClienteGUI2();
        gui.setVisible(true);
        });

       /* //GERAR aRQUIVO
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite o nome do arquivo de saída: ");
        String nomeArquivo = scanner.next();
        System.out.print("Digite a quantidade de clientes a serem gerados: ");
        int quantidadeClientes = scanner.nextInt();
        GeradorDeArquivosDeClientes gerador = new GeradorDeArquivosDeClientes();
        gerador.geraGrandeDataSetDeClientes(nomeArquivo, quantidadeClientes);*/
        
    
    }
}