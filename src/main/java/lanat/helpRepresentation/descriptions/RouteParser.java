package lanat.helpRepresentation.descriptions;

import lanat.*;
import lanat.helpRepresentation.descriptions.exceptions.InvalidRouteException;
import lanat.utils.UtlReflection;
import lanat.utils.UtlString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

/** Parser for simple route syntax used in description tags (e.g. <code>args.myArg1.type</code>). */
public class RouteParser {
	/** The current object being handled in the route */
	private NamedWithDescription currentTarget;
	private final String[] route;
	private int index;

	private RouteParser(@NotNull NamedWithDescription user, @Nullable String route) {
		// if route is empty, the command the user belongs to is the target
		if (UtlString.isNullOrEmpty(route)) {
			this.currentTarget = RouteParser.getCommandOf(user);
			this.route = new String[0];
			return;
		}

		final String[] splitRoute = route.split("\\.");

		if (splitRoute[0].equals("!")) {
			this.currentTarget = user;
			this.route = Arrays.copyOfRange(splitRoute, 1, splitRoute.length);
			return;
		}

		this.currentTarget = RouteParser.getCommandOf(user);
		this.route = splitRoute;
	}

	/**
	 * Parses a route and returns the object it points to. If the route is empty or null, the command the user belongs to
	 * is returned.
	 * <p>
	 * The reason why the user is needed is because its likely that it will be needed to gather the Command it belongs to,
	 * and also if the route starts with <code>!</code>, the user itself becomes the initial target.
	 * </p>
	 * @param user the user that is requesting to parse the route
	 * @param route the route to parse
	 * @return the object the route points to
	 */
	public static NamedWithDescription parse(@NotNull NamedWithDescription user, @Nullable String route) {
		return new RouteParser(user, route).parse();
	}

	public static Command getCommandOf(NamedWithDescription obj) {
		if (obj instanceof Command cmd) {
			return cmd;
		} else if (obj instanceof CommandUser cmdUser) {
			return cmdUser.getParentCommand();
		} else {
			throw new InvalidRouteException("Cannot get the Command " + obj.getName() + " belongs to");
		}
	}

	private NamedWithDescription parse() {
		for (this.index = 0; this.index < this.route.length; this.index++) {
			final String token = this.route[this.index];

			if (token.equals("args") && this.currentTarget instanceof ArgumentAdder argsContainer) {
				this.setCurrentTarget(argsContainer.getArguments(), MultipleNamesAndDescription::hasName);
			} else if (token.equals("groups") && this.currentTarget instanceof ArgumentGroupAdder groupsContainer) {
				this.setCurrentTarget(groupsContainer.getSubGroups(), (g, name) -> g.getName().equals(name));
			} else if (token.equals("cmds") && this.currentTarget instanceof Command cmdsContainer) {
				this.setCurrentTarget(cmdsContainer.getSubCommands(), MultipleNamesAndDescription::hasName);
			} else if (token.equals("type") && this.currentTarget instanceof Argument<?, ?> arg) {
				this.currentTarget = arg.argType;
			} else {
				throw new InvalidRouteException(this.currentTarget, token);
			}
		}

		return this.currentTarget;
	}

	private <E extends NamedWithDescription>
	void setCurrentTarget(List<E> list, BiFunction<E, String, Boolean> predicate) {
		if (this.index + 1 >= this.route.length)
			throw new InvalidRouteException(this.currentTarget, "", "Expected a name");

		final var name = this.route[++this.index];
		final Optional<E> res = list.stream().filter(x -> predicate.apply(x, name)).findFirst();

		this.currentTarget = res.orElseThrow(() -> new RuntimeException(
			"Element " + name + " is not present in "
				+ UtlReflection.getSimpleName(this.currentTarget.getClass()) + ' ' + this.currentTarget.getName())
		);
	}
}
