package lanat.helpRepresentation.descriptions;

import lanat.CommandUser;
import lanat.NamedWithDescription;
import lanat.helpRepresentation.descriptions.exceptions.UnknownTagException;
import org.jetbrains.annotations.NotNull;

import java.util.Hashtable;

public abstract class Tag {
	private static final Hashtable<String, Tag> registeredTags = new Hashtable<>();
	private static boolean initializedTags;


	protected abstract <T extends CommandUser & NamedWithDescription>
	@NotNull String parse(@NotNull String value, @NotNull T user);


	public static void initTags() {
		if (Tag.initializedTags) return;

		Tag.registerTag("link", new LinkTag());
		Tag.registerTag("desc", new DescTag());

		Tag.initializedTags = true;
	}

	protected static <T extends Tag> void registerTag(@NotNull String name, @NotNull T tag) {
		Tag.registeredTags.put(name, tag);
	}

	static <T extends CommandUser & NamedWithDescription>
	@NotNull String parseTagValue(@NotNull String tagName, @NotNull String value, @NotNull T user) {
		var tag = Tag.registeredTags.get(tagName.toLowerCase());
		if (tag == null) throw new UnknownTagException(tagName);
		return tag.parse(value, user);
	}

}