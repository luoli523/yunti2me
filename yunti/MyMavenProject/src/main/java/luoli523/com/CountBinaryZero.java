package luoli523.com;

public class CountBinaryZero {
  public static void main(String[] args) {
    int count = 0;
    int[] a = {3,8,9,4,6,1,142,23,43};


    for(int i = 0; i< a.length; i++) {
      int num = a[i];
      String binaryString = Integer.toBinaryString(num);

      System.out.println("[" + a[i] + "] BinaryString: [" + binaryString + "]");
      System.out.println("zero count: " + countOne(a[i]));
      System.out.println("zero count recursive: " + countOne(a[i]));
      System.out.println();

    }
  }

  public static int countOneRecusive(int num) {
    if(num > 0) {
      return 1 + countOne(num & (num-1));
    }
    return 0;
  }

  public static int countOne(int num) {
    int count = 0;
    while(num > 0) {
      num = num & (num -1);
      count++;
    }
    return count;
  }
}
