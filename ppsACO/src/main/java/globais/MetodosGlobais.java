package globais;

public class MetodosGlobais {

    public static int converteComponenteParaIDPPS(int numeroComponente, int horizontePlanejamento){
        return numeroComponente - horizontePlanejamento;
    }
    public static int converteComponenteParaMesPPS(int mesACO){
        return mesACO + 1;
    }
    public static int converteIDPPSParaComponente(int id, int horizontePlanejamento){
        return horizontePlanejamento + id;
    }
}
