package fun.jaobabus.stafftolls.arguments.restrictions;

import fun.jaobabus.commandlib.argument.AbstractArgumentRestriction;
import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.restrictions.AbstractRestrictionFactory;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;

import java.util.List;



public class IntRange extends AbstractRestrictionFactory.Parametrized<Long, IntRange.Arguments>
{
    @Override
    public String getName() {
        return "IntRange";
    }

    @Override
    public AbstractArgumentRestriction<Long> execute(IntRange.Arguments input) {
        return new AbstractArgumentRestriction.Parametrized<>() {
            @Override
            public String getName() {
                return "IntRange";
            }

            @Override
            public boolean checkRestriction(Long value, AbstractExecutionContext context) {
                return input.start <= value && value <= input.end;
            }

            @Override
            public String formatRestriction(Long value, AbstractExecutionContext context) {
                return String.format("%d <= %d <= %d", input.start, value, input.end);
            }

            @Override
            public void processTabComplete(String source, List<Long> complete, AbstractExecutionContext context) {}
        };
    }

    public static class Arguments {
        @Argument
        public Long start;

        @Argument
        public Long end;
    }

}
