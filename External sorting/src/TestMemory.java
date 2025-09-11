public class TestMemory {
    void func(){
        try {
            // Пытаемся выделить массив на 200 МБ
            byte[] bigArray = new byte[200 * 1024 * 1024];
            System.out.println("Выделено успешно");
        } catch (OutOfMemoryError e) {
            System.out.println("Ошибка памяти: " + e.getMessage());
        }
    }
}
