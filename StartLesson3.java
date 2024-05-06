package Lesson3;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

public class StartLesson3 {
    @SneakyThrows
    public static void main(String... args) {
        Fraction cmd = new Fraction(2,5);
        // Создание объекта с кэшированием результатов расчета
        Fractionable chachableCmd=Utils.cache(cmd);
        // Вызов метода для расчета результата деления. Второй вызов возвращает значение из кэша
        System.out.println("");
        System.out.println("1.Вызов двух методов расчета для текущего состояния объекта");
        System.out.println("Результат: " + chachableCmd.doubleValue());

        System.out.println("2.Вызов двух методов расчета для текущего состояния объекта");
        System.out.println("Результат: " + chachableCmd.doubleValue());
        Thread.sleep(1100, TimeUnit.MILLISECONDS.ordinal());
        chachableCmd.setDenum(10);
        System.out.println("3. Вызов после изменения состояние объекта");
        System.out.println("Результат: " + chachableCmd.doubleValue());
        System.out.println("4. Вызов после изменения состояние объекта");
        System.out.println("Результат: " + chachableCmd.doubleValue());
    }
}

