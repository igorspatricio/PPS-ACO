package PPS;

import isula.aco.Environment;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class ProblemaPPS extends Environment {


    int [][] restricao;
    int[] capex, opex;
    int horizontePlanejamento;
    ArrayList<Projeto> listaProjetos;


    public ArrayList<Projeto> getListaProjetos() {
        return listaProjetos;
    }

    public ProblemaPPS(JSONObject PPS) {
        //variaveis
        Utilidades utilidade = new Utilidades();
        JSONObject limite, projeto;
        JSONArray limites = (JSONArray) PPS.get("Limits");

        //custos
        for (Object i : limites) {
            limite = (JSONObject) i;

            if((limite.get("Classification").toString()).equals("CAPEX"))
            {

                this.capex = utilidade.stringVetor((String) limite.get("Values").toString());
            }
            if((limite.get("Classification").toString()).equals("OPEX"))
            {
                this.opex = utilidade.stringVetor((String) limite.get("Values").toString());
            }
        }
        //restricao
        this.restricao = utilidade.stringMatriz((String) PPS.get("Restriction").toString());
        //duracao do agendamento
        this.horizontePlanejamento = (this.capex).length;

        //extraindo lista de projetos
        JSONArray projetos = (JSONArray) PPS.get("Projects");
        this.listaProjetos = new ArrayList<Projeto>();

        //cria lista de projetos
        for(Object i : projetos)
        {
            projeto = (JSONObject) i;
            this.listaProjetos.add(new Projeto(projeto));
        }
    }

    @Override
    public String toString() {
        String preString = "[";
        for(int[] i : restricao)
        {
            preString = preString + Arrays.toString(i) + ",";
        }
        preString += "]";
        return "ProblemaPPS{" +
                ", \nrestricao=" + preString +
                ", \ncapex=" + Arrays.toString(capex) +
                ", \nopex=" + Arrays.toString(opex) +
                ", \nmeses=" + horizontePlanejamento +
                ", \nlistaProjetos=" + listaProjetos +
                '}';
    }

    @Override
    protected double[][] createPheromoneMatrix() {
        return new double[0][];
    }
}
