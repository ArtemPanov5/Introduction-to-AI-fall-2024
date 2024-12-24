import java.util.Random;

public class MapGenerator {
    private static final int SIZE = 9;
    private static final char EMPTY = '.';
    private static final char AGENT_SMITH = 'A';
    private static final char BACKDOOR_KEY = 'B';
    private static final char SENTINEL = 'S';
    private static final char KEYMAKER = 'K';
    private static final char NEO = 'N';

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

    private static void randomLoc(char map[][], char item, Random random){
        int x, y;
        do {
            x = random.nextInt(SIZE);
            y = random.nextInt(SIZE);
        } while (map[x][y] != EMPTY);
        map[x][y] = item;
    }

    public static void printMap(char[][] map){
        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        for(int i = 0; i < 1000; i++){
            System.out.println("Map # " + (i+1));
            printMap(generatingMap());
        }
    }
}
