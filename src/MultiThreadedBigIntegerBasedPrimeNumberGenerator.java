import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;

class MultithreadCalculate extends Thread {
    BigInteger PrimeNumCalculate = new BigInteger("-1");
    BigInteger indexNum = new BigInteger("-1");

    public BigInteger getPrimeNumCalculate() {
        return PrimeNumCalculate;
    }

    public void setPrimeNumCalculate(BigInteger primeNumCalculate) {
        PrimeNumCalculate = primeNumCalculate;
    }

    public BigInteger getIndexNum() {
        return indexNum;
    }

    public void setIndexNum(BigInteger indexNum) {
        this.indexNum = indexNum;
    }

    public void run() {
        try {
            boolean isPrime = true;
            BigInteger staticNumChecker = new BigInteger("2");
            int compare = -1;
            long startTime = System.nanoTime();
            while (compare != 0 ) {
                compare = staticNumChecker.compareTo(getPrimeNumCalculate());

                if (compare < 0) {
                    BigInteger modResult = getPrimeNumCalculate().mod(staticNumChecker);
                    if (modResult.equals(MultiThreadedBigIntegerBasedPrimeNumberGenerator.mod0)) {
                        isPrime = false;
                        MultiThreadedBigIntegerBasedPrimeNumberGenerator.primeArray[0][getIndexNum().intValue()] = getPrimeNumCalculate();
                        MultiThreadedBigIntegerBasedPrimeNumberGenerator.primeArray[1][getIndexNum().intValue()] = BigInteger.valueOf(0);
                        break;
                    }
                }

                staticNumChecker = staticNumChecker.add(BigInteger.valueOf(1));
            }

            if (isPrime) {
                MultiThreadedBigIntegerBasedPrimeNumberGenerator.primeArray[0][getIndexNum().intValue()] = getPrimeNumCalculate();
                MultiThreadedBigIntegerBasedPrimeNumberGenerator.primeArray[1][getIndexNum().intValue()] = BigInteger.valueOf(1);
            }
            long endTime = System.nanoTime();
            long timeElapsed = endTime - startTime;
            MultiThreadedBigIntegerBasedPrimeNumberGenerator.timeArray[getIndexNum().intValue()] = (timeElapsed/1000000);
            System.out.println("Thread " + Thread.currentThread().getId() + "; Index: " + getIndexNum() + "; Number: " + getPrimeNumCalculate() + "; isPrime: " + isPrime + "; Time To Calculate: " + (timeElapsed/1000000) + " ms");
        }
        catch (Exception e) {
            System.out.println("Exception is caught");
        }
    }
}

public class MultiThreadedBigIntegerBasedPrimeNumberGenerator {
    static public BigInteger primeBase = new BigInteger("1");
    static public BigInteger[][] primeArray;
    public static long [] timeArray;
    static public BigInteger totalPrimeCounter = new BigInteger("0");
    static public BigInteger mod2 = new BigInteger("2");
    static public BigInteger mod0 = new BigInteger("0");
    private static File file = null;
    private static PrintWriter out = null;
    private static int cores;

    private static void fillArray() {
        for (int i = 0; i < cores; i++) {
            primeArray[0][i] = BigInteger.valueOf(-1);
        }

        for (int i = 0; i < cores; i++) {
            primeArray[1][i] = BigInteger.valueOf(-1);
        }

        for (int i = 0; i < cores; i++) {
            timeArray[i] = -1;
        }
    }

    private static void CalculatePrimeNumberInBatches() {
        while (true) {
            primeArray = new BigInteger[2][cores];
            timeArray = new long[cores];
            fillArray();
            for (int i = 0; i < cores; i++) {
                MultithreadCalculate multithreadCalculate = new MultithreadCalculate();
                primeBase = primeBase.add(mod2);
                multithreadCalculate.setPrimeNumCalculate(primeBase);
                multithreadCalculate.setIndexNum(BigInteger.valueOf(i));
                multithreadCalculate.start();
            }

            while (true) {
                boolean flag = false;
                for (int i = 0; i < cores; i++) {
                    if ((primeArray[1][i].equals(mod0) || primeArray[1][i].equals(BigInteger.valueOf(1)))) {
                        flag = true;
                    } else {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    break;
                }
            }

            PrintPrimes();
        }
    }

    private static void PrintPrimes() {
        for (int i = 0; i < cores; i++) {
            if (primeArray[1][i].equals(BigInteger.valueOf(1))) {
                totalPrimeCounter = totalPrimeCounter.add(BigInteger.valueOf(1));
                out.println(totalPrimeCounter + ". Time to calculate: " + timeArray[i] + "; Prime Number: " + primeArray[0][i]);
            }
        }

        out.flush();
    }

    public static void main(String[] args) throws FileNotFoundException {
        cores = Runtime.getRuntime().availableProcessors();
        file = new File(System.getProperty("user.home") + "/Desktop" + "/PrimeNumber.txt");
        if (file.isFile()) {
            if (file.length() != 0) {
                System.out.print("File Already Exit. Would you like to continue? <Yes> or <No>: ");
                Scanner in = new Scanner(System.in);
                String flag = in.nextLine();

                while (!(flag.equalsIgnoreCase("Yes") || flag.equalsIgnoreCase("No"))) {
                    System.out.println("INVALID ANSWER. TYPE YES OR NO!");
                    System.out.print("Enter 'Yes' or 'No': ");
                    flag = in.nextLine();
                }

                if (flag.equalsIgnoreCase("Yes")) {
                    ArrayList<String> tempArr = new ArrayList<>();
                    Scanner lastLine = new Scanner(file);
                    while (lastLine.hasNextLine()) {
                        String specificLine = lastLine.nextLine();
                        tempArr.add(specificLine);
                    }

                    out = new PrintWriter(file);
                    for (int i = 0; i < tempArr.size(); i++) {
                        out.println(tempArr.get(i));
                        out.flush();
                    }
                    String[] splitString = tempArr.get(tempArr.size() - 1).split(" ");
                    totalPrimeCounter = new BigInteger(splitString[0].substring(0, splitString[0].length() - 1));
                    primeBase = new BigInteger(splitString[7]);

                } else {
                    out = new PrintWriter(file);

                    long startTime = System.nanoTime();
                    long endTime = System.nanoTime();
                    long timeElapsed = endTime - startTime;
                    totalPrimeCounter = totalPrimeCounter.add(BigInteger.valueOf(1));
                    out.println(totalPrimeCounter + ". Time To Calculate: " + (timeElapsed / 1000000) + " ms; Prime Number: " + 2);
                    out.flush();
                }
            } else {
                out = new PrintWriter(file);

                long startTime = System.nanoTime();
                long endTime = System.nanoTime();
                long timeElapsed = endTime - startTime;
                totalPrimeCounter = totalPrimeCounter.add(BigInteger.valueOf(1));
                out.println(totalPrimeCounter + ". Time To Calculate: " + (timeElapsed / 1000000) + " ms; Prime Number: " + 2);
                out.flush();
            }
        } else {
            out = new PrintWriter(file);

            long startTime = System.nanoTime();
            long endTime = System.nanoTime();
            long timeElapsed = endTime - startTime;
            totalPrimeCounter = totalPrimeCounter.add(BigInteger.valueOf(1));
            out.println(totalPrimeCounter + ". Time To Calculate: " + (timeElapsed / 1000000) + " ms; Prime Number: " + 2);
            out.flush();
        }
        CalculatePrimeNumberInBatches();
    }
}
