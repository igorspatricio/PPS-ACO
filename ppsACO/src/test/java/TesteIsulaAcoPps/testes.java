package TesteIsulaAcoPps;

import PPS.PPS;
import PPS.Projeto;
import PPS.Utilidades;
import isula.aco.pps.AmbientePps;
import isula.aco.pps.FormigaPps;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class testes {

        public static void main(String[] args) {
            JSONObject jsonObject;
            JSONObject teste;
            Utilidades ut = new Utilidades();
            //Cria o parse de tratamento
            JSONParser parser = new JSONParser();
            //Variaveis que irao armazenar os dados do arquivo JSON


            try {
                //Salva no objeto JSONObject o que o parse tratou do arquivo
                jsonObject = (JSONObject) parser.parse(new FileReader(
                        "src\\main\\resources\\experimento200.json"));
                AmbientePps pps = new AmbientePps(jsonObject);
                FormigaPps formiga = new FormigaPps(pps.getHorizontePlanejamentoMeses(), pps.getNumeroDeProjetos());
                double[][] representacao = pps.getPheromoneMatrix();
                pps.populatePheromoneMatrix(1);
                HashMap<Integer, Projeto> listaProj = pps.getListaProjetos();
                HashMap<Integer, Projeto> projetos = pps.getListaProjetos();
                Random rand = new Random();
                formiga.clear();
                List<Integer> vizinhanca;
                int projeto = 61;
                int count = 0;
                formiga.visitNode(0, pps);

                while(!(formiga.isSolutionReady(pps))){
                    vizinhanca = formiga.getNeighbourhood(pps);
                    formiga.visitNode(vizinhanca.get(0), pps);
                    formiga.visitNode(0, pps);
                }
                List<Integer> solucao = formiga.getSolution();
                PPS PPS = new PPS();
                PPS.transformaSolucaoACOParaSolucaoPPS(solucao, pps.getHorizontePlanejamentoMeses());

                System.out.println();

                System.out.println(formiga.getSolutionCost(pps, solucao));

                double minFO, maxFO, mediaFO = 0, razaoLFO, mediaTE = 0, valorfuncObjt;

                jsonObject = (JSONObject) parser.parse(new FileReader(
                        "src\\main\\resources\\solucao200.json"));

                JSONArray list = (JSONArray) jsonObject.get("ListaSolucoes");
                JSONObject objetoSolucao;
                objetoSolucao = (JSONObject) list.get(0);
                minFO = (double) objetoSolucao.get("BesSolutinCost");
                maxFO = (double) objetoSolucao.get("BesSolutinCost");
                for (Object i :
                        list) {
                    objetoSolucao = (JSONObject) i;
                    valorfuncObjt = (double) objetoSolucao.get("BesSolutinCost");

                    mediaFO += valorfuncObjt;

                    if (minFO > valorfuncObjt){
                        minFO = valorfuncObjt;
                    }
                    if (maxFO < valorfuncObjt){
                        maxFO = valorfuncObjt;
                    }

                    mediaTE += Double.parseDouble((String) objetoSolucao.get(" executionTime"));
                }
                mediaFO = mediaFO/list.size();
                mediaTE = mediaTE/list.size();
                razaoLFO = mediaFO/formiga.getSolutionCost(pps, solucao);

                System.out.printf("%2f & %2f & %2f & %2f & %2f", minFO
                        , maxFO
                        , mediaFO
                        , razaoLFO
                        , mediaTE

                );
                //System.out.println(formiga.getNeighbourhood(pps));



                //System.out.println(formiga.getSolutionCost(pps, formiga.getSolution()));



                /*
                for(double[] i : representacao)
                {

                    for(double j : i)
                    {
                        System.out.print(j+" - ");
                    }
                    System.out.println(' ');
                }*/
//                System.out.println(pps.toString());
//                System.out.println(Math.pow(pps.getCustoProjetoParaMes(0,12),-1));




            }
            //Trata as exceptions que podem ser lançadas no decorrer do processo
            catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


