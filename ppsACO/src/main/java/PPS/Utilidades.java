package PPS;


public class Utilidades {
//string para vetor de inteiros
    public int[] stringVetor(String lista)
    {
        //separa os numeros da String e os deixa em um vetor de string
        String[] stringsSeparadas = lista.replaceAll("\\[", "")
                .replaceAll("]", "").split(",");

        //cria um vetor vazio com o tamanho do vetor de string
        int[] intVetor = new int[stringsSeparadas.length];

        //transformam os itens de stringSeparadas em inteiros
        for (int i = 0; i < stringsSeparadas.length; i++) {

            try {
                intVetor[i] = Integer.parseInt(stringsSeparadas[i]);
            } catch (Exception e) {
                System.out.println("Unable to parse string to int: " + e.getMessage());
            }
        }

        return intVetor;
    }

    //string para matriz ( funciona apenas para matrizes quadradas)
    public int[][] stringMatriz(String matriz){
        //separa os numeros da String e os deixa em um vetor de string
        String[] stringsSeparadas = matriz.replaceAll("\\[", "")
                .replaceAll("]", "").split(",");

        int k = 0;

        //determi na o tamanho da matriz
        int tamanhoMatriz = (int) Math.sqrt(stringsSeparadas.length);

        //cria a matriz vazia quadrada
        int[][] matrizRetorno = new int[tamanhoMatriz][tamanhoMatriz];

        //adiciona os numeros na matriz
        for(int i = 0; i < tamanhoMatriz; i++){
            for(int j = 0; j < tamanhoMatriz; j++) {
                matrizRetorno[i][j] = Integer.parseInt(stringsSeparadas[k]);
                k++;
            }
        }

        return matrizRetorno;
    }
}
