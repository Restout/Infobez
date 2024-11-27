package org.example;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ввод текста и ключевого слова
        System.out.println("Введите текст:");
        String text = scanner.nextLine();
        System.out.println("Введите ключевое слово:");
        String keyword = scanner.nextLine();

        // Кодируем текст
        String encryptedText = encryptText(text, keyword);
        System.out.println("\nЗашифрованный текст: " + encryptedText);

        // Расшифровываем текст обратно
        String decryptedText = decryptText(encryptedText, keyword);
        System.out.println("\nРасшифрованный текст: " + decryptedText);
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

        // Сортируем массив по алфавитному порядку
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