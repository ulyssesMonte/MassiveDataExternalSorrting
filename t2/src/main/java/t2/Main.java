package t2;

import java.util.Scanner;

import javax.swing.SwingUtilities;

import t2.cms.ClienteGUI;
import t2.cms.GeradorDeArquivosDeClientes;

public class Main {
      
        public static void main(String[] args) {
       /* SwingUtilities.invokeLater(() -> {
        ClienteGUI2 gui = new ClienteGUI2();
        gui.setVisible(true);
        });*/

      //GERAR aRQUIVO
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite o nome do arquivo de saída: ");
        String nomeArquivo = scanner.next();
        System.out.print("Digite a quantidade de clientes a serem gerados: ");
        int quantidadeClientes = scanner.nextInt();
        GeradorDeArquivosDeClientes gerador = new GeradorDeArquivosDeClientes();
        gerador.geraGrandeDataSetDeClientes(nomeArquivo, quantidadeClientes);

        SwingUtilities.invokeLater(ClienteGUI::new);
    }
}