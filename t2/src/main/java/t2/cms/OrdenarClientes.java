package t2.cms;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class OrdenarClientes {
        
    public static void ordenarArquivoExterno(String nomeArquivo) throws IOException, ClassNotFoundException {
        // Passo 1: Dividir o arquivo em partes menores
        List<File> arquivosDivididos = dividirArquivo(nomeArquivo);
        try{            
            // Passo 2: Ordenar cada bloco de clientes
            for (File arquivo : arquivosDivididos) {
                // Usando a classe ArquivoCliente para ler os clientes do arquivo dividido
                ArquivoCliente arquivoCliente = new ArquivoCliente();
                arquivoCliente.abrirArquivo(arquivo.getName(), "leitura", Cliente.class);
                            
                // Lê os registros de clientes e ordena
                List<Cliente> clientes = arquivoCliente.leiaDoArquivo(100); // Lê até 100 clientes por vez
                while (clientes != null && !clientes.isEmpty()) {
                    clientes.sort(Comparator.comparing(Cliente::getNome).thenComparing(Cliente::getSobrenome));

                    escreverBloco(arquivo, clientes); // Escreve os clientes ordenados no bloco
                    clientes = arquivoCliente.leiaDoArquivo(100); // Continua lendo o próximo bloco
                }
                                                            
                arquivoCliente.fechaArquivo(); // Fecha o arquivo após a leitura e escrita
            }
                                                        
            // Passo 3: Mesclar os blocos ordenados em um único arquivo
            File arquivoFinal = new File("clientes_ordenados.dat");

            // Garantir que o arquivo esteja vazio
            ArquivoCliente arquivoCliente = new ArquivoCliente();
                arquivoCliente.abrirArquivo(arquivoFinal.getName(), "escrita", Cliente.class);
                // Apenas abrir o arquivo no modo "escrita" já sobrescreve o conteúdo
                arquivoCliente.fechaArquivo();

            // Chamar a função de mesclagem
            mesclarBlocos(arquivosDivididos, arquivoFinal);
    }   finally {
                // Passo 5: Limpar arquivos temporários
                for (File arquivo : arquivosDivididos) {
                    if (arquivo.exists()) {
                        arquivo.delete();
                    }
                }
            }
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

    public static void adicionarCliente(String nomeArq, Cliente novoCliente) {
        try (RandomAccessFile raf = new RandomAccessFile(nomeArq, "rw")) {
            // Posiciona o cursor no final do arquivo
            raf.seek(raf.length());

            // Serializa o novo cliente e escreve no arquivo
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(novoCliente);
            oos.flush();

            // Escreve o tamanho do objeto serializado (em bytes)
            byte[] dados = bos.toByteArray();
            raf.writeInt(dados.length); // Tamanho do objeto
            raf.write(dados); // Dados do objeto

            System.out.println("Cliente adicionado com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao adicionar cliente: " + e.getMessage());
        }
    }

}

