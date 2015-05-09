
package tk.luoxing123.utils;

import java.util.List;
import java.util.stream.IntStream;




public class PrimeNumber {
    public static void main(String[] args) throws Exception {
        IntStream.range(1,1000)
            .filter(i -> isPrime(i))
            .forEach(System.out::println);
    }
    public static boolean isPrime(int n){
        return    IntStream.rangeClosed(2,n-1).boxed()
            .collect(new Collectors.PrimeNumbersCollector())
            .stream().noneMatch(i ->  n%i==0);
    }
    public static List<Integer> primiesWithCollector(int n){
        return IntStream.rangeClosed(2,n).boxed()
            .collect( new Collectors.PrimeNumbersCollector());
    }
    
}
