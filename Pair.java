package Lesson3;

import java.util.Collections;
import java.util.Map;
public class Pair {
    private Pair(){};
    public static <T, U> Map<T, U> of(T key, U value) {
        return Collections.singletonMap(key, value);
    }
}
