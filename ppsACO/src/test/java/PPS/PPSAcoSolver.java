package PPS;

import isula.aco.*;
import isula.aco.Ant;
import isula.aco.AntColony;

import isula.aco.algorithms.antsystem.OfflinePheromoneUpdate;
import isula.aco.algorithms.antsystem.PerformEvaporation;
import isula.aco.algorithms.antsystem.RandomNodeSelection;
import isula.aco.algorithms.antsystem.StartPheromoneMatrix;
import isula.aco.pps.AmbientePps;
import isula.aco.pps.FormigaPps;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;

import javax.naming.ConfigurationException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PPSAcoSolver {
    public static void main(String[] args) {
        int numeroDeExecucoes = 10;

        JSONObject jsonObject;
        JSONObject teste;
        Utilidades ut = new Utilidades();
        //Cria o parse de tratamento
        JSONParser parser = new JSONParser();

        //Variaveis que irao armazenar os dados do arquivo JSON
        JSONObject solucoes = new JSONObject();
        JSONObject solucoesIndividuais = new JSONObject();
        JSONArray listaSolucoesJson = new JSONArray();

        try {
            //Salva no objeto JSONObject o que o parse tratou do arquivo
            jsonObject = (JSONObject) parser.parse(new FileReader(
                    "src\\main\\resources\\experimento200.json"));

            AmbientePps prob = new AmbientePps(jsonObject);
            List<Double> listaSolucoes = new ArrayList<Double>();

            //Execução do experimento que dentro de um for para poder ser feito n vezes
            for (int i = 0; i < numeroDeExecucoes; i++){
                //Execução
                ConfiguracaoAcoPPS config = new ConfiguracaoAcoPPS();
                AntColony<Integer, AmbientePps> colonia = getAntColony(config);

                AcoProblemSolver<Integer, AmbientePps> solver = new AcoProblemSolver<>();
                solver.initialize(prob, colonia, config);
                solver.addDaemonActions(new StartPheromoneMatrix<>(),
                        new PerformEvaporation<>());

                solver.addDaemonActions(getPheromoneUpdatePolicy());

                solver.getAntColony().addAntPolicies(new RandomNodeSelection<>());
                solver.solveProblem();
                //fim da execução do experimento

                //Armazena os resultados em variaveis para ser armazenadas em json
                solucoesIndividuais.put("BestSolution", solver.getBestSolutionAsString());
                solucoesIndividuais.put("BesSolutinCost", solver.getBestSolutionCost());
                solucoesIndividuais.put(solver.toString().split("=")[2].split(",")[1], solver.toString().split("=")[3].split(",")[0]);

                listaSolucoesJson.add(solucoesIndividuais);
                solucoesIndividuais = new JSONObject();
            }

            //armazena as soluções criadas em um Obj json
            solucoes.put("ListaSolucoes", listaSolucoesJson);
            for (double i :
                    listaSolucoes) {
                System.out.print(i+ "\t");
            }

            //excreve as soluções para um arquivo json
            try (FileWriter file = new FileWriter("src\\main\\resources\\solucao200.json")) {
                file.write(solucoes.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }

//            PPS solucao = new PPS();
//            solucao.transformaSolucaoACOParaSolucaoPPS(solver.getBestSolution(), prob.getHorizontePlanejamentoMeses());
//            solucao.mostraSolucao();
//            System.out.println("Custo da solucao: " + solver.getBestSolutionCost());
            /*
            181000 ~~ 183000 valores achados normalmente pelo aco
            180916.0 melhor achado pelo aco
            104198.0 todos os projetos agendados no mes 1
            195278.0 todos os projetos agendados no mes 61
             */

        }
        //Trata as exceptions que podem ser lançadas no decorrer do processo
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }
    public static AntColony<Integer, AmbientePps> getAntColony(final ConfiguracaoAcoPPS configurationProvider) {
        return new AntColony<Integer, AmbientePps>(configurationProvider.getNumberOfAnts()) {
            @Override
            protected Ant<Integer, AmbientePps> createAnt(AmbientePps environment) {
                return new FormigaPps(environment.getHorizontePlanejamentoMeses(), environment.getNumeroDeProjetos());
            }
        };
    }

    private static DaemonAction<Integer, AmbientePps> getPheromoneUpdatePolicy() {
        return new OfflinePheromoneUpdate<Integer, AmbientePps>() {
            @Override
            protected double getPheromoneDeposit(Ant<Integer, AmbientePps> ant,
                                                 Integer positionInSolution,
                                                 Integer solutionComponent,
                                                 AmbientePps environment,
                                                 ConfigurationProvider configurationProvider) {
                return 1 / ant.getSolutionCost(environment);
            }
        };
    }


//-------------------------------------verificações-------------------------------------------------------------------//


}

