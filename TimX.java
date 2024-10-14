package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.example.Tinhfx;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

public class TimX extends JFrame {
    private String str;

    public TimX(String str) {
        this.str = str;
    }

    private static final float INF = 999;

    public static float func(float x, String str) {
        Tinhfx instance = new Tinhfx();
        double result = instance.exp(str, x);
        return Math.abs((float) result);
    }

    private static float ieeeToDecimal(String ieeeBinary) {
        long longValue = Long.parseLong(ieeeBinary, 2);

        int sign = (int) ((longValue >> 31) & 1);
        int exponent = (int) ((longValue >> 23) & 0xFF);
        long fraction = longValue & 0x7FFFFF;

        int bias = 127;

        int actualExponent = exponent - bias;

        float fractionValue = 1.0f;
        for (int i = 22; i >= 0; --i) {
            fractionValue += ((fraction >> i) & 1) * Math.pow(2, i - 23);
        }

        float result;
        if (exponent == 0 && fraction == 0) {
            result = 0.0f;
        } else if (exponent == 255 && fraction == 0) {
            result = (float) ((sign == 0) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY);
        } else if (exponent == 255 && fraction != 0) {
            result = Float.NaN;
        } else {
            result = (float) (Math.pow(-1, sign) * fractionValue * Math.pow(2, actualExponent));
        }

        return result;
    }

    private static List<String> initPopulation(int populationSize) {
        List<String> population = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < populationSize; i++) {
            StringBuilder individual = new StringBuilder();
            for (int j = 0; j < 32; ++j) {
                char bit = (char) ('0' + random.nextInt(2));
                individual.append(bit);
            }
            population.add(individual.toString());
        }

        return population;
    }

    private static String crossover(String parent1, String parent2) {
        int crossoverPoint = new Random().nextInt(parent1.length());
        String child = parent1.substring(0, crossoverPoint) + parent2.substring(crossoverPoint);
        return child;
    }

    private static void mutate(StringBuilder individual, double mutationRate) {
        Random random = new Random();
        for (int i = 0; i < individual.length(); i++) {
            double mutationChance = random.nextDouble();

            if (mutationChance < mutationRate) {
                char bit = (individual.charAt(i) == '0') ? '1' : '0';
                individual.setCharAt(i, bit);
            }
        }
    }

    static class Pair {
        private final String first;
        private final float second;

        public Pair(String first, float second) {
            this.first = first;
            this.second = second;
        }

        public String getFirst() {
            return first;
        }

        public float getSecond() {
            return second;
        }
    }

    public String main() {
        XYSeries series = new XYSeries("f(x)");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        int maxGeneration = 10;
        int populationSize = 10000;
        List<String> population = initPopulation(populationSize);
        float bestFitness = INF;
        float solution = 0;
        int generation = 0;

        while (generation < maxGeneration) {
            double mutationRate = 0.05 * (maxGeneration - generation) / maxGeneration;
            List<Pair> funcVal = new ArrayList<>();

            for (String individual : population) {
                float fitness = func(ieeeToDecimal(individual), str);
                funcVal.add(new Pair(individual, fitness));
            }

            Collections.sort(funcVal, (a, b) -> Float.compare(a.getSecond(), b.getSecond()));
            series.add(generation, funcVal.get(0).getSecond());
            int newPopulationSize = populationSize / 4;
            List<String> newPopulation = new ArrayList<>();

            for (int i = 0; i < newPopulationSize; ++i) {
                newPopulation.add(funcVal.get(i).getFirst());
            }

            for (int i = newPopulationSize; i < populationSize; ++i) {
                int parentIndex1 = new Random().nextInt(newPopulationSize);
                int parentIndex2 = new Random().nextInt(newPopulationSize);

                String parent1 = newPopulation.get(parentIndex1);
                String parent2 = newPopulation.get(parentIndex2);

                String child = crossover(parent1, parent2);

                newPopulation.add(child);
            }

            for (int i = 0; i < populationSize; ++i) {
                StringBuilder individual = new StringBuilder(newPopulation.get(i));
                mutate(individual, mutationRate);
                newPopulation.set(i, individual.toString());
            }

            population = newPopulation;
            ++generation;
        }

        for (String individual : population) {
            float currentFitness = func(ieeeToDecimal(individual), str);
            if (currentFitness < bestFitness) {
                bestFitness = currentFitness;
                solution = ieeeToDecimal(individual);
            }
        }
        String ketquax;

        if (bestFitness < 0.01) {
            ketquax = "Best Fitness: " + bestFitness + "\nSolution: " + solution;
        } else {
            ketquax = "vo nghiem";
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Biểu đồ Hàm Số",
                "X",
                "f(X)",
                dataset
        );
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 370));
        setContentPane(chartPanel);
        chartPanel.setVisible(true);

        setVisible(true);
        return ketquax;
    }
}
