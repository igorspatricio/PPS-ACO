package PPS;

import isula.aco.ConfigurationProvider;

public class ConfiguracaoAcoPPS implements ConfigurationProvider {
    @Override
    public int getNumberOfAnts() {
        return 5000;
    }

    @Override
    public double getEvaporationRatio() {
        return 1 - 0.2;
    }

    @Override
    public int getNumberOfIterations() {
        return 100;
    }

    @Override
    public double getInitialPheromoneValue() {

        return 3;
    }

    @Override
    public double getHeuristicImportance() {
        return 1.5;
    }

    @Override
    public double getPheromoneImportance() {
        return 4;
    }
}
