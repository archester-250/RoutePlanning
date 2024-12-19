import java.util.*;
import java.util.stream.Collectors;

public class RoutePlanning {

    static class Path {
        int distance;
        int cost;
        int time;

        public Path(int distance, int cost, int time) {
            this.distance = distance;
            this.cost = cost;
            this.time = time;
        }
    }

    static class Customer {
        List<Path> paths;

        public Customer(List<Path> paths) {
            this.paths = paths;
        }
    }

    /**
     * 动态规划算法
     */
    public static double dynamicProgramming(List<Customer> customers, int maxTime) {
        int n = customers.size();
        double[][] dp = new double[n + 1][maxTime + 1];

        for (int i = 0; i <= n; i++) {
            Arrays.fill(dp[i], Double.MAX_VALUE);
        }
        dp[0][0] = 0; // 初始状态

        for (int i = 1; i <= n; i++) {
            for (int t = 0; t <= maxTime; t++) {
                for (Path path : customers.get(i - 1).paths) {
                    if (t >= path.time) {
                        dp[i][t] = Math.min(dp[i][t], dp[i - 1][t - path.time] +
                                0.5 * (path.distance + path.cost) / n);
                    }
                }
            }
        }

        // 找到满足时间约束的最优解
        double result = Double.MAX_VALUE;
        for (int t = 0; t <= maxTime; t++) {
            result = Math.min(result, dp[n][t]);
        }
        return result;
    }

    /**
     * 遗传算法
     */
    public static double geneticAlgorithm(List<Customer> customers, int maxTime, int generations, int populationSize) {
        int n = customers.size();
        Random random = new Random();

        // 初始化种群
        List<int[]> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            int[] individual = new int[n];
            for (int j = 0; j < n; j++) {
                individual[j] = random.nextInt(customers.get(j).paths.size());
            }
            population.add(individual);
        }

        // 遗传算法主循环
        for (int gen = 0; gen < generations; gen++) {
            // 计算适应度并排序
            population.sort(Comparator.comparingDouble(individual -> fitness(individual, customers, maxTime)));



            // 选择优秀个体
            List<int[]> nextGeneration = new ArrayList<>();
            for (int i = 0; i < populationSize / 2; i++) {
                nextGeneration.add(population.get(i));
            }

            // 交叉操作
            while (nextGeneration.size() < populationSize) {
                int[] parent1 = nextGeneration.get(random.nextInt(nextGeneration.size()));
                int[] parent2 = nextGeneration.get(random.nextInt(nextGeneration.size()));
                int[] child = new int[n];
                for (int i = 0; i < n; i++) {
                    child[i] = random.nextBoolean() ? parent1[i] : parent2[i];
                }
                nextGeneration.add(child);
            }

            // 变异操作
            for (int[] individual : nextGeneration) {
                if (random.nextDouble() < 0.1) { // 10% 概率变异
                    int idx = random.nextInt(n);
                    individual[idx] = random.nextInt(customers.get(idx).paths.size());
                }
            }

            population = nextGeneration;
        }

        // 返回最优个体的适应度
        return fitness(population.get(0), customers, maxTime);
    }

    /**
     * 计算适应度
     */
    public static double fitness(int[] individual, List<Customer> customers, int maxTime) {
        int n = customers.size();
        int totalTime = 0;
        double totalDistance = 0, totalCost = 0;

        for (int i = 0; i < n; i++) {
            Path path = customers.get(i).paths.get(individual[i]);
            totalTime += path.time;
            totalDistance += path.distance;
            totalCost += path.cost;
        }

        // 如果超过时间限制，返回高罚值，但不能返回 Double.MAX_VALUE
        if (totalTime > maxTime) return 1e9; // 返回一个大罚值，而不是 Double.MAX_VALUE

        // 计算目标函数
        return 0.5 * (totalDistance / n + totalCost / n);
    }



    public static void main(String[] args) {
        // 输入示例数据
        int n = 50; // 客户数量
        int m = 10; // 每个客户有3条路径
        int maxTime = 1500; // 时间限制

        List<Customer> customers = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            List<Path> paths = new ArrayList<>();
            for (int j = 0; j < m; j++) {
                paths.add(new Path(random.nextInt(50) + 1, random.nextInt(20) + 1, random.nextInt(30) + 1));
            }
            customers.add(new Customer(paths));
        }

        // 测试动态规划算法
        long startDP = System.currentTimeMillis();
        double resultDP = dynamicProgramming(customers, maxTime);
        long endDP = System.currentTimeMillis();
        System.out.println("Dynamic Programming Result: " + resultDP);
        System.out.println("Dynamic Programming Time: " + (endDP - startDP) + "ms");

        // 测试遗传算法
        long startGA = System.currentTimeMillis();
        double resultGA = geneticAlgorithm(customers, maxTime, 50, 100);
        long endGA = System.currentTimeMillis();
        System.out.println("Genetic Algorithm Result: " + resultGA);
        System.out.println("Genetic Algorithm Time: " + (endGA - startGA) + "ms");
    }
}
