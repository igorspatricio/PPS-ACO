package PPS;

import org.json.simple.JSONObject;

import java.util.Arrays;

public class Projeto {

    int id;
    int risco;
    int duracao;
    String classificacao;
    char tipo;
    int duracaoManutencao, comecoManutencao;
    int[] custo;//custo mensal
    final int MESES_EM_UM_ANO = 12;

    //contrutor
    public Projeto(JSONObject proj) {
        this.risco = (int) (long) proj.get("Risk");
        this.id = (int) (long) proj.get("Id");
        this.duracao = (int) (long) proj.get("Duration");
        this.classificacao = (String)proj.get("Classification");
        this.tipo =  ((String) proj.get("Maintenance")).charAt(0);
        this.duracaoManutencao = (int) (long) proj.get("Halting Duration");
        this.comecoManutencao = (int) (long) proj.get("Halting Start Month");

        //----------------------String -> array---------------------------//
        Utilidades utilidade = new Utilidades();
        this.custo = utilidade.stringVetor(proj.get("Costs").toString());
    }

    public int getRisco() {
        return risco;
    }

    public int getId() {
        return id;
    }

    public int getDuracao() {
        return duracao;
    }

    public String getClassificacao() {
        return classificacao;
    }

    public char getTipo() {
        return tipo;
    }

    public int getDuracaoManutencao() {
        return duracaoManutencao;
    }

    public int getComecoManutencao() {
        return comecoManutencao;
    }

    public int[] getCusto() {
        return custo;
    }

    public  int getCustoTotal()
    {
        int total = 0;
        for (int i: this.custo)
           total += i;
        return total;
    }

    public int getMesDoAno(int mes){
        if(mes % MESES_EM_UM_ANO == 0){
            return MESES_EM_UM_ANO;
        }else{
            return mes%MESES_EM_UM_ANO;
        }
    }

    public int getDuracaoDoProjetoEmAnos(int mes){
        int ano;
        ano = getMesDoAno(mes);
        ano += this.duracao-1;
        if(ano%12 ==0){
            ano = ano/MESES_EM_UM_ANO;
        }else{
            ano = ano/MESES_EM_UM_ANO + 1;
        }
        return ano;
    }

//Orçamento anual de um projeto dado que foi agendado para iniciar no mes da variavel mes
    public int[] getOrcamentoAnualDoProjeto(int mes)// TODO adicionar this. para metodos da classe
    {
        //constantes meses
        int[] custoPorAno;

        //Se um projeto é agendado e finalizado no mesmo ano o custo é tudo em uma variavel
        if( (getMesDoAno(mes) + this.duracao) < MESES_EM_UM_ANO + 1){
            custoPorAno = new    int[1];
            custoPorAno[0] = getCustoTotal();
        }else{
            custoPorAno = new int[this.getDuracaoDoProjetoEmAnos(mes)];
            custoPorAno[0] = 0;
            mes = getMesDoAno(mes);

            for(int mesCusto = 0, anoCusto = 0; mesCusto < this.duracao; mesCusto++){
                if(mes > MESES_EM_UM_ANO)
                {
                    anoCusto++;
                    custoPorAno[anoCusto] = 0;
                    mes = 1;
                }
                custoPorAno[anoCusto] += this.custo[mesCusto];
                mes++;
            }
        }
        return custoPorAno;
    }

    public boolean verificaSeProjetoEhDeManutencao(){
        if(tipo == 'L'){
            return true;
        }

        return false;
    }
    @Override
    public String toString() {
        return "Projeto{ " +
                "id=" + id +
                "Risco="+ risco +
                ", duracao=" + duracao +
                ", classificacao='" + classificacao + '\'' +
                ", tipo=" + tipo +
                ", duracaoManutencao=" + duracaoManutencao +
                ", comecoManutencao=" + comecoManutencao +
                ", custo=" + Arrays.toString(custo) +
                "}\n";
    }
}
