import java.util.*;

public class ArrayTest {
    private static int dimention;
    private static int sectionSize;
    private static int[] arr;
    private static List<int[]> segmentsList;

    public static void main(String[] args) {
        getDataFromConsole();
        fillArray();
        showArr();
        getSectionsFromArray();
        showSections();
        System.out.println(getMaxElem());
    }
    private static void showSections(){
        System.out.println( "sections: " + segmentsList.size());
        for(int[] section:segmentsList){
            for(int elem:section){
                System.out.print(elem + ",");
            }
            System.out.println();
        }
    }
    private static int getMaxElem(){
        List<Integer> minList = new ArrayList<Integer>();
        for(int[] section:segmentsList){
            int min = section[0];
            for(int elem:section){
                if(elem<min){
                    min  = elem;
                }
            }
            minList.add(min);
        }
        System.out.println("list of mins:");
        for(int elem:minList){
            System.out.print(elem + ",");
        }
        System.out.println();
        return minList.stream().mapToInt(v->v).max().orElseThrow(NoSuchElementException::new);
    }
    private static void getSectionsFromArray(){
        segmentsList = new ArrayList<int[]>();
        int[] sectionArr;
        for(int elemNum = 0; elemNum < arr.length; elemNum ++){
            sectionArr = new int[sectionSize];
            if (elemNum + sectionSize <= arr.length) {
                for(int i = 0; i<sectionSize; i++){
                    sectionArr[i] = arr[elemNum + i];
                }
                segmentsList.add(sectionArr);
            }
        }
    }
    private static void getDataFromConsole(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("inter array's dimention:...");
        dimention = scanner.nextInt();
        System.out.println("inter section size:...");
        sectionSize = scanner.nextInt();
        System.out.println("you wrote:");
        System.out.println("array's dimention: " + dimention);
        System.out.println("section size: " + sectionSize);
        arr = new int[dimention];
    }
    private static void showArr(){
        System.out.println("arrr");
        for (int elem:arr){
            System.out.print(elem + ",");
        }
        System.out.println();
    }
    private static void fillArray(){
        Random random = new Random();
        for(int i = 0; i< dimention; i++){
            arr[i] = random.nextInt(dimention);
        }
    }
}
