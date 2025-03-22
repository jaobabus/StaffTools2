package fun.jaobabus.stafftolls.arguments.restrictions;

import fun.jaobabus.commandlib.argument.AbstractArgumentRestriction;
import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.restrictions.AbstractRestrictionFactory;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;

import java.util.List;


public class FloatRange extends AbstractRestrictionFactory.Parametrized<Double, FloatRange.Arguments>
{
    @Override
    public String getName() {
        return "FloatRange";
    }

    @Override
    public AbstractArgumentRestriction<Double> execute(FloatRange.Arguments input, String path) {
        return new AbstractArgumentRestriction.Parametrized<>() {
            @Override
            public String getName() {
                return "FloatRange";
            }

            @Override
            public String getPath() {
                return path;
            }

            @Override
            public boolean checkRestriction(Double value, AbstractExecutionContext context) {
                return input.start <= value && value <= input.end;
            }

            @Override
            public String formatRestriction(Double value, AbstractExecutionContext context) {
                return String.format("%f <= %f <= %f", input.start, value, input.end);
            }

            @Override
            public void processTabComplete(String source, List<Double> complete, AbstractExecutionContext context) {}
        };
    }

    public static class Arguments {
        @Argument
        public Double start;

        @Argument
        public Double end;
    }

}
