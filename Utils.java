package Lesson3;
import java.lang.reflect.Proxy;

public class Utils {
    public Utils() {
    }

    public static <T> T cache(T ObjectIncome){
        return(T) Proxy.newProxyInstance(ObjectIncome.getClass().getClassLoader(),
                ObjectIncome.getClass().getInterfaces(),
                new CacheInvokeHandler(ObjectIncome)
                );
    }

}
