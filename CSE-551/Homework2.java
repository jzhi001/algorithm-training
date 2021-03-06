import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

public class Homework2 {

    static class Hospital {
        String name;
        
        // since we never look back, iterator is well enough to go
        Iterator<String> preferences;

        int positions;

        LinkedHashSet<String> offers;


        Hospital(String name, int positions, List<String> preferences) {
            this.name = name;
            this.preferences = preferences.iterator();
            this.positions = positions;
            this.offers = new LinkedHashSet<>();
        }

        public String nextPreference(){
            return this.preferences.next();
        }

        public boolean isOpen() {
            return positions > 0 && this.preferences.hasNext();
        }

        public void offerTo(String student) {
            positions--;
            offers.add(student);
        }

        public void cancelOffer(String student) {
            positions++;
            offers.remove(student);
        }
    }

    static class Student {

        String name;
        String offer;
        // hospital name => ranking
        Map<String, Integer> preference;

        Student(String name, List<String> preferences) {
            this.name = name;
            this.preference = new HashMap<>();

            for (int i = 0; i < preferences.size(); i++) {
                preference.put(preferences.get(i), i);
            }
        }

        void acceptOffer(String hospital) {
            this.offer = hospital;
        }

        // haven't reveive any offer
        boolean isFree() {
            return offer == null;
        }

        // prefer [h] than current offer?
        boolean prefer(String h) {
            if (isFree()) {
                throw new IllegalStateException("student has no offer");
            }
            int cur = preference.getOrDefault(h, Integer.MAX_VALUE);
            return cur < preference.get(offer);
        }

        @Override
        public String toString() {
            return String.format("%s: %s", name, preference);
        }
    }

    public static void galeShapley(Map<String, Hospital> hospitals,
                                   Map<String, Student> students) {

        Queue<Hospital> queue = new LinkedList<>(hospitals.values());

        while (!queue.isEmpty()) {

            Hospital h = queue.poll();
            String studentName = h.nextPreference();
            Student s = students.get(studentName);


            if (s == null) {
                throw new RuntimeException("no user find: " + studentName);
            }

            if(s.isFree() || s.prefer(h.name)){
                if (!s.isFree()) {
                    // return offer of current hospital
                    Hospital prev = hospitals.get(s.offer);
                    prev.cancelOffer(s.name);

                    if (prev.isOpen()) {
                        queue.add(prev);
                    }
                }

                s.acceptOffer(h.name);
                h.offerTo(s.name);
            }

            if (h.isOpen()) {
                queue.add(h);
            }
        }
    }

    /**
     * Input format:
     * First line contians 2 numbers: hospital count and student count
     * for next N lines, each line contains information of a hospital:
     * <hospital-name> <positions> <preference-count> <preference-1> <preference-2> ...
     * for next M lines, each line contains information of a student:
     * <student-name> <preference-count> <preference-1> <preference-2> ...
     * 
     * Example input is in input.txt
     * please run {@code java Homework2 < input.txt > output.txt} 
     *
     * Note: all {@code Map} implementations are {@code LinkedHashMap},
     * so you don't have to worry about the iteration sequence :)
     */
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        int hospitalNum = scanner.nextInt();
        int studentNum = scanner.nextInt();

        Map<String, Hospital> hospitals = new LinkedHashMap<>();
        Map<String, Student> students = new LinkedHashMap<>();

        for (int i = 0; i < hospitalNum; i++) {
            String name = scanner.next();
            int position = scanner.nextInt();
            int preferenceCnt = scanner.nextInt();

            List<String> preference = new ArrayList<>();

            for (int j = 0; j < preferenceCnt; j++) {
                preference.add(scanner.next());
            }
            hospitals.put(name, new Hospital(name, position, preference));
        }


        for (int i = 0; i < studentNum; i++) {

            String name = scanner.next();
            int preferenceCnt = scanner.nextInt();
            List<String> preference = new ArrayList<>();

            for (int j = 0; j < preferenceCnt; j++) {
                preference.add(scanner.next());
            }

            students.put(name, new Student(name, preference));
        }

        galeShapley(hospitals, students);

        for (Hospital hospital : hospitals.values()) {
            System.out.printf("%s: %s\n", hospital.name, hospital.offers);
        }

        scanner.close();
    }

}
