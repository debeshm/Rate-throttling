import java.lang.Math;
import java.lang.Thread;

public class TokenBucket {
    private final long capacity; // max tokens in the bucket
    private final long refillRate; // tokens per second
    private long tokens;
    private long lastRefillTimestamp;

    /**
     * Create a TokenBucket.
     *
     * @param capacity    The maximum capacity of the bucket.
     * @param refillRate  The rate at which tokens are added to the bucket per second.
     */
    public TokenBucket(long capacity, long refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = 0; // Start with a full bucket.
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    /**
     * Attempt to consume a single token from the bucket. If it was consumed, return true, otherwise return false.
     */
    public synchronized boolean tryConsume() {
        refill();

        if (tokens > 0) {
            tokens--;
            return true;
        }

        return false;
    }

    /**
     * Refill tokens according to the time passed since last refill and the refill rate.
     */
    private void refill() {
        long now = System.currentTimeMillis();
        long timeDelta = now - lastRefillTimestamp; // timeDelta is in milliseconds
        long tokensToAdd = timeDelta * refillRate / 1000; // convert milliseconds to seconds
        if (tokensToAdd > 0) {
            tokens = Math.min(capacity, tokens + tokensToAdd);
            lastRefillTimestamp = now;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TokenBucket bucket = new TokenBucket(5, 3); // Capacity of 5 tokens, refill rate of 1 token/second

        int i=0;
        while(true) {
            System.out.println("Attempt " + (i + 1) + ": " + (bucket.tryConsume() ? "Consumed" : "Failed to consume"));
            Thread.sleep(100); // Attempt at every 100 milli-ssecond interval.
            i++;
        }
    }
}

