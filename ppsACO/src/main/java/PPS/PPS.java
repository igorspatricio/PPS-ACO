package PPS;

import globais.MetodosGlobais;

import java.util.HashMap;

import java.util.List;

public class PPS {

    private HashMap<Integer, Integer> solucaoPPS;

    public PPS(){
        this.solucaoPPS = new HashMap<>();
    }

    public void transformaSolucaoACOParaSolucaoPPS(List<Integer> solucaoACO, int horizontePlanejamento){
        int idProjeto, mes;
        MetodosGlobais mg = new MetodosGlobais();

        for (int i = 0, j = 1; j < solucaoACO.size(); i+=2, j+=2) {
            idProjeto = mg.converteComponenteParaIDPPS(solucaoACO.get(i), horizontePlanejamento);
            mes = mg.converteComponenteParaMesPPS(solucaoACO.get(j));
            this.solucaoPPS.put(idProjeto,mes);
        }
    }

    public void mostraSolucao(){
        System.out.println(this.solucaoPPS);
    }



}
