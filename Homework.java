package week5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

class Distance implements Comparable{
    public int id;
    public double dist;
    Distance(int i, double d){
        id = i;
        dist = d;
    }
    public int compareTo(Object o){
        return (int)(dist - ((Distance)o).dist);
    }
}

public class Homework{

    static ArrayList<Double>  inputX = new ArrayList<Double>();
    static ArrayList<Double>  inputY = new ArrayList<Double>();
    static ArrayList<Integer> outputZ = new ArrayList<Integer>();
    static ArrayList<Distance>  a_distance = new ArrayList<Distance>();
    static ArrayList<Distance>  m_distance = new ArrayList<Distance>();
    static ArrayList<Boolean> visited = new ArrayList<Boolean>();


    static void import_XY_fromInputFile(String input_file_name){
        try {
            File f = new File(input_file_name);
            BufferedReader br = new BufferedReader(new FileReader(f));

            String line;

            // タイトル行(x,y)を読み飛ばす
            br.readLine();

            while ((line = br.readLine()) != null) {
              String[] data = line.split(",", 0);
              double x = Double.parseDouble(data[0]);
              double y = Double.parseDouble(data[1]);
              inputX.add(x);
              inputY.add(y);
              visited.add(false);
            }
            br.close();



        } catch (IOException e) {
            System.out.println(e);
        }

    }

    static void measure_distance(int midpoint){
        m_distance.clear();
        double midpoint_x = inputX.get(midpoint);
        double midpoint_y = inputY.get(midpoint);


        for(int i = 0; i< inputX.size() ; i++){
            if(visited.get(i)==false){
                double d = 0;
                double point_x = inputX.get(i);
                double point_y = inputY.get(i);

                double distance_x = point_x - midpoint_x;
                double distance_y = point_y - midpoint_y;

                d = Math.pow(distance_x, 2) + Math.pow(distance_y, 2);

                Distance dist_unvisited = new Distance(i, d);
                m_distance.add(dist_unvisited);

            }
            else{
                Distance dist_visited = new Distance(i, Double.MAX_VALUE);
                m_distance.add(dist_visited);
           }
        }



    }


    static void measure_distance_all(int start_point){
        double start_point_x = inputX.get(start_point);
        double start_point_y = inputY.get(start_point);

        for(int i = 0; i < inputX.size() ; i++){
            double d = 0;
            double point_x = inputX.get(i);
            double point_y = inputY.get(i);

            double distance_x = point_x - start_point_x;
            double distance_y = point_y - start_point_y;

            d = Math.pow(distance_x, 2) + Math.pow(distance_y, 2);

            Distance dist = new Distance(i, d);
            a_distance.add(dist);

        }

    }

    static int search_start_point(){
        // xの一番小さいものを探す
        double minX = Integer.MAX_VALUE;
        int minX_id = 0;

        for(int i = 0; i < inputX.size(); i++ ){
            double x_value = inputX.get(i);
            if(x_value < minX){
                minX = x_value;
                minX_id = i;
            }

        }

        return minX_id;
    }


    static int get_next_point(int pre_point){
        // プレポイントからの距離を取り出す
        measure_distance(pre_point);
        // 近い所を探す
        Collections.sort(m_distance);
        Distance nearest = m_distance.get(0);

        if(nearest.dist == Double.MAX_VALUE){
            return -1;
        }
        return nearest.id;
    }



    static void search_route(int start_point){

        measure_distance_all(start_point);

        Collections.sort(a_distance);

        int next;
        int pre_point = start_point;
        while((next = get_next_point(pre_point)) != -1){
            outputZ.add(next);
            visited.set(next, true);
            pre_point = next;
        }

    }


    static double calculate_allway(){

        double allway = 0;
        int id1, id2;
        double x1, x2, y1, y2;

        for(int i = 0; i<outputZ.size() - 1 ; i++){
            id1 = outputZ.get(i);
            id2 = outputZ.get(i+1);

            x1 = inputX.get(id1);
            x2 = inputX.get(id2);

            y1 = inputY.get(id1);
            y2 = inputY.get(id2);

            allway += Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2));

        }


        id1 = outputZ.get(outputZ.size() - 1);
        id2 = outputZ.get(0);
        x1 = inputX.get(id1);
        x2 = inputX.get(id2);

        y1 = inputY.get(id1);
        y2 = inputY.get(id2);

        allway += Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2));

        return allway;
    }


    static void output_result(String output_file_path){
        /* 入力配列の読み込んだものを標準出力に出力する */

        System.out.println("index");

        for(int i = 0; i<inputX.size();i++){
            System.out.println(outputZ.get(i));
        }


        try{
            FileWriter fw = new FileWriter(output_file_path);
            for(int i = 0; i<inputX.size();i++){
                fw.write(String.format("%d",outputZ.get(i)) + "\r\n");
            }
            fw.close();
        } catch(IOException ex){
            ex.printStackTrace();
        }

     }



    public static void main(String args[]){
        if(args.length <= 0){
            System.out.println("There is no command argument.");
            return;
        }


        String input_file_path = args[0];
        String folder_path = new File(input_file_path).getParent();

        int begin_index = input_file_path.length() - 5;
        String input_file_number = input_file_path.substring(begin_index, begin_index + 1);


        String output_file_number = input_file_number;
        String output_file_path = folder_path + "\\solution_yours_" + output_file_number + ".csv";


        /* 入力 */
        System.out.println(input_file_path);

        import_XY_fromInputFile(args[0]);


        /* 処理 */
        int start_point = search_start_point();
        outputZ.add(start_point);
        visited.set(start_point, true);

        search_route(start_point);

        double allway = calculate_allway();



        /* 出力 */
        output_result(output_file_path);


        System.out.println(allway);
        /* 処理終了メッセージを出す */
        System.out.println("処理終了");

    }

}