import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Test {
    public static void main(String[] args) {
        BloomFilter<String> bloomFilter = new BloomFilter.Builder()
                                                .setElementNums(1000)
                                                .setFalsePositiveRate(0.01)
                                                .build();
        bloomFilter.add("abc");
        System.out.println(bloomFilter.contains("abc"));
        System.out.println(bloomFilter.contains("ccc"));
        System.out.println(bloomFilter.count());
        bloomFilter.reset();
        System.out.println(bloomFilter.count());

        BloomFilter<Integer> bloomFilterInt = new BloomFilter.Builder()
                                                .setElementNums(100000)
                                                .setFalsePositiveRate(0.001)
                                                .build();
        Random r = new Random();
        Map<Integer, Boolean> map = new HashMap<>();
        for(int i = 0; i < 1000; i++){
            int rand = r.nextInt(10000);
            map.put(rand, true);
            bloomFilterInt.add(rand);
        }
        System.out.println(String.format("Bloom Filter has %d elements now", bloomFilterInt.count()));
        int falsePositiveCount = 0;
        for(int i = 0; i < 100000; i++){
            int rand = r.nextInt(10000);
            if(!map.keySet().contains(rand) && bloomFilterInt.contains(rand)){
                falsePositiveCount++;
            }
        }
        System.out.println(String.format("After 100000 tests, has %d false positive, the rate is %f", falsePositiveCount, ((float)falsePositiveCount) / 100000));
    }
}
