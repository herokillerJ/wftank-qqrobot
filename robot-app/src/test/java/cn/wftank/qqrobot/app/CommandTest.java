package cn.wftank.qqrobot.app;


import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;


@CommandLine.Command(name = "query", mixinStandardHelpOptions = true, version = "1.0",
        description = "商品数据库高级查询方式")
@Getter
@Setter
public class CommandTest {

    @CommandLine.Option(names = {"-s", "--size"}, description = "1,2,3,4,5等等" )
    private List<Integer> size;

    public static void main(String... args) {
        String cmd = "-s 5 -s    6  -s   7";
        CommandLine commandLine = new CommandLine(new CommandTest());
        commandLine.parseArgs(cmd.split("[\\s]+"));
        CommandTest commandTest = commandLine.getCommand();
        System.out.println(commandTest.getSize());
        System.out.println(usageString(commandLine, CommandLine.Help.Ansi.OFF));
        System.out.println(commandLine.getUsageMessage());
    }

    public static String usageString(Object annotatedObject, CommandLine.Help.Ansi ansi) {
        return usageString(new CommandLine(annotatedObject), ansi);
    }

    public static String usageString(CommandLine commandLine, CommandLine.Help.Ansi ansi) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        commandLine.usage(new PrintStream(baos, true), ansi);
        String result = baos.toString();

        if (ansi == CommandLine.Help.Ansi.AUTO) {
            baos.reset();
            commandLine.usage(new PrintStream(baos, true));
        } else if (ansi == CommandLine.Help.Ansi.ON) {
            baos.reset();
            commandLine.usage(new PrintStream(baos, true), CommandLine.Help.defaultColorScheme(CommandLine.Help.Ansi.ON));
        }
        return result;
    }


}
