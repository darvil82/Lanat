package argparser.helpRepresentation;

import argparser.Argument;
import argparser.ArgumentGroup;
import argparser.Command;
import argparser.ParentCommandGetter;
import argparser.utils.displayFormatter.Color;
import argparser.utils.displayFormatter.FormatOption;
import argparser.utils.displayFormatter.TextFormatter;
import argparser.utils.UtlString;

import java.util.*;

public class HelpFormatter {
	Command parentCmd;
	private byte indentSize = 3;
	public static short lineWrapMax = 110;
	private ArrayList<LayoutItem> layout = new ArrayList<>();
	public static boolean debugLayout = false;

	public HelpFormatter(Command parentCmd) {
		this.parentCmd = parentCmd;
		this.setLayout();
	}

	// the user can create a helpFormatter, though, the parentCmd should be applied later (otherwise stuff will fail)
	public HelpFormatter() {
		this.setLayout();
	}

	public HelpFormatter(HelpFormatter other) {
		this.parentCmd = other.parentCmd;
		this.indentSize = other.indentSize;
		this.layout.addAll(other.layout);
	}

	public void setParentCmd(Command parentCmd) {
		this.parentCmd = parentCmd;
	}

	public void setIndentSize(int indentSize) {
		this.indentSize = (byte)Math.max(indentSize, 0);
	}

	public byte getIndentSize() {
		return indentSize;
	}

	protected List<LayoutItem> getLayout() {
		return layout;
	}

	public void setLayout() {
		this.changeLayout(
			new LayoutItem(LayoutGenerators::title),
			new LayoutItem(LayoutGenerators::synopsis).indent(1).margin(1),
			new LayoutItem((c) -> "Description:").marginTop(1),
			new LayoutItem(LayoutGenerators::argumentDescriptions).indent(1)
		);
	}

	public void moveLayoutItem(int from, int to) {
		if (from < 0 || from >= this.layout.size() || to < 0 || to >= this.layout.size()) {
			throw new IndexOutOfBoundsException("invalid indexes given");
		}

		// same index, nothing to do
		if (from == to)
			return;

		final var item = this.layout.remove(from);
		this.layout.add(to, item);
	}

	protected final void addToLayout(LayoutItem... layoutItems) {
		this.layout.addAll(Arrays.asList(layoutItems));
		Collections.addAll(this.layout, layoutItems);
	}

	protected final void addToLayout(int after, LayoutItem... layoutItems) {
		this.layout.addAll(after, Arrays.asList(layoutItems));
	}

	protected final void changeLayout(LayoutItem... layoutItems) {
		this.layout = new ArrayList<>(Arrays.asList(layoutItems));
	}


	@Override
	public String toString() {
		var buffer = new StringBuilder();

		for (int i = 0; i < this.layout.size(); i++) {
			var generator = this.layout.get(i);
			if (HelpFormatter.debugLayout)
				buffer.append(new TextFormatter("LayoutItem " + i + ":\n")
					.addFormat(FormatOption.UNDERLINE)
					.setColor(Color.GREEN)
				);
			buffer.append(UtlString.wrap(generator.generate(this), lineWrapMax)).append('\n');
		}

		return buffer.toString();
	}

	/**
	 * Indents a string by the indent size specified in the {@link HelpFormatter} of the specified {@link Command}.
	 * @param str the string to indent
	 * @param cmd the {@link Command} that has the {@link HelpFormatter}
	 * @return the indented string
	 */
	public static String indent(String str, Command cmd) {
		return UtlString.indent(str, cmd.getHelpFormatter().getIndentSize());
	}

	/**
	 * Indents a string by the indent size specified in the {@link HelpFormatter}
	 * instance of the {@link Command} this object belongs to.
	 * @param str the string to indent
	 * @param obj the object that belongs to the {@link Command} that has the {@link HelpFormatter}
	 * @return the indented string
	 */
	public static <T extends ParentCommandGetter> String indent(String str, T obj) {
		return HelpFormatter.indent(str, obj.getParentCommand());
	}
}
