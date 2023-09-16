package lanat.argumentTypes;

import lanat.ArgumentType;
import lanat.utils.Range;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An argument type that takes multiple strings.
 */
public class MultipleStringsArgumentType extends ArgumentType<String[]> {
	@Override
	public @NotNull Range getRequiredArgValueCount() {
		return Range.AT_LEAST_ONE;
	}

	@Override
	public @NotNull String[] parseValues(@NotNull String @NotNull [] args) {
		return args;
	}

	@Override
	public @Nullable String getDescription() {
		return "Accepts multiple strings.";
	}
}