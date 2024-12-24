import java.io.*;
import java.util.*;

public class GA {
    private static final int[][] MAP = new int[9][9]; // Sudoku grid
    private static final int EMPTY = 0; // Represents empty cells in the Sudoku grid
    private static double MAX_FITNESS = 0;
    private static double MEAN_FITNESS = 0;
    private static int NUMBER_OF_GENERATIONS = 0;

    // Class for generating the Sudoku puzzle
    static class GeneratorOfGameMaps {
        public static int[][] generation() {
            // Initialize the grid with 0s (empty cells)
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    MAP[i][j] = 0;
                }
            }
            // Fill the grid with a valid Sudoku solution
            fillGrid(0, 0);
            // Output the generated grid to a file
            printMapFile(MAP);
            // Add some empty spaces to the grid
            Create_Spaces();
            return MAP;
        }

        // Recursively fills the grid with valid numbers
        public static boolean fillGrid(int row, int column) {
            if (column == 9) {
                row++;
                column = 0;
            }
            if (row == 9) {
                return true;
            }
            int[] numbers = randomizeNumbers();
            for (int num : numbers) {
                if (isAvailable(row, column, num)) {
                    MAP[row][column] = num;
                    if (fillGrid(row, column + 1)) {
                        return true;
                    }
                    MAP[row][column] = EMPTY; // Backtrack if no valid number is found
                }
            }
            return false;
        }

        // Randomizes the numbers 1 to 9
        public static int[] randomizeNumbers() {
            int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9};
            Random random = new Random();
            for (int i = numbers.length - 1; i > 0; i--) {
                int index = random.nextInt(i + 1);
                int temp = numbers[index];
                numbers[index] = numbers[i];
                numbers[i] = temp;
            }
            return numbers;
        }

        // Checks if a number can be placed at a specific position
        public static boolean isAvailable(int row, int column, int number) {
            for (int i = 0; i < 9; i++) {
                if (MAP[row][i] == number || MAP[i][column] == number) {
                    return false;
                }
            }
            int subRow = (row / 3) * 3;
            int subCol = (column / 3) * 3;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (MAP[subRow + i][subCol + j] == number) {
                        return false;
                    }
                }
            }
            return true;
        }

        // Creates random empty spaces on the grid
        public static void Create_Spaces() {
            Random random = new Random();
            int emptySize = random.nextInt(41, 61); // Random number of empty cells
            for (int i = 0; i < emptySize; i++) {
                int row = random.nextInt(9);
                int col = random.nextInt(9);
                MAP[row][col] = EMPTY; // Set selected cells to empty
            }
        }

        // Prints the Sudoku grid to a file
        public static void printMapFile(int[][] GameMap) {
            try {
                FileWriter writer = new FileWriter("output.txt", true);
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (GameMap[i][j] == EMPTY) {
                            writer.write("-" + " ");
                        } else {
                            writer.write(GameMap[i][j] + " ");
                        }
                    }
                    writer.write("\n");
                }
                writer.write("\n");
                writer.close();
            } catch (IOException e) {
                System.out.println("writing file failed");
            }
        }
    }

    // Sudoku Solver using Genetic Algorithm
    public class SudokuSolver {
        private static final List<Pair> blanks = new ArrayList<>();
        private static final int[][] GameField = new int[9][9];
        private static final int POPULATION_SIZE = 100000; // Increased population size
        private static final int MAX_GENERATIONS = 500; // Maximum number of generations
        private static final int STAGNATION_LIMIT = 10; // Limit for fitness stagnation

        // Class representing a pair of coordinates (row, col)
        static class Pair {
            int row, col;

            public Pair(int row, int col) {
                this.row = row;
                this.col = col;
            }
        }

        // Class representing an individual in the population (Sudoku grid)
        static class Individual {
            int[][] values;
            double fitness;

            public Individual(int[][] values) {
                this.values = values;
            }
        }

        // Main function to solve the Sudoku puzzle using GA
        public static int[][] solveSudoku(int[][] InputField) {
            for (int i = 0; i < 9; i++) {
                System.arraycopy(InputField[i], 0, GameField[i], 0, 9);
            }
            blanksFinding();
            List<Individual> currentGeneration = GenerationCreation();
            calculateFitnessForGeneration(currentGeneration);
            int generationCount = 0;
            int stagnationCount = 0;
            double bestFitness = currentGeneration.get(0).fitness;
            while (generationCount < MAX_GENERATIONS) {
                if (currentGeneration.get(0).fitness == 0) {
                    MEAN_FITNESS = meanFitness(currentGeneration);
                    MAX_FITNESS = currentGeneration.get(currentGeneration.size()-1).fitness;
                    NUMBER_OF_GENERATIONS = generationCount;
                    return currentGeneration.get(0).values; // Solution found
                }
                currentGeneration = nextGeneration(currentGeneration);
                calculateFitnessForGeneration(currentGeneration);
                double currentBestFitness = currentGeneration.get(0).fitness;
                if (currentBestFitness == bestFitness) {
                    stagnationCount++;
                } else {
                    stagnationCount = 0;
                    bestFitness = currentBestFitness;
                }
                if (stagnationCount >= STAGNATION_LIMIT) {
                    currentGeneration = GenerationCreation(); // Restart if stagnation occurs
                    calculateFitnessForGeneration(currentGeneration);
                    stagnationCount = 0;
                }
                generationCount++;
            }
            return GameField; // Return the current grid if no solution is found within max generations
        }

        public static double meanFitness(List<Individual> generation) {
            double result = 0;
            for (Individual individual : generation) {
                result += individual.fitness;
            }
            return result / generation.size();
        }

        // Finds the blank (empty) cells in the grid
        private static void blanksFinding() {
            blanks.clear();
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (GameField[i][j] == 0) {
                        blanks.add(new Pair(i, j)); // Add blank cells to the list
                    }
                }
            }
        }

        // Creates the initial population of individuals (Sudoku grids)
        private static List<Individual> GenerationCreation() {
            List<Individual> generation = new ArrayList<>();
            Random random = new Random();
            for (int i = 0; i < POPULATION_SIZE; i++) {
                int[][] individualValues = new int[9][9];
                for (int row = 0; row < 9; row++) {
                    System.arraycopy(GameField[row], 0, individualValues[row], 0, 9);
                }
                // Fill in the blank cells with valid numbers
                for (Pair blank : blanks) {
                    int attempts = 0;
                    while (attempts < 30) {
                        int number = random.nextInt(1, 10);
                        if (isAvailable(individualValues, blank.row, blank.col, number)) {
                            individualValues[blank.row][blank.col] = number;
                            break;
                        }
                        attempts++;
                    }
                }
                generation.add(new Individual(individualValues));
            }
            return generation;
        }

        // Checks if a number can be placed at a given position in the grid
        private static boolean isAvailable(int[][] grid, int row, int col, int number) {
            for (int i = 0; i < 9; i++) {
                if (grid[row][i] == number || grid[i][col] == number) {
                    return false;
                }
            }
            int subRow = (row / 3) * 3, subCol = (col / 3) * 3;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (grid[subRow + i][subCol + j] == number) {
                        return false;
                    }
                }
            }
            return true;
        }

        // Method to calculate the fitness for each individual in the current generation.
