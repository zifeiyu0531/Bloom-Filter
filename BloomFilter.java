import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;

class BloomFilter<E> {
    private BitSet bitSet; // 过滤器
    private int size; // 过滤器长度
    private int hashNums; // hash个数
    private volatile int currentElementNums; // 过滤器已存元素个数

    private static final MessageDigest hashFunction; // hash函数
    static {
        MessageDigest tmp;
        try {
            tmp = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            tmp = null;
        }
        hashFunction = tmp;
    }

    private BloomFilter(Builder<E> builder) {
        this.size = (int) Math
                .ceil((-builder.elementNums * Math.log(builder.falsePositiveRate)) / Math.pow((Math.log(2)), 2));
        this.hashNums = (int) Math.ceil((this.size / builder.elementNums) * Math.log(2));
        this.bitSet = new BitSet(this.size);
    }

    public static class Builder<E> {
        private int elementNums; // 元素个数
        private double falsePositiveRate; // 错误率

        public Builder() {
        }

        public Builder<E> setElementNums(int elementNums) {
            this.elementNums = elementNums;
            return this;
        }

        public Builder<E> setFalsePositiveRate(double falsePositiveRate) {
            this.falsePositiveRate = falsePositiveRate;
            return this;
        }

        public BloomFilter<E> build() {
            return new BloomFilter<>(this);
        }

    }

    public void add(E element) {
        add(element.toString().getBytes());
    }

    private void add(byte[] data) {
        int[] hashes = getHashes(data);
        for (int hash : hashes) {
            this.bitSet.set(Math.abs(hash % this.size), true);
        }
        currentElementNums++;
    }

    public boolean contains(E element) {
        return contains(element.toString().getBytes());
    }

    private boolean contains(byte[] data) {
        int[] hashes = getHashes(data);
        for (int hash : hashes) {
            if (!this.bitSet.get(Math.abs(hash % this.size))) {
                return false;
            }
        }
        return true;
    }

    private int[] getHashes(byte[] data) {
        int res[] = new int[this.hashNums];
        int k = 0;
        byte salt = 0;

        while (k < this.hashNums) {
            hashFunction.update(salt++);
            byte[] md5Result = hashFunction.digest(data);

            for (int i = 0; i < md5Result.length / 4 && k < this.hashNums; i++) {
                int hash = 0;
                for (int j = (i * 4); j < (i * 4) + 4; j++) {
                    hash <<= 8;
                    hash |= ((int) md5Result[j]) & 0xFF;
                }
                res[k] = hash;
                k++;
            }
        }
        return res;
    }

    public int count() {
        return this.currentElementNums;
    }

    public synchronized void reset() {
        this.bitSet = new BitSet(this.size);
        this.currentElementNums = 0;
    }
}