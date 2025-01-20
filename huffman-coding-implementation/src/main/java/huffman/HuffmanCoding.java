package huffman;

import priorityqueue.PriorityQueue;
import java.io.*;
import java.util.*;

public class HuffmanCoding {
    static class Node implements Comparable<Node> {
        char character;
        int frequency;
        Node left, right;

        Node(char character, int frequency) {
            this.character = character;
            this.frequency = frequency;
        }

        Node(int frequency, Node left, Node right) {
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.frequency, other.frequency);
        }
    }

    private static Map<Character, String> huffmanCodes = new HashMap<>();
    private static Node root;

    public static void main(String[] args) throws IOException {
        String inputFile = "plik.txt";
        String encodedFile = "zaszyfrowany_plik.txt";
        String decodedFile = "odszyfrowany_plik.txt";

        String text = readFile(inputFile);
        System.out.println("Wczytano tekst z pliku: " + inputFile);

        Map<Character, Integer> frequencyMap = calculateFrequency(text);
        System.out.println("\nCzęstotliwość występowania znaków:");
        frequencyMap.forEach((k, v) -> System.out.println("'" + k + "': " + v));

        PriorityQueue<Node> queue = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            queue.add(new Node(entry.getKey(), entry.getValue()));
        }

        root = buildHuffmanTree(queue);
        generateCodes(root, "");

        System.out.println("\nWygenerowane kody Huffmana:");
        huffmanCodes.forEach((k, v) -> System.out.println("'" + k + "': " + v));

        String encodedText = compress(text);
        writeEncodedFile(encodedFile, encodedText, frequencyMap);
        System.out.println("\nZakodowany tekst zapisano do pliku: " + encodedFile);

        String decodedText = decompress(encodedText);
        writeFile(decodedFile, decodedText);
        System.out.println("Zdekodowany tekst zapisano do pliku: " + decodedFile);

        int originalSize = text.length() * 16; // Zakładając UTF-16
        int compressedSize = encodedText.length();
        double compressionRatio = (1 - (double)compressedSize/originalSize) * 100;

        System.out.println("\nStatystyki kompresji:");
        System.out.println("Rozmiar oryginalny: " + originalSize + " bitów");
        System.out.println("Rozmiar po kompresji: " + compressedSize + " bitów");
        System.out.printf("Współczynnik kompresji: %.2f%%\n", compressionRatio);

        if (text.equals(decodedText)) {
            System.out.println("\nWeryfikacja: Tekst został poprawnie zdekodowany!");
        } else {
            System.out.println("\nBŁĄD: Zdekodowany tekst różni się od oryginału!");
        }
    }

    private static String readFile(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        StringBuilder text = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            text.append(line).append("\n");
        }
        reader.close();
        return text.toString();
    }

    private static void writeFile(String fileName, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(content);
        writer.close();
    }

    private static Map<Character, Integer> calculateFrequency(String text) {
        Map<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }
        return frequencyMap;
    }

    private static Node buildHuffmanTree(PriorityQueue<Node> queue) {
        while (queue.size() > 1) {
            Node left = queue.poll();
            Node right = queue.poll();
            Node parent = new Node(left.frequency + right.frequency, left, right);
            queue.add(parent);
        }
        return queue.poll();
    }

    private static void generateCodes(Node node, String code) {
        if (node == null) return;

        if (node.left == null && node.right == null) {
            huffmanCodes.put(node.character, code);
        }

        generateCodes(node.left, code + "0");
        generateCodes(node.right, code + "1");
    }

    private static String compress(String text) {
        StringBuilder encodedText = new StringBuilder();
        for (char c : text.toCharArray()) {
            encodedText.append(huffmanCodes.get(c));
        }
        return encodedText.toString();
    }

    private static void writeEncodedFile(String fileName, String encodedText, Map<Character, Integer> frequencyMap) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

        for (Map.Entry<Character, String> entry : huffmanCodes.entrySet()) {
            writer.write(entry.getKey() + ":" + entry.getValue());
            writer.newLine();
        }

        writer.write("====");
        writer.newLine();

        writer.write(encodedText);
        writer.close();
    }

    private static String decompress(String encodedText) {
        StringBuilder decodedText = new StringBuilder();
        Node current = root;

        for (char bit : encodedText.toCharArray()) {
            if (bit == '0') {
                current = current.left;
            } else {
                current = current.right;
            }

            if (current.left == null && current.right == null) {
                decodedText.append(current.character);
                current = root;
            }
        }

        return decodedText.toString();
    }
}
