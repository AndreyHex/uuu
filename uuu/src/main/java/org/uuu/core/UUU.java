package org.uuu.core;

import org.uuu.core.scanner.Scanner;
import org.uuu.core.scanner.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class UUU {
    public static void main(String[] args) {
        if (args.length == 0) runRepl();
        else if (args.length != 1) System.out.println("Expected only one arg");
        else {
            String code;
            try {
                code = readFile(Path.of(args[0]));
            } catch (IOException e) {
                System.out.println("Error while reading file: " + e.getMessage());
                return;
            }
            run(code);

        }
    }

    private static void runRepl() {
        try (InputStreamReader inputStream = new InputStreamReader(System.in)) {
            BufferedReader br = new BufferedReader(inputStream);
            while (true) {
                System.out.print("> ");
                String line = br.readLine();
                if (line == null) break;
                run(line);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static String readFile(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes);
    }

    private static void run(String code) {
        Scanner scanner = new Scanner(code);
        List<Token> tokens = scanner.scan();
        System.out.println(tokens.stream().map(Token::toString).collect(Collectors.joining(" ")));
    }

    private static void error(String line, int n, String message) {
        System.out.println(message);
        System.out.println(n + "| " + line);
    }
}
