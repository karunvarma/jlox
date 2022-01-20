package com.kv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import com.kv.Scanner;

public class Lox {

    /**
     * used to stop the code execution when error is found
     */
    static boolean hadError = false;

    public static void main(String[] args) throws IOException {

        if(args.length > 1){
            System.out.println("Usage jlox [script]");
            System.exit(64);
        }
        else if(args.length == 1)
        {
            runFile(args[1]);
        }
        else
        {
            runPrompt();
        }
    }

    /**
     * this is used to fire up the interactice prompt(REPL)
     */
    private static void runPrompt() throws IOException {
        InputStreamReader inputStream = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(inputStream);

        while(true){
            System.out.print("> ");
            String line = reader.readLine();
            if(line == null) break;
            run(line);
            // TODO: how to handle exceptions
        }
    }


    /**
     * This method takes the file path as input and starts executing
     * the code withing it at once.
     * @param path
     * @throws IOException
     */
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if(hadError) System.exit(65);
    }

    /**
     *
     * @param source
     */
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokenList = scanner.scanTokens();

        for(Token token : tokenList){
            System.out.println(token);
        }
    }

    static void error(int line,String message){
        report(line,"",message);
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line "+line+"] Error "+where +": " + message);
        hadError = true;
    }


}
