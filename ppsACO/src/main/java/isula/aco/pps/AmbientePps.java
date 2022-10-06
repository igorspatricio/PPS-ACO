package isula.aco.pps;

import PPS.Projeto;
import PPS.Utilidades;
import globais.MetodosGlobais;
import isula.aco.Environment;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AmbientePps extends Environment {

   private double [][] matrizHeuristica;
   private int [][] restricao;
   private int[] capex, opex;
   private int horizontePlanejamentoAnos;
   private int horizontePlanejamentoMeses;
   private final int horizonteDeExecucao = 1;
   private int riscoTotal = 0;
   private int numeroDeProjetos;
   HashMap<Integer, Projeto> listaProjetos;
   private List<Integer> listaIdProjetosManutencao;
   final int MESES_EM_UM_ANO = 12;

    public AmbientePps(JSONObject PPS) {
        super();
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
        this.horizontePlanejamentoAnos = (this.capex).length;
        this.horizontePlanejamentoMeses = this.horizontePlanejamentoAnos * 12;

        //extraindo lista de projetos
        JSONArray projetos = (JSONArray) PPS.get("Projects");
        this.listaProjetos = new HashMap<>();
        this.listaIdProjetosManutencao = new ArrayList<>();
        this.numeroDeProjetos = 0;

        //cria lista de projetos
        for(Object i : projetos)
        {
            projeto = (JSONObject) i;

            int id = (int)(long)projeto.get("Id");
            this.listaProjetos.put(id, new Projeto(projeto));
            riscoTotal += (int) (long) projeto.get("Risk");
            if(listaProjetos.get(id).verificaSeProjetoEhDeManutencao()){
                this.listaIdProjetosManutencao.add(id);
            }
            this.numeroDeProjetos++;
        }
        this.setPheromoneMatrix(this.createPheromoneMatrix());
        this.criarMatrizHeuristica();
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
                ", \nmeses=" + horizontePlanejamentoMeses +
                ", \nlistaProjetos=" + listaProjetos +
                ", \nRiscoTotal=" + riscoTotal +
                '}';

    }

    @Override
    protected double[][] createPheromoneMatrix() {
        if (this.listaProjetos != null) {
            int numeroDeComponentes = (this.horizontePlanejamentoMeses + this.horizonteDeExecucao) + this.listaProjetos.size(); //PH + 1° mes do horizonte de execução componente de meses e  numero de Projetos componente projetos
            return new double[numeroDeComponentes][numeroDeComponentes];
        }
        else{
            return new double[0][];
        }
    }
    protected void criarMatrizHeuristica() {
        if (this.listaProjetos != null) {
            int tempoParaAgendamento = this.horizontePlanejamentoMeses + this.horizonteDeExecucao;
            int numeroDeComponentes = tempoParaAgendamento +
                    this.listaProjetos.size(); //PH + 1° mes do horizonte de execução componente de meses e  numero de Projetos componente projetos



            this.matrizHeuristica = new double[numeroDeComponentes][numeroDeComponentes];
            for (int key :
                    listaProjetos.keySet()) {
                int posicaoProjeto = MetodosGlobais.converteIDPPSParaComponente(key, this.horizontePlanejamentoMeses);
                for (int i = 0; i < tempoParaAgendamento ; i++) {
                    matrizHeuristica[posicaoProjeto][i] = 1 - this.getCustoProjetoParaMes(key, i +1);
                    matrizHeuristica[i][posicaoProjeto] = 1 - this.getCustoMesParaProjeto(key);
                }
            }

        }
        else{
            this.matrizHeuristica = new double[0][];
        }

    }
    public double getHeuristica(int componenteAtual, int componenteDestino){
        return this.matrizHeuristica[componenteAtual][componenteDestino];
    }


    public int[] getOpex() {
        return opex;
    }

    public int[] getCapex() {
        return capex;
    }

    public int getHorizontePlanejamentoAnos() {
        return horizontePlanejamentoAnos;
    }
    public int getHorizontePlanejamentoMeses() {
        return horizontePlanejamentoMeses;
    }

    public HashMap<Integer, Projeto> getListaProjetos() {
        return listaProjetos;
    }

    public int getNumeroDeProjetos() {
        return numeroDeProjetos;
    }

    public int getRiscoTotal() {
        return riscoTotal;
    }

    public double getCustoProjetoParaMes(int projeto, int mes){
        double retorno; // valor a ser retornado

        //informações do projeto
        int risco = this.listaProjetos.get(projeto).getRisco();
        int duracao = this.listaProjetos.get(projeto).getDuracao();
        //Horizonte de planejamento em meses
        int mesesTotais = this.horizontePlanejamentoMeses;

        //Formula da função J para projeto -> mes
        retorno = (double) (risco*(2*mesesTotais + 1 - (mes + duracao - 1)));
        retorno = 1/retorno;

        return retorno;
    }

    public double getCustoMesParaProjeto(int projeto){
        double retorno;// valor a ser retornado

        //informações do projeto
        int risco = this.listaProjetos.get(projeto).getRisco();
        int duracao = this.listaProjetos.get(projeto).getDuracao();

        //Formula da função J para mes -> projeto
        retorno =(double) (risco * duracao);
        retorno = 1/retorno;

        return  retorno;


    }
    public String getTipoCusto(int id){
        Projeto proj = this.listaProjetos.get(id);
        return proj.getClassificacao();
    }

    public boolean verificaRestricaoDeCusto(int id, int[] recursoDisponivel, int mes){
        int[] custoAnualProj = this.listaProjetos.get(id).getOrcamentoAnualDoProjeto(mes);

        if(mes > this.horizontePlanejamentoMeses) {//
            return true;
        }
        for(int anoRecursoDisponivel = mesesParaAno(mes), anoCustoAnualProj = 0;
        anoRecursoDisponivel < this.horizontePlanejamentoAnos && anoCustoAnualProj < custoAnualProj.length;
        anoRecursoDisponivel++, anoCustoAnualProj++){
            if((recursoDisponivel[anoRecursoDisponivel] - custoAnualProj[anoCustoAnualProj]) < 0){
                return false;
            }
        }
        return true;

    }
    boolean paradaCoincidem(int idProj, int mes, int idProj2, int mes2){
        Projeto projeto1, projeto2;
        int inicioManutencao1, fimManutencao1;
        int inicioManutencao2, fimManutencao2;
        projeto1 = listaProjetos.get(idProj);
        projeto2 = listaProjetos.get(idProj2);

        inicioManutencao1 = (projeto1.getComecoManutencao() - 1) + mes;
        fimManutencao1 = (projeto1.getDuracaoManutencao() - 1) + inicioManutencao1;

        inicioManutencao2 = (projeto2.getComecoManutencao() - 1) + mes2;
        fimManutencao2 = (projeto2.getDuracaoManutencao() - 1) + inicioManutencao2;

        return ((inicioManutencao1 <= inicioManutencao2 && fimManutencao1 >= fimManutencao2)||
                (inicioManutencao1 <= fimManutencao2 && fimManutencao1 >= fimManutencao2)||
                (inicioManutencao1 <= inicioManutencao2 && fimManutencao1 >= inicioManutencao2)||
                (inicioManutencao1 >= inicioManutencao2 && fimManutencao1 <= fimManutencao2));


    }

    boolean verificaRestricaoManutencao(int idProj, List<Integer> solucao, int mes){
        boolean podeAgendar = true;
        if(mes > this.horizontePlanejamentoMeses){
            return podeAgendar;
        }
        if(listaProjetos.get(idProj).verificaSeProjetoEhDeManutencao()) {
            MetodosGlobais mg = new MetodosGlobais();
            int idProj2, mes2;
            int posicaoListaManutencao = listaIdProjetosManutencao.indexOf(idProj);

            for (int i = 0; i < restricao[posicaoListaManutencao].length; i++){
                if (restricao[posicaoListaManutencao][i] == 1 &&
                        solucao.contains(mg.converteIDPPSParaComponente(this.listaIdProjetosManutencao.get(i), this.horizontePlanejamentoMeses))){
                    idProj2 = this.listaIdProjetosManutencao.get(i);
                    mes2 = mg.converteComponenteParaMesPPS(solucao.get(solucao.indexOf(mg.converteIDPPSParaComponente(idProj2, this.horizontePlanejamentoMeses)) + 1));//TODO funcao pegar agendamento com id e variaveis auxiliares
                    if(paradaCoincidem(idProj, mes, idProj2, mes2)){
                        podeAgendar = false;
                        break;
                    }
                }
            }
        }

        return podeAgendar;
    }

    //trasforma o mes do horizonte de planejamento em um ano
    public int mesesParaAno(int mes){
        return mes / MESES_EM_UM_ANO;
    }
    public void descontarRecursos(int projId, int mes, int[] recurso){


        int[] custoProj = this.listaProjetos.get(projId).getOrcamentoAnualDoProjeto(mes);
        for(int i = mesesParaAno(mes), j = 0; i < horizontePlanejamentoAnos && j < custoProj.length; i++,j++){
            recurso[i] -= custoProj[j];

        }

    }

    public int[] calculaOrcamentoDisponivel(List<Integer> solucao, String tipoCusto){
        MetodosGlobais mg = new MetodosGlobais();
        int[] recursoSobrando;
        int idAtual;

        if(tipoCusto.equals("CAPEX")){
            recursoSobrando = this.capex.clone();
        }else{
            recursoSobrando = this.opex.clone();
        }

        if (solucao.size() < 2){
            return recursoSobrando;
        }

        for (int componenteProj = 0, componenteMes = 1; componenteMes < solucao.size(); componenteMes+=2, componenteProj+=2) {
            idAtual = mg.converteComponenteParaIDPPS(solucao.get(componenteProj), this.horizontePlanejamentoMeses);
            if(getTipoCusto(idAtual).equals(tipoCusto)){
                descontarRecursos(idAtual, mg.converteComponenteParaMesPPS(solucao.get(componenteMes)), recursoSobrando);
            }
        }

        return recursoSobrando;
    }

}