// Fitness is calculated based on the number of repeated digits in rows, columns, and subgrids.
// After calculating fitness, the generation is sorted in ascending order of fitness.
        private static void calculateFitnessForGeneration(List<Individual> generation) {
            for (Individual individual : generation) {
                individual.fitness = calculateFitness(individual.values);
            }
            generation.sort(Comparator.comparingDouble(ind -> ind.fitness));
        }

        // Method to compute the fitness value of a given Sudoku grid.
// Fitness is computed as the squared sum of errors in rows, columns, and 3x3 subgroups.
        private static double calculateFitness(int[][] values) {
            int rowErrors = 0, colErrors = 0, subGridErrors = 0;

            // Count errors in rows and columns
            for (int i = 0; i < 9; i++) {
                rowErrors += (9 - uniqueCount(values[i]));
                colErrors += (9 - uniqueCount(getColumn(values, i)));
            }
            // Count errors in 3x3 subgrids
            for (int i = 0; i < 9; i += 3) {
                for (int j = 0; j < 9; j += 3) {
                    subGridErrors += (9 - uniqueCount(getSubGrid(values, i, j)));
                }
            }
            // Return the squared sum of row, column, and subgrid errors
            return Math.pow(rowErrors, 2) + Math.pow(colErrors, 2) + Math.pow(subGridErrors, 2);
        }

        // Helper method to extract a specific column from the Sudoku grid.
        private static int[] getColumn(int[][] grid, int col) {
            int[] column = new int[9];
            for (int i = 0; i < 9; i++) {
                column[i] = grid[i][col];
            }
            return column;
        }

        // Helper method to extract a 3x3 subgrid from the Sudoku grid, starting at a given row and column.
        private static int[] getSubGrid(int[][] grid, int startRow, int startCol) {
            int[] subGrid = new int[9];
            int idx = 0;
            for (int i = startRow; i < startRow + 3; i++) {
                for (int j = startCol; j < startCol + 3; j++) {
                    subGrid[idx++] = grid[i][j];
                }
            }
            return subGrid;
        }

        // Helper method to count the number of unique numbers in an array (ignoring zeros).
        private static int uniqueCount(int[] array) {
            Set<Integer> unique = new HashSet<>();
            for (int num : array) {
                if (num > 0) {
                    unique.add(num);
                }
            }
            return unique.size();
        }

        // Method to generate the next generation of individuals.
