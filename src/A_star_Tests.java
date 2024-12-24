import java.util.*;

public class A_star_Tests{

    // Генерация стартового игрового поля
    class mapGenerator{
        private static final int SIZE = 9;
        private static final char EMPTY = '.';
        private static final char AGENT_SMITH = 'A';
        private static final char BACKDOOR_KEY = 'B';
        private static final char SENTINEL = 'S';
        private static final char KEYMAKER = 'K';
        private static final char NEO = 'N';
        private static final char PERCEPTIONZONE = 'P';

        public Graph generating_Map(){
            Random random = new Random();
            Graph map = new Graph(SIZE);

            for(int i = 0; i < SIZE; i++){
                for(int j = 0; j < SIZE; j++){
                    map.getListOfVertices(i,j).setContent(EMPTY);
                }
            }

            map.getListOfVertices(0,0).setContent(NEO);
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

        private static void randomLoc(Graph map, char content, Random random){
            int x, y;
            do {
                x = random.nextInt(SIZE);
                y = random.nextInt(SIZE);
            } while (map.getListOfVertices(x, y).getContent() != EMPTY);
            map.getListOfVertices(x, y).setContent(content);
            if (content == AGENT_SMITH || content == SENTINEL){
                add_Parception_Zone(map, content, x, y);
            }
        }

        public static void add_Parception_Zone(Graph map, char content, int x, int y){
            int perceptionRadius = 1;

            List<Node> perceptionZone = new ArrayList<>();

            if (content == AGENT_SMITH){
                perceptionZone.add(map.getListOfVertices(x, y));
                for (int dx = -perceptionRadius; dx <= perceptionRadius; dx++) {
                    for (int dy = -perceptionRadius; dy <= perceptionRadius; dy++) {
                        int newX = x + dx;
                        int newY = y + dy;
                        if (newX >= 0 && newX < SIZE && newY >= 0 && newY < SIZE) {
                            Node vertex = map.getListOfVertices(newX, newY);
                            if (vertex != null && !perceptionZone.contains(vertex) && vertex.content != NEO) {
                                perceptionZone.add(vertex);
                            }
                        }
                    }
                }
            } else if (content == SENTINEL){
                perceptionZone.add(map.getListOfVertices(x, y));

                for (int i = -1; i <= 1; i += 2) {
                    int newX = x + i;
                    int newY = y;
                    if (newX >= 0 && newX < SIZE && newY >= 0 && newY < SIZE) {
                        Node vertex = map.getListOfVertices(newX, newY);
                        if (vertex != null && vertex.content != NEO) {
                            perceptionZone.add(vertex);
                        }
                    }
                    newX = x;
                    newY = y + i;
                    if (newX >= 0 && newX < SIZE && newY >= 0 && newY < SIZE) {
                        Node vertex = map.getListOfVertices(newX, newY);
                        if (vertex != null && vertex.content != NEO) {
                            perceptionZone.add(vertex);
                        }
                    }
                }
            }

            for (Node v : perceptionZone) {
                v.setContent(PERCEPTIONZONE);
                v.setCost(1000);
            }

            map.getListOfVertices(x, y).setContent(content);
        }

        public static void printMap(Graph map){
            for(int i = 0; i < SIZE; i++){
                for(int j = 0; j < SIZE; j++){
                    System.out.print(map.getListOfVertices(i,j).getContent() + " ");
                }
                System.out.println();
            }
        }
    }

    class Graph{
        int size;
        Node[][] ListOfVertices;

        public Graph(int size) {
            this.size = size;
            this.ListOfVertices = new Node[size][size];

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    this.ListOfVertices[i][j] = new Node('.', new int[]{i, j}, 1, 0, 0, null);
                }
            }
        }

        public Node getListOfVertices(int x, int y) {
            return ListOfVertices[x][y];
        }
    }

    class Node{
        char content;
        int[] location;
        int cost;
        double g_x;
        double heuristic;
        int[][] edges;

        public Node(char content, int[] location, int cost, double g_x, double heuristic, int[][] edges) {
            this.content = content;
            this.location = location;
            this.cost = cost;
            this.g_x = g_x;
            this.heuristic = heuristic;
            this.edges = edges;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        public void setContent(char content) {
            this.content = content;
        }

        public char getContent() {
            return content;
        }

        public int[] getLocation() {
            return location;
        }
    }

    public static void A_star_algorithm(int Var_Of_scenario, Graph map, int x_Of_Goal, int y_Of_Goal){

        PriorityQueue<Node> PQ = new PriorityQueue<>(Comparator.comparingDouble(node -> node.heuristic + node.g_x));
        HashSet<Node> visited = new HashSet<>();
        Node Goal = map.getListOfVertices(x_Of_Goal, y_Of_Goal);
        Node Start = map.getListOfVertices(0, 0);

        Start.g_x = 0;
        Start.heuristic = heuristic_cost_estimate(Start, Goal);
        PQ.add(Start);

        int result = -1;

        while (!PQ.isEmpty()){
            Node current = PQ.poll();

            if (current == Goal) {
                result = (int)current.g_x;
                mapGenerator.printMap(map); // Final state
                break;
            }

            visited.add(current);
            current.setContent('N'); // Mark as visited
            mapGenerator.printMap(map); // Show current state
            current.setContent('*');
            System.out.println(); // Separate iterations with a line for readability

            for (int[] move : new int[][]{{-1,0},{1,0},{0,-1},{0,1}}) {
                int nx = current.location[0] + move[0];
                int ny = current.location[1] + move[1];
                if (nx >= 0 && ny >= 0 && nx < 9 && ny < 9) {
                    Node neighbor = map.getListOfVertices(nx, ny);
                    if (!visited.contains(neighbor) && neighbor.content != mapGenerator.PERCEPTIONZONE) {
                        double tentative_gScore = current.g_x + neighbor.cost;
                        if (tentative_gScore < neighbor.g_x || !PQ.contains(neighbor)) {
                            neighbor.g_x = tentative_gScore;
                            neighbor.heuristic = heuristic_cost_estimate(neighbor, Goal);
                            PQ.add(neighbor);
                        }
                    }
                }
            }
        }

        System.out.println(result);
        System.out.println(System. currentTimeMillis() / 1000) ;
    }

    public static double heuristic_cost_estimate(Node currentPosition, Node goal){
        return Math.sqrt(Math.pow((currentPosition.getLocation()[0] - goal.getLocation()[0]), 2) + Math.pow((currentPosition.getLocation()[1] - goal.getLocation()[1]), 2));
    }

    public static void main(String[] args) {
        A_star_Tests astar = new A_star_Tests();
        mapGenerator gen = astar.new mapGenerator();
        Graph map = gen.generating_Map();
        int x = 0;
        int y = 0;
        for (int i = 0; i < mapGenerator.SIZE; i++) {
            for (int j = 0; j < mapGenerator.SIZE; j++) {
                if (map.getListOfVertices(i, j).getContent() == mapGenerator.KEYMAKER) {
                    x = i;
                    y = j;
                }
            }
        }
        A_star_algorithm(0, map, x, y);
//        for(int i = 0; i < 100; i++){
//            System.out.println("Map # " + (i+1));
//            mapGenerator.printMap(gen.generating_Map());
//        }
    }
}
