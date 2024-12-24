import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class BT_Tests {

    private static final int SIZE = 9;
    private static final char EMPTY = '.';
    private static final char AGENT_SMITH = 'A';
    private static final char BACKDOOR_KEY = 'B';
    private static final char SENTINEL = 'S';
    private static final char KEYMAKER = 'K';
    private static final char NEO = 'N';
    private static final char PERCEPTIONZONE = 'P';

    static class MapGenerator {
        public static char[][] generatingMap(){
            Random random = new Random();
            char[][] map = new char[SIZE][SIZE];

            for(int i = 0; i < SIZE; i++){
                for(int j = 0; j < SIZE; j++){
                    map[i][j] = EMPTY;
                }
            }

            map[0][0] = NEO;
            for (int i = 0; i < random.nextInt(3); i++){
                randomLoc(map, AGENT_SMITH, random);
            }
            for (int i = 0; i < random.nextInt(2); i++){
                randomLoc(map, SENTINEL, random);
            }
            randomLoc(map, KEYMAKER, random);
            randomLoc(map, BACKDOOR_KEY, random);

            return map;
        }

        private static void randomLoc(char[][] map, char item, Random random) {
            int x, y;
            do {
                x = random.nextInt(SIZE);
                y = random.nextInt(SIZE);
            } while (map[x][y] != EMPTY || (x == 0 && y == 0));
            map[x][y] = item;

            if (item == AGENT_SMITH || item == SENTINEL) {
                addPerceptionZone(map, item, x, y);
            }
        }

        public static void addPerceptionZone(char[][] map, char content, int x, int y) {
            if (content == AGENT_SMITH){
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        int newX = x + dx;
                        int newY = y + dy;
                        if (newX >= 0 && newX < SIZE && newY >= 0 && newY < SIZE) {
                            if (map[newX][newY] == EMPTY ) {
                                map[newX][newY] = PERCEPTIONZONE;
                            }
                        }
                    }
                }
            }
            else{
                for (int i = -1; i <= 1; i += 2) {
                    int newX = x + i;
                    int newY = y;
                    if (newX >= 0 && newX < SIZE && newY >= 0 && newY < SIZE) {
                        if (map[newX][newY] == EMPTY) {
                            map[newX][newY] = PERCEPTIONZONE;
                        }
                    }
                    newX = x;
                    newY = y + i;
                    if (newX >= 0 && newX < SIZE && newY >= 0 && newY < SIZE) {
                        if (map[newX][newY] == EMPTY) {
                            map[newX][newY] = PERCEPTIONZONE;
                        }
                    }
                }
            }
            map[x][y] = content;
        }

        public static void printMap(char[][] map){
            for(int i = 0; i < SIZE; i++){
                for(int j = 0; j < SIZE; j++){
                    System.out.print(map[i][j] + " ");
                }
                System.out.println();
            }
        }
    }

    static int[][] moves = new int[][]{{1, 0}, {0, -1}, {0, 1}, {0, 1}};
    static boolean[][] visited = new boolean[SIZE][SIZE];
    static char[][] field = new char[SIZE][SIZE];
    static int MIN_PATH = 1000000;
    static int goalX;
    static int goalY;
    static char[][] currentMap;

    public static boolean backtrackingAlgorithm(int x, int y, int steps) {
        if (field[x][y] == KEYMAKER) {
            MIN_PATH = Math.min(MIN_PATH, steps);
            goalX = x;
            goalY = y;
//            if (steps == (Math.abs(0 - goalX) + Math.abs(0 - goalY))){
//                System.out.println("e " + steps);
//                System.out.println(System. currentTimeMillis() / 1000);
//                System.exit(0);
//            }
            return true;
        }

        if (MIN_PATH != 1000000) {
            if ((steps + (Math.abs(x - goalX) + Math.abs(y - goalY))) > MIN_PATH) {
                return false;
            }
        }

        visited[x][y] = true;
        step(x, y);

        boolean flag = false;

        for (int[] move : moves) {
            int newX = x + move[0];
            int newY = y + move[1];

            if(isValidAndSafe(newX, newY)) {
                if (backtrackingAlgorithm(newX, newY, steps+1)){
                    flag = true;
                }
                step(x, y);
            }
        }
        return flag;
    }

    // Simulated step method that uses generated map data instead of Scanner
    public static void step(int x, int y) {
        System.out.println("m " + x + " " + y);
        int numberOfItems = 0;

        // Count items in perception zone
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int newX = x + dx;
                int newY = y + dy;
                if (newX >= 0 && newX < SIZE && newY >= 0 && newY < SIZE && currentMap[newX][newY] != EMPTY) {
                    field[newX][newY] = currentMap[newX][newY];
                    numberOfItems++;
                }
            }
        }

//        System.out.println(numberOfItems);
        // Print items in perception zone
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int newX = x + dx;
                int newY = y + dy;
                if (newX >= 0 && newX < SIZE && newY >= 0 && newY < SIZE && currentMap[newX][newY] != EMPTY) {
                    System.out.println(newX + " " + newY + " " + currentMap[newX][newY]);
                }
            }
        }
    }

    public static boolean isValidAndSafe(int x, int y) {
        if ((x < 0 || x >= SIZE) || (y < 0 || y >= SIZE) || field[x][y] == SENTINEL || field[x][y] == AGENT_SMITH
                || field[x][y] == PERCEPTIONZONE || visited[x][y]) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {

        long numberOfW = 0;
        long numberOfL = 0;

        ArrayList<Long> times = new ArrayList<>();

        for (int test = 0; test < 1000; test++) {
            long m = System.currentTimeMillis();
            currentMap = MapGenerator.generatingMap();
            MapGenerator.printMap(currentMap);
            // Initialize field
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    visited[i][j] = false;
                    field[i][j] = EMPTY;
                }
            }
            field[0][0] = NEO;
            MIN_PATH = 11000000;

            if (backtrackingAlgorithm(0, 0, 0)){
                System.out.println("e " + MIN_PATH);
                System.out.println((System.currentTimeMillis() - m)/1000);
                times.add((System.currentTimeMillis() - m)/1000);
                numberOfW++;
            }
            else {
                System.out.println("e -1");
                System.out.println( (System.currentTimeMillis() - m)/1000);
                numberOfL++;
            }
        }

        System.out.println("WinRate: " + numberOfW/1000);
        System.out.println("LoseRate: " + numberOfL/1000);

        double sumOfTimes = 0;
        for (double time : times) {
            sumOfTimes += time;
        }
        double median = sumOfTimes / 1000;

        double sumOfSqrt = 0;
        for (int i = 0; i < times.size(); i++){
            sumOfSqrt += Math.pow(times.get(i) - median, 2);
        }
        double StdDeviation = Math.sqrt(sumOfSqrt/(times.size()-1));

        HashSet<Double> timesSet = new HashSet<>();

        int max = 0;
        double mode = 0;

        for (int i = 0; i < times.size(); i++){
            double a = times.get(i);

            if (timesSet.contains(a)){
                continue;
            }
            int b = 0;
            for (int j = 0; j < times.size(); j++) {
                if (times.get(j) == a){
                    b++;
                }
            }

            if (b > max){
                max = b;
                mode = a;
            }
        }

        System.out.println("Median: " + median);
        System.out.println("StdDeviation: " + StdDeviation);
        System.out.println("Mode: " + mode);
    }
}
