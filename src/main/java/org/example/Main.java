package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Collator;
import java.util.*;

public class Main {

    private static final Collator russianCollator = Collator.getInstance(new Locale("ru", "RU"));
    private static final Collator englishCollator = Collator.getInstance(new Locale("en", "US"));
    private static final int CONSOLE_VALUE = 1;
    private static final int FILE_VALUE = 2;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int mode = getMode();
        String text = getText();

        switch (mode) {
            case (1): {
                String keyword = getKeyWord();
                // Режим шифрования
                double lenSqr = Math.pow(keyword.length(), 2);
                int blocks = (int) Math.ceil(text.length() / lenSqr);
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < blocks; i++) {
                    String textBlock;
                    if (i != blocks - 1) {
                        textBlock = text.substring(i * (int) lenSqr, (i + 1) * (int) lenSqr);
                    } else {
                        textBlock = text.substring(i * (int) lenSqr);
                    }
                    builder.append(encryptText(textBlock, keyword));
                }
                saveResult(builder.toString(), "зашифрованный");
                break;
            }
            case (2): {
                String keyword = getKeyWord();
                // Режим расшифровки
                double lenSqr = Math.pow(keyword.length(), 2);
                int blocks = (int) Math.ceil(text.length() / lenSqr);
                StringBuilder builder = new StringBuilder();

                for (int i = 0; i < blocks; i++) {
                    String textBlock;
                    if (i != blocks - 1) {
                        textBlock = text.substring(i * (int) lenSqr, (i + 1) * (int) lenSqr);
                    } else {
                        textBlock = text.substring(i * (int) lenSqr);
                    }
                    builder.append(decryptText(textBlock, keyword));
                }
                saveResult(builder.toString().replaceAll("z+$", ""), "расшифрованный");
                break;
            }
            case (3): {
                String result = getResult();
                //Режим взлома
                String decryptedText = crack(text, result);
                saveResult(decryptedText, "расшифрованный");
                break;
            }
            default: {
                System.out.println("Некорректный выбор режима.");
            }
        }
    }

    private static int getMode() {
        System.out.println("Выберите режим работы:");
        System.out.println("1. Шифрование");
        System.out.println("2. Расшифровка");
        System.out.println("3. Режим взлома");
        int mode = scanner.nextInt();
        scanner.nextLine();
        return mode;
    }

    private static String getKeyWord() {
        System.out.println("Введите ключевое слово:");
        return scanner.nextLine();
    }

    private static String getResult() {
        System.out.println("Выберите как ввести результат:");
        System.out.println("1. Ввести вручную");
        System.out.println("2. Прочитать из файла");

        int choice = scanner.nextInt();
        scanner.nextLine();

        String text;
        switch (choice) {
            case CONSOLE_VALUE: {
                text = getTextFromConsole();
                break;
            }
            case FILE_VALUE: {
                text = getTextFromFile();
                break;
            }
            default: {
                System.out.println("Некорректный выбор.");
                return null;
            }
        }
        return text.trim().toLowerCase().replaceAll(" ", "");
    }

    private static String getText() {
        System.out.println("Выберите источник текста:");
        System.out.println("1. Ввести текст вручную");
        System.out.println("2. Прочитать текст из файла");

        int choice = scanner.nextInt();
        scanner.nextLine();

        String text;
        switch (choice) {
            case CONSOLE_VALUE: {
                text = getTextFromConsole();
                break;
            }
            case FILE_VALUE: {
                text = getTextFromFile();
                break;
            }
            default: {
                System.out.println("Некорректный выбор.");
                return null;
            }
        }
        return text.trim().toLowerCase().replaceAll(" ", "");
    }

    private static String getTextFromFile() {
        String text;
        System.out.println("Введите путь к файлу:");
        String filePath = scanner.nextLine();
        try {
            text = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
            return "";
        }
        return text;
    }

    private static String getTextFromConsole() {
        String text;
        System.out.println("Введите текст:");

        text = scanner.nextLine();
        return text;
    }

    // Метод для кодирования текста
    private static String encryptText(String text, String keyword) {
        int length = keyword.length();
        char[][] table = new char[length][length];
        char defaultChar = 'z';
        int charIndex = 0;

        // Заполняем таблицу
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (charIndex < text.length()) {
                    table[i][j] = text.charAt(charIndex++);
                } else {
                    table[i][j] = defaultChar;
                }
            }
        }

        System.out.println("\nШаг 1: Таблица после заполнения:");
        printTable(table);

        // Сортируем столбцы по алфавитному порядку ключевого слова
        char[] keywordChars = keyword.toCharArray();
        Integer[] columnOrder = new Integer[length];
        for (int i = 0; i < length; i++) {
            columnOrder[i] = i;
        }
        Arrays.sort(columnOrder, Comparator.comparingInt(o -> keywordChars[o]));

        // Переставляем столбцы
        char[][] sortedColumnsTable = new char[length][length];
        for (int i = 0; i < length; i++) {
            int oldIndex = columnOrder[i];
            for (int j = 0; j < length; j++) {
                sortedColumnsTable[j][i] = table[j][oldIndex];
            }
        }

        System.out.println("\nШаг 2: Таблица после сортировки столбцов:");
        printTable(sortedColumnsTable);

        // Сортируем строки по алфавитному порядку ключевого слова
        Integer[] rowOrder = new Integer[length];
        for (int i = 0; i < length; i++) {
            rowOrder[i] = i;
        }
        Arrays.sort(rowOrder, Comparator.comparingInt(o -> keywordChars[o]));

        // Переставляем строки
        char[][] sortedRowsTable = new char[length][length];
        for (int i = 0; i < length; i++) {
            int oldIndex = rowOrder[i];
            for (int j = 0; j < length; j++) {
                sortedRowsTable[i][j] = sortedColumnsTable[oldIndex][j];
            }
        }

        System.out.println("\nШаг 3: Таблица после сортировки строк:");
        printTable(sortedRowsTable);

        // Собираем зашифрованный текст
        StringBuilder encryptedText = new StringBuilder();
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                encryptedText.append(sortedRowsTable[i][j]);
            }
        }

        return encryptedText.toString();
    }

    // Метод для расшифровки текста
    private static String decryptText(String encryptedText, String keyword) {
        int length = keyword.length();
        char[][] table = new char[length][length];
        int charIndex = 0;

        // Заполняем таблицу зашифрованного текста
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                table[i][j] = encryptedText.charAt(charIndex++);
            }
        }

        System.out.println("\nШаг 1: Таблица зашифрованного текста:");
        printTable(table);

        // Сортируем столбцы по алфавитному порядку ключевого слова
        int[] columnOrder = getAlphabeticalOrder(keyword);

        // Переставляем столбцы обратно в исходный порядок
        char[][] sortedColumnsTable = new char[length][length];
        for (int i = 0; i < length; i++) {
            int oldIndex = columnOrder[i];
            for (int j = 0; j < length; j++) {
                sortedColumnsTable[j][i] = table[j][oldIndex];
            }
        }

        System.out.println("\nШаг 2: Таблица после обратной сортировки столбцов:");
        printTable(sortedColumnsTable);

        // Восстанавливаем порядок строк, но в обратном порядке
        int[] rowOrder = getAlphabeticalOrder(keyword);

        // Переставляем строки обратно в исходный порядок
        char[][] sortedRowsTable = new char[length][length];
        for (int i = 0; i < length; i++) {
            int oldIndex = rowOrder[i];
            for (int j = 0; j < length; j++) {
                sortedRowsTable[i][j] = sortedColumnsTable[oldIndex][j];
            }
        }

        System.out.println("\nШаг 3: Таблица после обратной сортировки строк:");
        printTable(sortedRowsTable);

        // Собираем расшифрованный текст
        StringBuilder decryptedText = new StringBuilder();
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                decryptedText.append(sortedRowsTable[i][j]);
            }
        }

        // Убираем лишние 'z' из конца строки
        return decryptedText.toString().replaceAll("z+$", "");
    }

    public static int[] getAlphabeticalOrder(String word) {
        // Преобразуем слово в массив символов
        char[] chars = word.toCharArray();
        // Сохраняем пары (буква, индекс)
        Character[] charArray = new Character[chars.length];
        for (int i = 0; i < chars.length; i++) {
            charArray[i] = chars[i];
        }

        Arrays.sort(charArray);
        // Создаем список для хранения порядкового номера каждой буквы, учитывая повторения
        Map<Character, List<Integer>> charPositions = new HashMap<>();

        // Заполняем map с индексами для каждой буквы
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            charPositions.putIfAbsent(c, new ArrayList<>());
            charPositions.get(c).add(i);  // Порядковый номер начинается с 1
        }

        // Массив для хранения порядковых номеров для исходного слова
        int[] order = new int[word.length()];
        for (int i = 0; i < word.length(); i++) {
            char currentChar = word.charAt(i);
            // Извлекаем порядок для данной буквы, учитывая повторения
            order[i] = charPositions.get(currentChar).remove(0);  // Забираем первый номер для этой буквы
        }

        return order;
    }

    private static String detectLanguage(String text) {
        if (text.matches(".*\\p{IsCyrillic}.*")) {
            return "ru"; // Русский
        } else if (text.matches(".*\\p{IsLatin}.*")) {
            return "en"; // Английский
        }
        return "unknown"; // Неопознанный язык
    }

    private static void saveResult(String resultText, String operation) {
        System.out.println("Выберите, куда сохранить результат:");
        System.out.println("1. Вывести на экран");
        System.out.println("2. Сохранить в файл");
        int outputChoice = scanner.nextInt();
        scanner.nextLine(); // Очистка буфера

        if (outputChoice == 1) {
            System.out.println("\n" + operation + " текст:");
            System.out.println(resultText);
        } else if (outputChoice == 2) {
            System.out.println("Введите путь для сохранения файла:");
            String relativePath = scanner.nextLine();
            try {
                // Создание пути относительно текущей рабочей директории
                String currentDir = System.getProperty("user.dir");
                String filePath = Paths.get(currentDir, relativePath).toString();
                Path path = Paths.get(filePath);

                // Создание всех необходимых директорий
                Files.createDirectories(path.getParent());

                // Сохранение результата в файл
                Files.write(path, resultText.getBytes());
                System.out.println("Результат успешно сохранён в файл: " + filePath);
            } catch (IOException e) {
                System.out.println("Ошибка при сохранении файла: " + e.getMessage());
            }
        } else {
            System.out.println("Некорректный выбор.");
        }
    }

    private static String crack(String encryptedText, String findText) {
        String result = "Failed to Crack Text";
        findText = findText.replaceAll(" ", "").trim().toLowerCase();

        for (int i = 2; i <= 11; i++) {
            System.out.println(i);
            List<int[]> permutations = getPermutations(i);
            boolean textWasFind = false;
            double lenSqr = Math.pow(i, 2);
            int blocks = (int) Math.ceil(encryptedText.length() / lenSqr);

            for (int[] permutation : permutations) {
                StringBuilder builder = new StringBuilder();
                for (int j = 0; j < blocks; j++) {
                    String textBlock;
                    if (j != blocks - 1) {
                        textBlock = encryptedText.substring(j * (int) lenSqr, (j + 1) * (int) lenSqr);
                    } else {
                        textBlock = encryptedText.substring(j * (int) lenSqr);
                    }
                    builder.append(decryptCrack(textBlock, permutation));
                }
                String decryptedStr = builder.toString().replaceAll("z+$", "");

                if (decryptedStr.equals(findText)) {
                    textWasFind = true;
                    result = findText;

                    System.out.println(addAsciiToNumbers(permutation));
                    System.out.println(Arrays.toString(permutation));
                    break;
                }
            }

            if (textWasFind) {
                break;
            }
        }
        return result;
    }

    private static String decryptCrack(String encryptedText, int[] order) {
        int length = order.length;
        char defaultChar = 'z';
        char[][] table = new char[length][length];
        int charIndex = 0;

        // Заполняем таблицу зашифрованного текста
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (charIndex < encryptedText.length()) {
                    table[i][j] = encryptedText.charAt(charIndex++);
                } else {
                    table[i][j] = defaultChar;
                }
            }
        }

        // Переставляем столбцы обратно в исходный порядок
        char[][] sortedColumnsTable = new char[length][length];
        for (int i = 0; i < length; i++) {
            int oldIndex = order[i];
            for (int j = 0; j < length; j++) {
                sortedColumnsTable[j][i] = table[j][oldIndex];
            }
        }

        // Восстанавливаем порядок строк, но в обратном порядке
        int[] rowOrder = order;

        // Переставляем строки обратно в исходный порядок
        char[][] sortedRowsTable = new char[length][length];
        for (int i = 0; i < length; i++) {
            int oldIndex = rowOrder[i];
            for (int j = 0; j < length; j++) {
                sortedRowsTable[i][j] = sortedColumnsTable[oldIndex][j];
            }
        }

        // Собираем расшифрованный текст
        StringBuilder decryptedText = new StringBuilder();
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                decryptedText.append(sortedRowsTable[i][j]);
            }
        }

        return decryptedText.toString();
    }

    public static List<int[]> getPermutations(int size) {
        List<int[]> result = new ArrayList<>();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = i;
        }
        generatePermutations(array, 0, result);
        return result;
    }

    private static void generatePermutations(int[] array, int index, List<int[]> result) {
        if (index == array.length) {
            result.add(array.clone());  // Добавляем текущую перестановку
            return;
        }

        for (int i = index; i < array.length; i++) {
            swap(array, i, index);  // Меняем элементы
            generatePermutations(array, index + 1, result);  // Рекурсивный вызов
            swap(array, i, index);  // Возвращаем элементы на место (backtrack)
        }
    }

    private static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }


    public static String addAsciiToNumbers(int[] numbers) {
        StringBuilder result = new StringBuilder();

        // Кодировка буквы 'a' в ASCII
        int asciiA = 97;

        // Проходим по массиву чисел
        for (int number : numbers) {
            // Прибавляем к числу кодировку буквы 'a' и преобразуем в символ
            char newChar = (char) (number + asciiA);
            // Добавляем символ в строку
            result.append(newChar);
        }

        // Возвращаем строку
        return result.toString();
    }

    // Метод для печати таблицы
    private static void printTable(char[][] table) {
        for (char[] row : table) {
            for (char c : row) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
    }
}