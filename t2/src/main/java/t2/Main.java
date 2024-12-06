package t2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import javax.swing.SwingUtilities;

import t2.cms.ArquivoCliente;
import t2.cms.Cliente;
import t2.cms.ClienteGUI2;

public class Main {
    public String caminhoArquivo = "arquivo.dat";

    public void ordenarArquivoExterno() throws IOException, ClassNotFoundException {
        int tamanhoBloco = 100;
        List<File> blocosOrdenados = dividirArquivo(tamanhoBloco);
        mesclarBlocos(blocosOrdenados);
    }

    private List<File> dividirArquivo(int tamanhoBloco) throws IOException, ClassNotFoundException {
         List<File> arquivosBlocos = new ArrayList<>();
          ArquivoCliente arquivoCliente = new ArquivoCliente();
           arquivoCliente.abrirArquivo(caminhoArquivo, "leitura", Cliente.class);
            List<Cliente> clientes = new ArrayList<>(); int contador = 0;
             List<Cliente> clientesLidos;
             while (!(clientesLidos = arquivoCliente.leiaDoArquivo(tamanhoBloco)).isEmpty()) {
                clientes.addAll(clientesLidos);
                if (clientes.size() >= tamanhoBloco) {
                    arquivosBlocos.add(escreverBloco(clientes.subList(0, tamanhoBloco), contador));
                    clientes = clientes.subList(tamanhoBloco, clientes.size());
                    contador++;
                }
            }
            if (!clientes.isEmpty()) { arquivosBlocos.add(escreverBloco(clientes, contador));
            } arquivoCliente.fechaArquivo(); return arquivosBlocos;
        } private File escreverBloco(List<Cliente> clientes, int blocoNumero) throws IOException {
            // Ordenar clientes em ordem alfabética pelo nome 
            clientes.sort(Comparator.comparing(Cliente::getNome));
            ArquivoCliente arquivoBloco = new ArquivoCliente();
            String nomeArquivoBloco = "bloco_" + blocoNumero + ".txt";
            arquivoBloco.abrirArquivo(nomeArquivoBloco, "escrita", Cliente.class);
            arquivoBloco.escreveNoArquivo(clientes); arquivoBloco.fechaArquivo();
            return new File(nomeArquivoBloco);
        }

    /*private List<File> dividirArquivo(int tamanhoBloco) throws IOException {
        List<File> arquivosBlocos = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo));
        List<Cliente> clientes = new ArrayList<>();
        String linha; int contador = 0;
        while ((linha = reader.readLine()) != null) { String[] dados = linha.split(",");
        Cliente cliente = new Cliente(dados[0], dados[1], dados[2], dados[3], Integer.parseInt(dados[4]));
        clientes.add(cliente);
        if (clientes.size() == tamanhoBloco) { arquivosBlocos.add(escreverBloco(clientes, contador));
            clientes.clear(); contador++; } } if (!clientes.isEmpty()) { arquivosBlocos.add(escreverBloco(clientes, contador));
            } reader.close();
        return arquivosBlocos;
    }

    private File escreverBloco(List<Cliente> clientes, int blocoNumero) throws IOException {
        // Ordenar clientes em ordem alfabética pelo nome 
        clientes.sort(Comparator.comparing(Cliente::getNome));
        ArquivoCliente arquivoBloco = new ArquivoCliente();
        arquivoBloco.abrirArquivo("bloco.txt", "escrita", Cliente.class);
        arquivoBloco.escreveNoArquivo(clientes);

        return arquivoBloco;
    }*/

    private void mesclarBlocos(List<File> blocosOrdenados) throws IOException, ClassNotFoundException {
        PriorityQueue<BufferedReader> heap = new PriorityQueue<>(Comparator.compare(this::peekClienteId));
        Map<BufferedReader, Cliente> cache = new HashMap<>();
        BufferedWriter writer = new BufferedWriter(new FileWriter("arquivo_ordenado.txt"));
        for (File bloco : blocosOrdenados) {
            BufferedReader reader = new BufferedReader(new FileReader(bloco));
            cache.put(reader, lerCliente(reader));
            heap.add(reader);
        }
        while (!heap.isEmpty()) {
            BufferedReader reader = heap.poll(); Cliente cliente = cache.get(reader);
            writer.write(cliente.getNome() + "," + cliente.getNome()); writer.newLine();
            Cliente proximoCliente = lerCliente(reader);
            if (proximoCliente != null) {
                cache.put(reader, proximoCliente);
                heap.add(reader);
            }
        }
        writer.close();
        for (BufferedReader reader : cache.keySet()) {
            reader.close();
        }
    }

    private Cliente lerCliente(BufferedReader reader) {
        try {
            String linha = reader.readLine();
            if (linha != null) {
                String[] dados = linha.split(",");
                return new Cliente(dados[0], dados[1], dados[2], dados[3], Integer.parseInt(dados[4]));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String peekClienteId(BufferedReader reader) {
        Cliente cliente = cache.get(reader);
        return cliente != null ? cliente.getNome() : String.MAX_VALUE;
    }


    public static void main(String[] args) {
        // SwingUtilities.invokeLater(() -> {
        // ClienteGUI2 gui = new ClienteGUI2();
        // gui.setVisible(true);
        // });
        
    
    }
}