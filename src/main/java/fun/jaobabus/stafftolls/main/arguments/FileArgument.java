package fun.jaobabus.stafftolls.main.arguments;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.context.DummyArgumentContext;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;

import java.io.File;
import java.util.List;

public class FileArgument extends AbstractArgument.Parametrized<File, DummyArgumentContext>
{
    public FileArgument() {}

    @Override
    public ParseMode getParseMode() {
        return ParseMode.SpaceTerminated;
    }

    @Override
    public String dumpSimple(File player, DummyArgumentContext executionContext) {
        return player.getName();
    }

    @Override
    public List<File> tapComplete(String s, DummyArgumentContext abstractExecutionContext) { return null; }

    @Override
    public File parseSimple(String s, DummyArgumentContext abstractExecutionContext) {
        return new File(s);
    }
}