// The method selects elites, performs crossover, and applies mutations to create new individuals.
        private static List<Individual> nextGeneration(List<Individual> currentGeneration) {
            List<Individual> nextGen = new ArrayList<>();
            Random random = new Random();
            // Preserve the top eliteCount individuals
            int eliteCount = Math.min(25000, currentGeneration.size());
            nextGen.addAll(currentGeneration.subList(0, eliteCount));
            // Generate new individuals until the population size is reached
            while (nextGen.size() < POPULATION_SIZE) {
                Individual parent1 = selectParent(currentGeneration);
                Individual parent2 = selectParent(currentGeneration);
                // Perform crossover to create a child
                Individual child = crossover(parent1, parent2);
                // Apply mutation with a probability of 70%
                if (random.nextDouble() < 0.65) {
                    mutate(child);
                }
                nextGen.add(child);
            }
            return nextGen;
        }

        // Method to select a parent for crossover using a tournament selection mechanism.
        private static Individual selectParent(List<Individual> generation) {
            Random random = new Random();
            Individual best = null;
            // Randomly select 25 candidates and choose the one with the best fitness
            for (int i = 0; i < 25; i++) {
                Individual candidate = generation.get(random.nextInt(generation.size()));
                if (best == null || candidate.fitness < best.fitness) {
                    best = candidate;
                }
            }

            return best;
        }

        // Method to perform crossover between two parent individuals to produce a child.
        private static Individual crossover(Individual parent1, Individual parent2) {
            int[][] childValues = new int[9][9];
            Random random = new Random();

            // Each cell in the child is randomly inherited from either parent1 or parent2
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    childValues[i][j] = random.nextBoolean() ? parent1.values[i][j] : parent2.values[i][j];
                }
            }
            return new Individual(childValues);
        }

        // Method to apply mutation to an individual by modifying values in the blank cells.
        private static void mutate(Individual individual) {
            Random random = new Random();
            for (Pair blank : blanks) {
                if (random.nextDouble() < 3) { // Mutation probability
                    int currentNumber = individual.values[blank.row][blank.col];
                    individual.values[blank.row][blank.col] = 0; // Temporarily clear the cell
                    int attempts = 0;
                    boolean mutated = false;
                    // Try to assign a new valid number to the cell
                    while (attempts < 20) {
                        int newNumber = random.nextInt(1, 10);
                        if (isAvailable(individual.values, blank.row, blank.col, newNumber)) {
                            individual.values[blank.row][blank.col] = newNumber;
                            mutated = true;
                            break;
                        }
                        attempts++;
                    }
                    // If mutation fails, revert to the original number
                    if (!mutated) {
                        individual.values[blank.row][blank.col] = currentNumber;
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        double[] MAXes_FITNESS = new double[400];
        double[] Means = new double[400];
        double[] runTimes = new double[400];
        int[] numberOfGeneration = new int[400];

        String filePath = "input.txt"; // Укажите путь к файлу с данными

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int taskCount = 0;

            while ((line = reader.readLine()) != null && taskCount < 400) {
                // Пропускаем строки, пока не найдем начало задачи
                if (!line.startsWith("Grid")) {
                    continue;
                }

                int filling = 0;
                int[][] GameMap = new int[9][9];
                int gridRow = 0;

                // Считываем сетку судоку
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Solution:")) {
                        break; // Завершаем чтение сетки
                    }

                    String[] cells = line.trim().split(" ");
                    for (int j = 0; j < 9; j++) {
                        if (cells[j].equals("-")) {
                            GameMap[gridRow][j] = 0; // 0 обозначает пустую ячейку
                            filling++;
                        } else {
                            GameMap[gridRow][j] = Integer.parseInt(cells[j]);
                        }
                    }
                    gridRow++;
                    if (gridRow == 9) break; // Завершаем чтение после заполнения 9 строк
                }

                // Обработка задачи
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
                    writer.write("Task " + (taskCount + 1) + ":\n");
                    GeneratorOfGameMaps.printMapFile(GameMap); // Печать изначальной карты

                    long startTime = System.nanoTime();
                    int[][] result = SudokuSolver.solveSudoku(GameMap); // Решение судоку
                    long endTime = System.nanoTime();

                    double executionTimeInSeconds = (endTime - startTime) / 1_000_000_000.0;

                    writer.write("Solution " + (taskCount + 1) + ":\n");
                    GeneratorOfGameMaps.printMapFile(result); // Печать решённой карты

                    MAXes_FITNESS[taskCount] = MAX_FITNESS;
                    Means[taskCount] = MEAN_FITNESS;
                    runTimes[taskCount] = executionTimeInSeconds;
                    numberOfGeneration[taskCount] = NUMBER_OF_GENERATIONS;

                    writer.write("number of generation: " + NUMBER_OF_GENERATIONS + "\n");
                    writer.write("Number of empty cells: " + filling + "\n");
                    writer.write("MAX of fitness in last generation: " + MAX_FITNESS + "\n");
                    writer.write("Mean of fitness in last generation: " + MEAN_FITNESS + "\n");
                    writer.write("Execution time (seconds): " + executionTimeInSeconds + "\n");
                    writer.write("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                taskCount++;
            }
        }

        // Финальная запись результатов
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true))) {
            writer.write("Runtimes:\n");
            for (double runTime : runTimes) {
                writer.write(runTime + ", ");
            }
            writer.write("\n");

            writer.write("Means:\n");
            for (double mean : Means) {
                writer.write(mean + ", ");
            }
            writer.write("\n");

            writer.write("MAX:\n");
            for (double maxFitness : MAXes_FITNESS) {
                writer.write(maxFitness + ", ");
            }
            writer.write("\n");

            writer.write("Number of generation:\n");
            for (int generations : numberOfGeneration) {
                writer.write(generations + ", ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}