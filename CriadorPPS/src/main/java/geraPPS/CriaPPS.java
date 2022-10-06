package geraPPS;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CriaPPS {

    Random rand = new Random();
    int custoTotalCapex = 0;
    int getCustoTotalOpex = 0;

    public JSONArray gerarLimites(){
        JSONArray limits = new JSONArray();
        limits.add(this.gerarCapexOpex("CAPEX", 5));
        limits.add(this.gerarCapexOpex("OPEX", 5));
        return limits;
    }
    public JSONObject gerarCapexOpex(String tipo, int anos){
        JSONObject custo = new JSONObject();
        JSONArray valores = new JSONArray();
        int custoTotal;

        custo.put("Classification", tipo);
        if (tipo == "CAPEX") {
            custoTotal= this.custoTotalCapex;
        }else{
            custoTotal = this.getCustoTotalOpex;
        }

        for (int i = 0; i < anos; i++) {
            valores.add((int)custoTotal/5);
        }
        custo.put("Values", valores);
        return custo;
    }

    public JSONArray gerarListaProjetos(int projetos, int projetosManutencao){
        JSONArray listaProjetos = new JSONArray();
        for (int i = 0; i < projetos + projetosManutencao; i++) {
            if (i < projetos) {
                listaProjetos.add(gerarProjeto(i+1, 'N'));
            }else{
                listaProjetos.add(gerarProjeto(i+1, 'L'));
            }
        }

        return listaProjetos;
    }
    public JSONObject gerarProjeto(int id, char clasificacao){
        //Variaveis
        int custoMensal, custoTotal;
        JSONObject projeto = new JSONObject();
        List<String> tipos = Arrays.asList("CAPEX","OPEX");

        // valores padroes
        int numTipo = rand.nextInt(tipos.size()); // Numero para Capex ou Opex
        projeto.put("Id", id);
        projeto.put("Classification", tipos.get(numTipo));

        //duração
        int duration = 6 + rand.nextInt(24-6);//duração de 6 a 24meses 2 anos
        projeto.put("Duration", duration);

        //valores de projetos de manutencao
        if(clasificacao == 'N') {
            projeto.put("Halting Duration", -1);
            projeto.put("Maintenance", "N");
            projeto.put("Halting Start Month", -1);
        }else {
            int mesComeco, duracao;
            mesComeco = 1+rand.nextInt((int)duration/2);
            duracao = 1+rand.nextInt(duration - mesComeco);

            projeto.put("Maintenance", "L");
            projeto.put("Halting Start Month", mesComeco);
            projeto.put("Halting Duration", duracao);
        }

        //gerar lista de custos e o custo total
        JSONArray custos = new JSONArray();
        int custoTotalProjeto = 0;
        for (int i = 0; i < duration; i++) {
            custoMensal = rand.nextInt(1500)+1000;
            custos.add(custoMensal); //custo de 1000 a 2000
            custoTotalProjeto += custoMensal;
        }
        if (tipos.get(numTipo) == "CAPEX") {
            this.custoTotalCapex += custoTotalProjeto;
        }else{
            this.getCustoTotalOpex += custoTotalProjeto;
        }

        projeto.put("Costs", custos);

        //definir riscos
        int risco = 20 + rand.nextInt(5*duration);
        projeto.put("Risk", risco);
        return projeto;
    }

    public JSONArray geraMatrizDeRetricaoManutencao(int nProjetosManutencao){
        JSONArray matrizRetorno = new JSONArray();
        JSONArray linhaProjetoI;
        int aux;

        int matriz[][] = new int[nProjetosManutencao][nProjetosManutencao];
        for (int i = 0; i < nProjetosManutencao; i++) {
            for (int j = 0; j <= i; j++) {
                if (j != i) {
                    aux = rand.nextInt(2);
                    matriz[i][j] = aux;
                    matriz[j][i] = aux;
                }else{
                    matriz[i][j] = 0;
                }
            }
        }
        for (int i = 0; i < nProjetosManutencao; i++) {
            linhaProjetoI = new JSONArray();
            for (int j = 0; j < nProjetosManutencao; j++) {
                linhaProjetoI.add(matriz[i][j]);

            }
            matrizRetorno.add(linhaProjetoI);
        }
        return matrizRetorno;
    }
}
