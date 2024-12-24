import java.util.Scanner;

/**
 * BacktrackingAttempt2 implements a backtracking algorithm for Neo to navigate
 * through a 9x9 grid while avoiding dangers and reaching the Keymaker.
 */
public class Backtracking {

    private static final int SIZE = 9; // Size of the grid
    private static final char EMPTY = '.'; // Represents an empty cell
    private static final char AGENT_SMITH = 'A'; // Represents Agent Smith
    private static final char BACKDOOR_KEY = 'B'; // Represents the backdoor key
    private static final char SENTINEL = 'S'; // Represents a Sentinel
    private static final char KEYMAKER = 'K'; // Represents the Keymaker
    private static final char NEO = 'N'; // Represents Neo
    private static final char PERCEPTIONZONE = 'P'; // Represents perception zones

    // Possible moves: down, left, right, up
    static int[][] moves = new int[][]{{1, 0}, {0, -1}, {0, 1}, {0, -1}};

    static boolean[][] visited = new boolean[SIZE][SIZE]; // Track visited cells
    static char[][] field = new char[SIZE][SIZE]; // The game grid
    static int MIN_PATH = 1000000; // Initialize minimum path length
    static int goalX; // X-coordinate of the goal
    static int goalY; // Y-coordinate of the goal

    /**
     * Executes the backtracking algorithm to find the minimum path to the Keymaker.
     *
     * @param x     The current x-coordinate of Neo.
     * @param y     The current y-coordinate of Neo.
     * @param steps The number of steps taken so far.
     * @return True if the Keymaker is reachable; false otherwise.
     */
    public static boolean backtrackingAlgorithm(int x, int y, int steps) {
        // Check if Neo has reached the Keymaker
        if (field[x][y] == KEYMAKER) {
            MIN_PATH = Math.min(MIN_PATH, steps);
            goalX = x;
            goalY = y;
            // Check if the steps match the Manhattan distance to the goal
            if (steps == (Math.abs(0 - goalX) + Math.abs(0 - goalY))) {
                System.out.println("e " + steps);
                System.exit(0);
            }
            return true;
        }

        // Prune the search if the estimated cost exceeds the minimum path
        if (MIN_PATH != 1000000) {
            if ((steps + (Math.abs(x - goalX) + Math.abs(y - goalY))) > MIN_PATH) {
                return false;
            }
        }

        visited[x][y] = true; // Mark the current cell as visited
        step(x, y); // Explore the current position

        boolean flag = false; // To track if the goal was found

        // Explore all possible moves
        for (int[] move : moves) {
            int newX = x + move[0];
            int newY = y + move[1];

            // Check if the new position is valid and safe
            if (isValidAndSafe(newX, newY)) {
                if (backtrackingAlgorithm(newX, newY, steps + 1)) {
                    flag = true; // The goal was found through this path
                }
                step(x, y); // Unmark the step for backtracking
            }
        }
        return flag; // Return whether the goal was found
    }

    /**
     * Interacts with the Codeforces interactor to get information about the current cell.
     *
     * @param x The x-coordinate of the current position.
     * @param y The y-coordinate of the current position.
     */
    public static void step(int x, int y) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("m " + x + " " + y); // Move command
        int numberOfItems = Integer.parseInt(scanner.nextLine()); // Number of items in the cell

        // Read information about the items in the cell
        for (int i = 0; i < numberOfItems; i++) {
            String[] arr = scanner.nextLine().split(" ");
            int a = Integer.parseInt(arr[0]);
            int b = Integer.parseInt(arr[1]);
            char item = arr[2].charAt(0);

            field[a][b] = item; // Place the item in the field
        }
    }

    /**
     * Checks if the next move is valid and safe for Neo.
     *
     * @param x The x-coordinate of the potential next position.
     * @param y The y-coordinate of the potential next position.
     * @return True if the move is valid and safe; false otherwise.
     */
    public static boolean isValidAndSafe(int x, int y) {
        return (x >= 0 && x < SIZE) && (y >= 0 && y < SIZE) &&
                (field[x][y] != SENTINEL && field[x][y] != AGENT_SMITH &&
                        field[x][y] != PERCEPTIONZONE && !visited[x][y]);
    }

    /**
     * The main method initializes the grid and starts the backtracking algorithm.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int Var = Integer.parseInt(sc.nextLine()); // Perception variant
        String[] arr = sc.nextLine().split(" ");
        goalX = Integer.parseInt(arr[0]); // Set the goal X-coordinate
        goalY = Integer.parseInt(arr[1]); // Set the goal Y-coordinate

        // Initialize the grid and visited status
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                visited[i][j] = false;
                field[i][j] = EMPTY;
            }
        }

        field[0][0] = NEO; // Set Neo's initial position

        // Start the backtracking algorithm from Neo's starting position
        if (backtrackingAlgorithm(0, 0, 0)) {
            System.out.println("e " + MIN_PATH); // Print the minimum path if found
        } else {
            System.out.println("e -1"); // Print -1 if the goal is unreachable
        }
    }
}