package Lesson3;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;

class CacheInvokeHandler implements InvocationHandler{
    private Object currentObj;
    // Закэшированные значения будем хранить в коллекции Map. В качестве ключа используется строка из имени метода и времени жизни
    private final Map<String, ConcurrentHashMap<String, Map<Long, Double>>> cache = new ConcurrentHashMap<>();
    CacheInvokeHandler(Object object) {
        this.currentObj = object;

        ScheduledExecutorService cacheCleaner = Executors.newSingleThreadScheduledExecutor(it -> {
            Thread th = new Thread(it);
            th.setDaemon(true);
            return th;
        });

        cacheCleaner.scheduleAtFixedRate(() -> {
            for (Map.Entry<String, ConcurrentHashMap<String, Map<Long, Double>>> cacheEntry : cache.entrySet()) {
                //вытащим из ключа время жизни
                long initLifeTime = Long.parseLong(cacheEntry.getKey().substring(cacheEntry.getKey().lastIndexOf('#') + 1));
                initLifeTime = TimeUnit.NANOSECONDS.convert(initLifeTime, TimeUnit.MILLISECONDS);
                long currentTime;
                long cachedValueLifeTime;
                Iterator<Map.Entry<String, Map<Long, Double>>> cachedValuesIterator = cacheEntry.getValue().entrySet().iterator();
                while (cachedValuesIterator.hasNext()) {
                    Map<Long, Double> pair = cachedValuesIterator.next().getValue();

                    cachedValueLifeTime = pair.entrySet().iterator().next().getKey();

                    currentTime = System.nanoTime();

                    // Если время жизни закэшированного значения истекло, то удаляем
                    if (initLifeTime < (currentTime -  cachedValueLifeTime)) {
                        cachedValuesIterator.remove();
                        System.out.println("@@@@ Поток cacheCleaner: Удалил значение из кэша: " + pair);
                        System.out.println("@@@@ Поток cacheCleaner: Содержимое кэша: " + cache + "\n");
                    }
                }
            }
        }, 1, 10, TimeUnit.MILLISECONDS);
    }



    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method currentMethod = currentObj.getClass().getMethod(method.getName(), method.getParameterTypes());
        String cacheKey = method.getName();

        if (currentMethod.isAnnotationPresent(Cache.class)) {
            System.out.println("Вызов метода " + cacheKey + " через Proxy");

            ConcurrentHashMap<String, Map<Long, Double>> cachedValues;
            Double result;
            cacheKey = cacheKey + '#' + currentMethod.getDeclaredAnnotation(Cache.class).timeValue();

            if (!this.cache.containsKey(cacheKey)) {
                this.cache.put(cacheKey, new ConcurrentHashMap<>());
            }
            cachedValues = this.cache.get(cacheKey);

            if (this.currentObj instanceof Fraction) {
                String stateKey = ((Fraction) (this.currentObj)).getStateKey();
                Map<Long, Double> pair = cachedValues.get(stateKey);
                if (pair != null) {
                    result = pair.entrySet().iterator().next().getValue();
                    System.out.println("Значение найдено в кэше!");
                } else {
                    result = (Double) currentMethod.invoke(this.currentObj, args);
                    System.out.println("Значения нет в кэше!");
                }
                // В любом случае добавить значение в кэш. Если ранее значения не было, то добавится, если было и было обращение, то обновится время начала жизни
                cachedValues.put(stateKey, Pair.of(System.nanoTime(), result));
                System.out.println("Поместили в кэш/обновили время начала жизни.");
                System.out.println("Содержимое кэша: " + cache + "\n");
                return result;
            }
        }
        return method.invoke(currentObj, args);
    }
}