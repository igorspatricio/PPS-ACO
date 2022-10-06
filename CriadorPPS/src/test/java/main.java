import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import geraPPS.CriaPPS;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;  // Import the Scanner class

public class main {
    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);  // Create a Scanner object

        JSONObject obj = new JSONObject();
        CriaPPS gerarPPS = new CriaPPS();

        String pathArquivoJSON = "src\\main\\resources\\";


//        obj.put("name", "mkyong.com");
//        obj.put("age", 100);
//
//        JSONArray list = new JSONArray();
//        list.add("msg 1");
//        list.add("msg 2");
//        list.add("msg 3");
//
//        obj.put("messages", list);

        System.out.println("Nome do arquivo json a ser salvo(Incluir o .json no fim): ");
        String nomeArquivo = scan.nextLine();  // Read user input
        pathArquivoJSON = pathArquivoJSON.concat(nomeArquivo);

        System.out.println("Numero de projetos de não manutenção: ");
        int nProjetos = scan.nextInt();
        System.out.println("Numero de projetos de manutenção: ");
        int nProjetosManutencao = scan.nextInt();



        obj.put("Projects", gerarPPS.gerarListaProjetos(nProjetos,nProjetosManutencao));
        obj.put("Restriction", gerarPPS.geraMatrizDeRetricaoManutencao(nProjetosManutencao));
        obj.put("Limits", gerarPPS.gerarLimites());

        try (FileWriter file = new FileWriter(pathArquivoJSON)) {
            file.write(obj.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print(obj);
    }
}
