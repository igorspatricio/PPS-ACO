package isula.aco.pps;

import globais.MetodosGlobais;
import isula.aco.Ant;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FormigaPps extends Ant<Integer, AmbientePps> {
    private final int horizonteDePlanejamento, numeroDeProjetos;
    private static final int horizonteDeExecucao = 1;
    //Construtor
    public FormigaPps(int horizonteDePlanejamento, int numeroDeProjetos){
        super();
        this.setSolution(new ArrayList<Integer>());
        this.horizonteDePlanejamento = horizonteDePlanejamento;
        this.numeroDeProjetos = numeroDeProjetos;

    }

    //Limpa a solução da formiga para gerar uma proxima
    @Override
    public void clear() {
        super.clear();
        int componenteAleatorio = this.horizonteDePlanejamento + this.horizonteDeExecucao + new Random().nextInt(this.numeroDeProjetos);
        this.visitNode(componenteAleatorio,  null);


    }

    //Quando todos os projetos sejam agendados a solução esta completa
    @Override
    public boolean isSolutionReady(AmbientePps ambientePps){
        return getCurrentIndex()== (ambientePps.getNumeroDeProjetos() * 2 );
    }

    //Retorna o custo da solução
    @Override
    public double getSolutionCost(AmbientePps ambientePps, List<Integer> list) {
        //Valores necessarioa pra calcular J_psi
        double riscoTotal = (double) 3 * this.horizonteDePlanejamento * ambientePps.getRiscoTotal() ;
        double somatoria = 0;//somatoria do (J_psi¹,psi¹+1)^-1 para os projetos agendados

        //calcula a somatoria
        for(int i = 0; i < list.size(); i+=2){
            //J_(i, i+1) ^ -1
            somatoria += Math.pow(ambientePps.getCustoProjetoParaMes(list.get(i) - this.horizonteDePlanejamento,(list.get(i+1))+1), -1);
        }

        return (riscoTotal - somatoria);

    }

    //Funcao para determinar a o valor heuristico da ligação
    //valor de eta
    @Override
    public Double getHeuristicValue(Integer componenteDaSolucao, Integer posicaoNaSolucao, AmbientePps ambientePps) {
        double valorHeuristico = 0;
        int idProjeto;
        List<Integer> solucao = this.getSolution();

        if(componenteEhProjeto(posicaoNaSolucao)){  //calcula a heuristica de adcionar um mes
            idProjeto = MetodosGlobais.converteComponenteParaIDPPS(componenteDaSolucao,
                    this.horizonteDePlanejamento);
            valorHeuristico = 1 - ambientePps.getCustoMesParaProjeto(idProjeto); //tirar direto da tabela
        }else{// calcula a heuristica de acionar um projeto
            int mes = MetodosGlobais.converteComponenteParaMesPPS(componenteDaSolucao);
            int componenteAnterior = posicaoNaSolucao - 1;
            idProjeto = MetodosGlobais.converteComponenteParaIDPPS(solucao.get(componenteAnterior),
                    this.horizonteDePlanejamento);
            valorHeuristico = ambientePps.getHeuristica(solucao.get(componenteAnterior), componenteDaSolucao);
        }

        return valorHeuristico;
    }

    @Override
    public List<Integer> getNeighbourhood(AmbientePps ambientePps) {
        List<Integer> vizinhaca = new ArrayList<Integer>(), solucao = getSolution();
        int componenteAtual = getCurrentIndex() - 1;//index componente atual

        if(componenteEhProjeto(componenteAtual)){//Escolhe uma vizinhança de meses
            int idProjeto = MetodosGlobais.converteComponenteParaIDPPS(solucao.get(componenteAtual),
                    this.horizonteDePlanejamento);
            int mes;
            int[] recursoDisponivel = ambientePps.calculaOrcamentoDisponivel(solucao, ambientePps.getTipoCusto(idProjeto));

            for(int componenteMes = 0; componenteMes < this.horizonteDePlanejamento + this.horizonteDeExecucao; componenteMes++){
                mes = MetodosGlobais.converteComponenteParaMesPPS(componenteMes);
                if(ambientePps.verificaRestricaoDeCusto(idProjeto, recursoDisponivel, mes) &&
                        ambientePps.verificaRestricaoManutencao(idProjeto, solucao, mes)){
                    vizinhaca.add(componenteMes);
                }
            }
        }else{//Escolhe uma vizinhança de projeto
            for (int idProjeto : ambientePps.getListaProjetos().keySet()) {
                if(!(isNodeVisited(MetodosGlobais.converteIDPPSParaComponente(idProjeto, this.horizonteDePlanejamento)))){
                    vizinhaca.add(MetodosGlobais.converteIDPPSParaComponente(idProjeto, this.horizonteDePlanejamento));
                }
            }
        }

        return vizinhaca;
    }

    @Override
    public Double getPheromoneTrailValue(Integer componenteDaSolucao, Integer posicaoNaSolucao, AmbientePps ambientePps) {
        if(posicaoNaSolucao == 0){
            double[][] matriz = ambientePps.getPheromoneMatrix();
            return matriz[componenteDaSolucao][componenteDaSolucao];
        }else{
            int componenteAnterior = this.getSolution().get(posicaoNaSolucao - 1);
            double[][] matriz = ambientePps.getPheromoneMatrix();
            return matriz[componenteAnterior][componenteDaSolucao];
        }
    }

    @Override
    public void setPheromoneTrailValue(Integer componenteDaSolucao, Integer posicaoNaSolucao, AmbientePps ambientePps, Double novoPheromonio) {
        if(posicaoNaSolucao == 0) {
            double[][] matriz = ambientePps.getPheromoneMatrix();
            matriz[componenteDaSolucao][componenteDaSolucao] = novoPheromonio;
        }else{
            int componenteAnterior = this.getSolution().get(posicaoNaSolucao - 1);
            double[][] matriz = ambientePps.getPheromoneMatrix();
            matriz[componenteAnterior][componenteDaSolucao] = novoPheromonio;
        }
    }
    //--------------------------------------------Metodos extras--------------------------------------------//
    private boolean componenteEhProjeto(int index){
        return index % 2 == 0;

    }





}
