package lanat.test;

import lanat.Argument;
import lanat.ArgumentType;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class TestArgumentTypes extends UnitTests {
	private enum TestEnum {
		ONE, TWO, THREE
	}

	@Override
	protected TestingParser setParser() {
		return new TestingParser("TestArgumentTypes") {{
			this.addArgument(Argument.create("boolean", ArgumentType.BOOLEAN()));
			this.addArgument(Argument.create(ArgumentType.COUNTER(), "counter", "c"));
			this.addArgument(Argument.create("integer", ArgumentType.INTEGER()));
			this.addArgument(Argument.create("float", ArgumentType.FLOAT()));
			this.addArgument(Argument.create("string", ArgumentType.STRING()));
			this.addArgument(Argument.create("multiple-strings", ArgumentType.STRINGS()));
			this.addArgument(Argument.create("file", ArgumentType.FILE()));
			this.addArgument(Argument.create("enum", ArgumentType.ENUM(TestEnum.TWO)));
			this.addArgument(Argument.create("key-value", ArgumentType.KEY_VALUES(ArgumentType.INTEGER())));
			this.addArgument(Argument.create("int-range", ArgumentType.INTEGER_RANGE(3, 10)));
			this.addArgument(Argument.create("try-parse", ArgumentType.TRY_PARSE(Double.class)));
		}};
	}

	@Test
	public void testBoolean() {
		assertEquals(Boolean.TRUE, this.parser.parseGetValues("--boolean").<Boolean>get("boolean").get());
		assertEquals(Boolean.FALSE, this.parser.parseGetValues("").<Boolean>get("boolean").get());
	}

	@Test
	public void testCounter() {
		assertEquals(0, this.parser.parseGetValues("").<Integer>get("counter").get());
		assertEquals(1, this.parser.parseGetValues("-c").<Integer>get("counter").get());
		assertEquals(4, this.parser.parseGetValues("-cccc").<Integer>get("counter").get());
	}

	@Test
	public void testInteger() {
		assertEquals(4, this.<Integer>parseArg("integer", "4"));
		this.assertNotPresent("integer");
		assertNull(this.parseArg("integer", "invalid"));
	}

	@Test
	public void testFloat() {
		assertEquals(4.67f, this.<Float>parseArg("float", "4.67"));
		this.assertNotPresent("float");
		assertNull(this.parseArg("float", "invalid"));
	}

	@Test
	public void testString() {
		assertEquals("hello", this.parseArg("string", "hello"));
		assertEquals("foo", this.parseArg("string", "foo bar"));
		assertEquals("foo bar", this.parseArg("string", "'foo bar'"));
		this.assertNotPresent("string");
	}

	@Test
	public void testStrings() {
		assertArrayEquals(new String[] { "hello" }, this.parseArg("multiple-strings", "hello"));
		assertArrayEquals(new String[] { "hello", "world" }, this.parseArg("multiple-strings", "hello world"));
		assertArrayEquals(new String[] { "hello world" }, this.parseArg("multiple-strings", "'hello world'"));
	}

	@Test
	public void testFile() {
		assertEquals("hello.txt", this.<File>parseArg("file", "hello.txt").getName());
		this.assertNotPresent("file");
	}

	@Test
	public void testEnum() {
		assertEquals(TestEnum.ONE, this.parseArg("enum", "ONE"));
		assertEquals(TestEnum.TWO, this.parseArg("enum", "TWO"));
		assertEquals(TestEnum.THREE, this.parseArg("enum", "THREE"));
		assertEquals(TestEnum.TWO, this.parser.parseGetValues("").get("enum").get());
	}

	@Test
	public void testKeyValue() {
		final var hashMap = new HashMap<String, Integer>() {{
			this.put("key", 6);
			this.put("foo", 2);
			this.put("foo2", 1996);
		}};

		assertEquals(hashMap, this.parseArg("key-value", "key=6 foo=2 foo2=1996"));
		this.assertNotPresent("key-value");
		assertNull(this.parseArg("key-value", "invalid"));
	}

	@Test
	public void testIntegerRange() {
		assertEquals(4, this.<Integer>parseArg("int-range", "4"));
		this.assertNotPresent("int-range");
		assertNull(this.parseArg("int-range", "invalid"));
		assertNull(this.parseArg("int-range", "11"));
	}

	@Test
	public void testTryParse() {
		assertEquals(4.67, this.<Double>parseArg("try-parse", "4.67"));
		this.assertNotPresent("try-parse");
		assertNull(this.parseArg("try-parse", "invalid"));
	}
}