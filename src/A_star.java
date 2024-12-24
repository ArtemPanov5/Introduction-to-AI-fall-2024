import java.util.*;

public class A_star {

    // Constants defining various elements of the game
    private static final int SIZE = 9; // Size of the game board
    private static final char EMPTY = '.'; // Empty space character
    private static final char AGENT_SMITH = 'A'; // Agent Smith character
    private static final char BACKDOOR_KEY = 'B'; // Backdoor key character
    private static final char SENTINEL = 'S'; // Sentinel character
    private static final char KEYMAKER = 'K'; // Keymaker character
    private static final char NEO = 'N'; // Neo character
    private static final char PERCEPTIONZONE = 'P'; // Perception zone character

    // Inner class for generating the game map
    class mapGenerator {
        public Graph generating_Map() {
            Random random = new Random();
            Graph map = new Graph(SIZE);

            // Initialize the board with empty spaces
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    map.getListOfVertices(i, j).setContent(EMPTY);
                }
            }

            // Place Neo at the starting position (0, 0)
            map.getListOfVertices(0, 0).setContent(NEO);

            // Randomly place Agent Smiths
            for (int i = 0; i < random.nextInt(3); i++) {
                randomLoc(map, AGENT_SMITH, random);
            }

            // Randomly place Sentinels
            for (int i = 0; i < random.nextInt(2); i++) {
                randomLoc(map, SENTINEL, random);
            }

            // Randomly place Keymaker
            randomLoc(map, KEYMAKER, random);

            // Randomly place Backdoor Key
            randomLoc(map, BACKDOOR_KEY, random);

