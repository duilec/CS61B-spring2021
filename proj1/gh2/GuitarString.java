package gh2;

// uncomment the following import once you're ready to start this portion
 import deque.Deque;
// maybe more imports
// I would like ArrayDeque when using Java Visualizer
 import deque.ArrayDeque;

//Note: This file will not compile until you complete the Deque implementations
public class GuitarString {
    /** Constants. Do not change. In case you're curious, the keyword final
     * means the values cannot be changed at runtime. We'll discuss this and
     * other topics in lecture on Friday. */
    private static final int SR = 44100;      // Sampling Rate
    private static final double DECAY = .996; // energy decay factor

    /* Buffer for storing sound data. */
    // uncomment the following line once you're ready to start this portion
    private Deque<Double> buffer;
    private double sample;

    /* Create a guitar string of the given frequency.  */
    public GuitarString(double frequency) {
        // Create a buffer with capacity = SR / frequency. You'll need to
        //       cast the result of this division operation into an int. For
        //       better accuracy, use the Math.round() function before casting.
        //       Your should initially fill your buffer array with zeros.
        int capacity = (int)(Math.round(SR / frequency));
        buffer = new ArrayDeque<>();
        while (capacity > 0){
            buffer.addFirst(0.0);
            capacity -= 1;
        }
    }


    /* Pluck the guitar string by replacing the buffer with white noise. */
    public void pluck() {
        // Dequeue everything in buffer, and replace with random numbers
        //       between -0.5 and 0.5. You can get such a number by using:
        //       double r = Math.random() - 0.5;
        //
        //       Make sure that your random numbers are different from each
        //       other. This does not mean that you need to check that the numbers
        //       are different from each other. It means you should repeatedly call
        //       Math.random() - 0.5 to generate new random numbers for each array index.

        // note: Math.random() generates double greater than 0.0 and smaller than 1.0
        int bufferSize = buffer.size();
        while(bufferSize > 0){
            double r = Math.random() - 0.5;
            buffer.removeFirst();
            buffer.addFirst(r);
            bufferSize -= 1;
        }
    }

    /* Advance the simulation one time step by performing one iteration of
     * the Karplus-Strong algorithm.
     */
    public void tic() {
        // Dequeue the front sample and enqueue a new sample that is
        //       the average of the two multiplied by the DECAY factor.
        //       **Do not call StdAudio.play().**

        // note: sample() not affect running of tic()
        double sample = buffer.removeFirst();
        double newFront = buffer.get(0);
        sample = ((sample + newFront) / 2) * DECAY;
        buffer.addLast(sample);
    }

    /* Return the double at the front of the buffer. */
    public double sample() {
        // Return the correct thing.
        sample = buffer.get(0);
        return sample;
    }
}
    // Remove all comments that say TODO when you're done.
