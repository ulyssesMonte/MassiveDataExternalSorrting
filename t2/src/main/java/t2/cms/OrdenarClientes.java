package t2.cms;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class OrdenarClientes {
        
    public static void ordenarArquivoExterno(String nomeArquivo) throws IOException, ClassNotFoundException {
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
                                                                                                                        
                    public static List<File> dividirArquivo(String nomeArq) throws IOException, ClassNotFoundException {
                        List<File> arquivosDivididos = new ArrayList<>();
                        ArquivoCliente arquivoCliente = new ArquivoCliente();
                        arquivoCliente.abrirArquivo(nomeArq, "leitura", Cliente.class);
                                                                                                                
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
                
                    public static void escreverBloco(File arquivo, List<Cliente> clientes) throws IOException {
                        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(arquivo))) {
                            for (Cliente cliente : clientes) {
                                outputStream.writeObject(cliente);
                            }
                        }
                    }
                                                                            
                    public static void mesclarBlocos(List<File> arquivos, File arquivoFinal) throws IOException, ClassNotFoundException {
    PriorityQueue<Cliente> pq = new PriorityQueue<>(Comparator.comparing(Cliente::getNome));
    List<ObjectInputStream> streams = new ArrayList<>();

    try {
        // Inicializa streams e insere o primeiro cliente de cada arquivo na fila de prioridade
        for (File arquivo : arquivos) {
            ObjectInputStream stream = new ObjectInputStream(new FileInputStream(arquivo));
            streams.add(stream);

            try {
                Cliente cliente = (Cliente) stream.readObject();
                pq.add(cliente);
            } catch (EOFException e) {
                // Arquivo vazio, não insere na fila
            }
        }

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(arquivoFinal))) {
            while (!pq.isEmpty()) {
                Cliente cliente = pq.poll();
                outputStream.writeObject(cliente);

                // Recarrega o próximo cliente do stream correspondente
                for (int i = 0; i < streams.size(); i++) {
                    ObjectInputStream stream = streams.get(i);
                    try {
                        Cliente proximoCliente = (Cliente) stream.readObject();
                        pq.add(proximoCliente);
                        break; // Apenas adiciona o próximo cliente de um stream
                    } catch (EOFException e) {
                        // Final do arquivo alcançado
                    }
                }
            }
        }
    } finally {
        // Fecha todos os streams
        for (ObjectInputStream stream : streams) {
            stream.close();
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

    public static void adicionarCliente(String nomeArquivo, Cliente novoCliente) {
        ArquivoCliente arquivoCliente = new ArquivoCliente();
    
        try {
            // Abrir o arquivo no modo leitura/escrita
            arquivoCliente.abrirArquivo(nomeArquivo, "leitura/escrita", Cliente.class);
    
            // Escrever o novo cliente no arquivo
            arquivoCliente.escreveNoArquivo(Collections.singletonList(novoCliente));
        } catch (IOException e) {
            System.err.println("Erro ao adicionar cliente: " + e.getMessage());
        } finally {
            try {
                // Fechar o arquivo
                arquivoCliente.fechaArquivo();
            } catch (IOException e) {
                System.err.println("Erro ao fechar o arquivo: " + e.getMessage());
            }
        }
    }

}

