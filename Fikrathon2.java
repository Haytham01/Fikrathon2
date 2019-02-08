package Problems;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

class Problem {
    static int R, C, F, N, B, T;
    static BufferedReader br;
    static BufferedWriter bw;

    static {
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream("harthiya.csv"), "UTF-8"));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("outputH.csv"), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        readInput();
        //int[][] city = new int[R][C];
        ArrayList<Order> orders = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            orders.add(Order.readOrder(i));
        }

        // order based on startTime
        Collections.sort(orders);

        Motor[] motors = new Motor[F];
        for (int j = 0; j < motors.length; j++) {
            if (orders.size() == 0) break;

            motors[j] = new Motor(orders.remove(0));
            // find next order with valid startTime and deadline
            while(true) {
                int nearestValidOrderIndex = motors[j].findNearestValidOrder(orders);
                if (nearestValidOrderIndex > -1) {
                    motors[j].assignOrder(orders.remove(nearestValidOrderIndex));
                }else break;
            }
        }


        for (int j = 0; j < motors.length; j++) {
            // find next order with valid deadline only
            while(true) {
                int nearestOrderIndex = motors[j].findNearestOrder(orders);
                if (nearestOrderIndex > -1) {
                    motors[j].assignOrder(orders.remove(nearestOrderIndex));

                    // find next order with valid startTime and deadline
                    while(true) {
                        int nearestValidOrderIndex = motors[j].findNearestValidOrder(orders);
                        if (nearestValidOrderIndex > -1) {
                            motors[j].assignOrder(orders.remove(nearestValidOrderIndex));
                        }else break;
                    }

                }else break;
            }
        }


        for (int i = 0; i < motors.length; i++) {
            if (motors[i] == null) bw.write(0 + "\n");
            else {
                motors[i].outputOrders();
            }
        }

        bw.flush();
        bw.close();
        br.close();
    }

    public static void readInput() throws IOException {
        String[] input = br.readLine().split(",");

        R = Integer.parseInt(input[0]);
        C = Integer.parseInt(input[1]);
        F = Integer.parseInt(input[2]);
        N = Integer.parseInt(input[3]);
        B = Integer.parseInt(input[4]);
        T = Integer.parseInt(input[5]);
    }
}

class Motor {
    int time = 0;
    Point currentLocation = new Point(0, 0);
    ArrayList<Order> orders = new ArrayList<>();

    Motor(Order order) {
        assignOrder(order);
    }

    void assignOrder(Order order) {
        updateTime(order);
        orders.add(order);
    }


    private void updateTime(Order order) {
        time = time + order.startTime;
        currentLocation = order.restaurantLocation;
        time = time + currentLocation.distance(order.clientLocation);
        currentLocation = order.clientLocation;
    }

    boolean isOrderValid(Order order) {
        int toRestaurantTime = currentLocation.distance(order.restaurantLocation);

        if (time + toRestaurantTime <= order.startTime) {
            return true;
        } else {
            return false;
        }
    }

    boolean isDeadlineValid(Order order) {
        int timeToClient = currentLocation.distance(order.restaurantLocation) + order.restaurantLocation.distance(order.clientLocation) + time;

        if (timeToClient <= order.deadLine) {
            return true;
        } else {
            return false;
        }
    }

    void outputOrders() throws IOException {
        Problem.bw.write(String.valueOf(orders.size() + ","));

        for (int i = 0; i < orders.size() - 1; i++) {
            Problem.bw.write(orders.get(i).orderNumber + ",");
        }

        Problem.bw.write(orders.get(orders.size() - 1).orderNumber + "\n");
    }

    public int findNearestValidOrder(ArrayList<Order> orders) {
        if(orders.size() == 0) return -1;

        int nearestDistance = currentLocation.distance(orders.get(0).restaurantLocation);
        int nearestOrderIndex = 0;
        for (int i = 1; i < orders.size(); i++) {
            int distance = currentLocation.distance(orders.get(i).restaurantLocation);
            if (distance < nearestDistance && isOrderValid(orders.get(i))) {
                nearestDistance = distance;
                nearestOrderIndex = i;
            }
        }

        if (isOrderValid(orders.get(nearestOrderIndex))) return nearestOrderIndex;
        else return -1;
    }

    public int findNearestOrder(ArrayList<Order> orders) {
        if(orders.size() == 0) return -1;

        int nearestDistance = currentLocation.distance(orders.get(0).restaurantLocation);
        int nearestOrderIndex = 0;
        for (int i = 1; i < orders.size(); i++) {
            int distance = currentLocation.distance(orders.get(i).restaurantLocation);
            if (distance < nearestDistance && isDeadlineValid(orders.get(i))) {
                nearestDistance = distance;
                nearestOrderIndex = i;
            }
        }

        if (isOrderValid(orders.get(nearestOrderIndex))) return nearestOrderIndex;
        else return -1;
    }
}

class Order implements Comparable {
    Point restaurantLocation;
    Point clientLocation;
    int startTime;
    int deadLine;
    int orderNumber;

    Order(Point restaurantLocation, Point clientLocation, int startTime, int deadLine, int orderNumber) {
        this.restaurantLocation = restaurantLocation;
        this.clientLocation = clientLocation;
        this.startTime = startTime;
        this.deadLine = deadLine;
        this.orderNumber = orderNumber;
    }

    static Order readOrder(int orderNumber) throws IOException {
        String[] input = Problem.br.readLine().split(",");
        int a = Integer.parseInt(input[0]);
        int b = Integer.parseInt(input[1]);
        int x = Integer.parseInt(input[2]);
        int y = Integer.parseInt(input[3]);
        int s = Integer.parseInt(input[4]);
        int f = Integer.parseInt(input[5]);
        return new Order(new Point(a, b),
                new Point(x, y),
                s, f, orderNumber);
    }


    @Override
    public int compareTo(Object o) {
        Order order = (Order) o;
        if (startTime < order.startTime) {
            return -1;
        } else if (startTime > order.startTime) {
            return 1;
        } else {
            return deadLine < order.deadLine ? 1 : -1;
        }
    }
}

class Point {
    int x, y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    int distance(Point p) {
        return Math.abs(x - p.x) + Math.abs(y - p.y);
    }
}