            return map;
        }

        private void randomLoc(Graph map, char content, Random random) {
            int x, y;
            do {
                x = random.nextInt(SIZE);
                y = random.nextInt(SIZE);
            } while (map.getListOfVertices(x, y).getContent() != EMPTY);
            map.getListOfVertices(x, y).setContent(content);
            if (content == AGENT_SMITH || content == SENTINEL) {
                addPerceptionZone(map, content, x, y);
            }
        }

        // Method to add perception zones around Agent Smith and Sentinel
        public static void addPerceptionZone(Graph map, char content, int x, int y) {
            int perceptionRadius = (content == AGENT_SMITH) ? 1 : 1;

            List<Node> perceptionZone = new ArrayList<>();

            if (content == AGENT_SMITH) {
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
            } else if (content == SENTINEL) {
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

            // Set perception zone nodes
            for (Node v : perceptionZone) {
                v.setContent(PERCEPTIONZONE);
                v.setCost(1000);
            }

            map.getListOfVertices(x, y).setContent(content);
        }

        // Method to print the game map
        public static void printMap(Graph map) {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    System.out.print(map.getListOfVertices(i, j).getContent() + " ");
                }
                System.out.println();
            }
        }
    }

    class Graph {
        int size;
        Node[][] ListOfVertices;

        public Graph(int size) {
            this.size = size;
            this.ListOfVertices = new Node[size][size];

            // Initialize all nodes with empty spaces
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

    class Node {
        char content;
        int[] location;
        int cost;
        double g_x;
        double heuristic;
        int[][] edges;
        int[] parent;

        public Node(char content, int[] location, int cost, double g_x, double heuristic, int[][] edges) {
            this.content = content;
            this.location = location;
            this.cost = cost;
            this.g_x = g_x;
            this.heuristic = heuristic;
            this.edges = edges;
        }

        public void setParent(int[] parent) {
            this.parent = parent;
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

    // A* algorithm implementation
    public static void A_star_algorithm(int variant, Graph map, int xGoal, int yGoal) {
        int perceptionRange = (variant == 1) ? 1 : 2; // Perception range for Neo
        PriorityQueue<Node> PQ = new PriorityQueue<>(Comparator.comparingDouble(node -> node.heuristic)); // + node.g_x)
        HashSet<Node> visited = new HashSet<>();
        Node Goal = map.getListOfVertices(xGoal, yGoal);
        Node Start = map.getListOfVertices(0, 0);

        Start.g_x = 0;
        Start.heuristic = heuristic_cost_estimate(Start, Goal);
        PQ.add(Start);

        int result = -1;

        Node NeoLoc = map.getListOfVertices(0, 0);

        while (!PQ.isEmpty()) {
            Node to = PQ.peek();
            Node from = findFrom(map, to, visited, Goal);

            Node current = PQ.poll();

            if (from != null) {
                if (from.location[0] != NeoLoc.location[0] && from.location[1] != NeoLoc.location[1]) {
                    while (NeoLoc.location[0] != 0 && NeoLoc.location[1] != 0) {
                        System.out.println("m " + NeoLoc.location[0] + " " + NeoLoc.location[1]);
                        Scanner sc = new Scanner(System.in);
                        int dangers = sc.nextInt();

                        for (int i = 0; i < dangers; i++) {
                            int dangerX = sc.nextInt();
                            int dangerY = sc.nextInt();
                            char dangerType = sc.next().charAt(0);
                        }

                        NeoLoc.location[0] = NeoLoc.parent[0];
                        NeoLoc.location[1] = NeoLoc.parent[1];
                    }

                    current = NeoLoc;
                }
            }

            if (current == Goal) {
                result = (int) current.g_x; // Print the length of the shortest path
                break;
            }

            visited.add(current);
            System.out.println("m " + current.location[0] + " " + current.location[1]); // Movement command

            Scanner sc = new Scanner(System.in);
            int dangers = sc.nextInt(); // Read number of dangers

            for (int i = 0; i < dangers; i++) {
                int dangerX = sc.nextInt();
                int dangerY = sc.nextInt();
                char dangerType = sc.next().charAt(0);

                if (dangerType == SENTINEL || dangerType == AGENT_SMITH) {
                    Node dangerNode = map.getListOfVertices(dangerX, dangerY);
                    dangerNode.setContent(dangerType);
                    dangerNode.setCost(1000); // Mark as dangerous cell
                } else {
                    Node item = map.getListOfVertices(dangerX, dangerY);
                    item.setContent(dangerType);
                }
            }

            // Explore neighbors
            for (int[] move : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
                int nx = current.location[0] + move[0];
                int ny = current.location[1] + move[1];

                if (nx >= 0 && ny >= 0 && nx < SIZE && ny < SIZE) {
                    Node neighbor = map.getListOfVertices(nx, ny);

                    if (!visited.contains(neighbor) && neighbor.content != PERCEPTIONZONE) {
                        double tentative_gScore = current.g_x + neighbor.cost;

                        if (tentative_gScore < neighbor.g_x || !PQ.contains(neighbor)) {
                            neighbor.g_x = tentative_gScore;
                            neighbor.setParent(current.location); // Add parent
                            neighbor.heuristic = heuristic_cost_estimate(neighbor, Goal);
                            PQ.add(neighbor);
                        }
                    }
                }
            }

            NeoLoc = current;
        }
        System.out.println("e " + result);
    }

    // Helper method to find the previous node in the path
    public static Node findFrom(Graph map, Node to, HashSet<Node> visited, Node goal) {
        ArrayList<Node> list = new ArrayList<>();
        for (int[] move : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
            int nx = to.location[0] + move[0];
            int ny = to.location[1] + move[1];

            if (nx >= 0 && ny >= 0 && nx < SIZE && ny < SIZE) {
                Node neighbor = map.getListOfVertices(nx, ny);

                if (visited.contains(neighbor) && neighbor.content != PERCEPTIONZONE) {
                    list.add(neighbor);
                }
            }
        }
        if (list.isEmpty()) {
            return null;
        }
        Collections.sort(list, Comparator.comparingDouble(n -> heuristic_cost_estimate(n, goal)));
        return list.get(0);
    }

    // Heuristic function to estimate the distance between two points
    public static double heuristic_cost_estimate(Node currentPosition, Node goal) {
        return Math.sqrt(Math.pow((currentPosition.getLocation()[0] - goal.getLocation()[0]), 2) +
                Math.pow((currentPosition.getLocation()[1] - goal.getLocation()[1]), 2));
    }

    // Main method where the program execution begins
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in); // Create a new Scanner object to read input from the user

        int variant = sc.nextInt();   // Read the game variant (1 or 2)
        int xGoal = sc.nextInt();     // Read X coordinate of Keymaker
        int yGoal = sc.nextInt();     // Read Y coordinate of Keymaker

        // Create an instance of the A_star class
        A_star astar = new A_star();

        // Generate a new map generator
        mapGenerator gen = astar.new mapGenerator();

        // Generate the game map
        Graph map = gen.generating_Map();

        // Initialize all nodes with empty spaces and cost 1
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                map.getListOfVertices(i, j).setContent(EMPTY);
                map.getListOfVertices(i, j).setCost(1);
            }
        }

        // Set Neo as the starting position
        map.getListOfVertices(0, 0).setContent(NEO);

        // Call the A* algorithm function with the generated map
        A_star_algorithm(variant, map, xGoal, yGoal);
    }
}
