import java.util.*;

class MetroStation {
    String name;

    MetroStation(String name) {
        this.name = name;
    }
}

class Graph {
    Map<MetroStation, List<Pair>> adj = new HashMap<>();
    List<MetroStation> stationList = new ArrayList<>();
    Map<MetroStation, Integer> dist = new HashMap<>();
    Map<MetroStation, MetroStation> parent = new HashMap<>();

    void addStation(MetroStation station) {
        stationList.add(station);
        adj.put(station, new ArrayList<>());
        dist.put(station, Integer.MAX_VALUE);
    }

    void addRoute(MetroStation u, MetroStation v, int w, boolean bidirectional) {
        adj.get(u).add(new Pair(v, w));
        if (bidirectional) {
            adj.get(v).add(new Pair(u, w));
        }
    }

    void viewStations() {
        for (MetroStation s : stationList) {
            System.out.println(s.name);
        }
    }

    MetroStation searchStation(String name) {
        for (MetroStation s : stationList) {
            if (s.name.equalsIgnoreCase(name)) return s;
        }
        return null;
    }

    void shortestRoute(MetroStation src, MetroStation destination) {
        for (MetroStation s : stationList) {
            dist.put(s, Integer.MAX_VALUE);
            parent.put(s, null);
        }

        PriorityQueue<Pair> pq = new PriorityQueue<>(Comparator.comparingInt(p -> p.weight));
        pq.add(new Pair(src, 0));
        dist.put(src, 0);

        while (!pq.isEmpty()) {
            MetroStation u = pq.poll().station;

            for (Pair neighbor : adj.get(u)) {
                MetroStation v = neighbor.station;
                int weight = neighbor.weight;
                if (dist.get(v) > dist.get(u) + weight) {
                    dist.put(v, dist.get(u) + weight);
                    parent.put(v, u);
                    pq.add(new Pair(v, dist.get(v)));
                }
            }
        }

        if (dist.get(destination) == Integer.MAX_VALUE) {
            System.out.println("No path exists between " + src.name + " and " + destination.name);
        } else {
            System.out.println("Shortest distance: " + dist.get(destination));
            System.out.print("Path: ");
            printPath(destination);
            System.out.println();
        }
    }

    void printPath(MetroStation station) {
        if (parent.get(station) != null) {
            printPath(parent.get(station));
            System.out.print("->");
        }
        System.out.print(station.name);
    }

    void printAllRoutes(MetroStation src, MetroStation destination) {
        Map<MetroStation, Boolean> visited = new HashMap<>();
        List<String> path = new ArrayList<>();
        for (MetroStation s : stationList) visited.put(s, false);
        printAllPathsUtil(src, destination, visited, path);
    }

    private void printAllPathsUtil(MetroStation u, MetroStation d, Map<MetroStation, Boolean> visited, List<String> path) {
        visited.put(u, true);
        path.add(u.name);

        if (u.equals(d)) {
            System.out.println(String.join(" -> ", path));
        } else {
            for (Pair p : adj.get(u)) {
                if (!visited.get(p.station)) {
                    printAllPathsUtil(p.station, d, visited, path);
                }
            }
        }

        path.remove(path.size() - 1);
        visited.put(u, false);
    }

    void fare(MetroStation src, MetroStation destination) {
        shortestRoute(src, destination);
        if (dist.get(destination) != Integer.MAX_VALUE) {
            System.out.println("Total Fare: ₹" + (dist.get(destination) * 5));
        }
    }
}

class Pair {
    MetroStation station;
    int weight;

    Pair(MetroStation s, int w) {
        this.station = s;
        this.weight = w;
    }
}

public class MetroMap {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Graph g = new Graph();

        MetroStation a = new MetroStation("Kashmere Gate");
        MetroStation b = new MetroStation("Rajiv Chauk");
        MetroStation c = new MetroStation("NSP");
        MetroStation d = new MetroStation("Botanical Garden");

        g.addStation(a);
        g.addStation(b);
        g.addStation(c);
        g.addStation(d);

        g.addRoute(a, b, 15, true);
        g.addRoute(b, c, 30, true);
        g.addRoute(a, c, 200, true);
        g.addRoute(a, d, 30, true);

        char choice;
        do {
            System.out.println("\n--- METRO MAP ---");
            System.out.println("1. View Stations");
            System.out.println("2. Add Station");
            System.out.println("3. Add Route");
            System.out.println("4. View All Routes");
            System.out.println("5. Shortest Path");
            System.out.println("6. View Fare");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            int ch = sc.nextInt();
            sc.nextLine();

            switch (ch) {
                case 1:
                    g.viewStations();
                    break;
                case 2:
                    System.out.print("Enter station name: ");
                    String name = sc.nextLine();
                    MetroStation newStation = new MetroStation(name);
                    g.addStation(newStation);
                    break;
                case 3: {
                    MetroStation[] sd = getSourceDestination(g, sc);
                    System.out.print("Enter distance: ");
                    int dist = sc.nextInt();
                    g.addRoute(sd[0], sd[1], dist, true);
                    System.out.println("Route added.");
                    break;
                }
                case 4: {
                    MetroStation[] sd = getSourceDestination(g, sc);
                    g.printAllRoutes(sd[0], sd[1]);
                    break;
                }
                case 5: {
                    MetroStation[] sd = getSourceDestination(g, sc);
                    g.shortestRoute(sd[0], sd[1]);
                    break;
                }
                case 6: {
                    MetroStation[] sd = getSourceDestination(g, sc);
                    g.fare(sd[0], sd[1]);
                    break;
                }
                case 7:
                    System.exit(0);
            }
            System.out.print("\nDo you want to return to main menu? (Y/N): ");
            choice = sc.next().charAt(0);
            sc.nextLine();
        } while (choice == 'Y' || choice == 'y');
        sc.close();
    }
    
    static MetroStation[] getSourceDestination(Graph g, Scanner sc) {
        int i = 1;
        for (MetroStation s : g.stationList) {
            System.out.println(i + ". " + s.name);
            i++;
        }

        System.out.print("Choose source (1 to " + g.stationList.size() + "): ");
        int sourceIndex = sc.nextInt();
        System.out.print("Choose destination (1 to " + g.stationList.size() + "): ");
        int destinationIndex = sc.nextInt();

        return new MetroStation[]{g.stationList.get(sourceIndex - 1), g.stationList.get(destinationIndex - 1)};
    }
}
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.