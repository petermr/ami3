package org.contentmine.ami.tools.test;

import java.util.concurrent.Callable;

import picocli.CommandLine;

@CommandLine.Command(
		name = "myprogram",
		subcommands = {
				CommandTest1.FrequencyCommand.class, 
				CommandTest1.HistogramCommand.class}
		)
public class CommandTest1  implements Callable<Void> {

    public CommandTest1(){

    }

    public Void call() {
        System.out.println("Main program called");
        return null;
    }

    public static void main(String[] args){

        String[] input1 = {"frequency", "-id", "1001", "-table", "ex1"};
        String[] input2 = {"histogram", "-id", "1002", "-table", "ex5" };

        CommandLine commandLine = new CommandLine(new CommandTest1());
        System.out.println("==Test1==");
        commandLine.execute(input1);
        System.out.println();

        System.out.println("==Test2==");
        commandLine.execute(input2);
        System.out.println();


    }

    @CommandLine.Command(name = "frequency", description = "Frequency analysis.")
    static class FrequencyCommand implements Callable<Void> {

        @CommandLine.Option(names = {"-id"}, arity = "1..*", description = "Unique case identifier")
        public String id;

        @CommandLine.Option(names = "-table", arity = "1..*", description = "Database table")
        public String table;

        public FrequencyCommand(){

        }

        public Void call() {
            System.out.println("Frequency");
            System.out.println("ID = " + id);
            System.out.println("Table = " + table);
            return null;
        }
    }

    @CommandLine.Command(name = "histogram", description = "Histogram plot.")
    static class HistogramCommand implements Callable<Void> {

        @CommandLine.Option(names = {"-id"}, arity = "1..*", description = "Unique case identifier")
        public String id;

        @CommandLine.Option(names = "-table", arity = "1..*", description = "Database table")
        public String table;

        public HistogramCommand(){

        }

        public Void call() {
            System.out.println("Histogram");
            System.out.println("ID = " + id);
            System.out.println("Table = " + table);
            return null;
        }
    }

}
