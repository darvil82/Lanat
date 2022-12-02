import argparser.*;
import argparser.argumentTypes.KeyValuesArgument;
import argparser.utils.ErrorLevel;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.HashMap;

public class SimpleTests {
	public static void main(String[] args) {

		final var argumentParser = new ArgumentParser("Testing") {{
			addArgument(new Argument<>("what", new StringJoiner())
				.onOk(t -> System.out.println("wow look a string: '" + t + "'"))
				.positional()
				.obligatory()
			);
		}};

//		var pArgs = argumentParser.parseArgs("-fff --test hii subcommand --nose <x.1 y.347 z.43423> another --test 'this is a test' what");
		final var pArgs = argumentParser.parseArgs("--what hello world");

		System.out.println(pArgs.<String>get("what").get());
	}
}
