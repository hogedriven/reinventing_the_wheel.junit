package net.hogedriven.reinventing_the_wheel.junit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

public class HogeAssert {

	public static <T> void assertThat(T actual, Matcher<T> matcher) {
		assertThat("", actual, matcher);
	}

	public static <T> void assertThat(String reason, T actual, Matcher<T> matcher) {
		if (!matcher.matches(actual)) {
			Description description = new StringDescription();
			description.appendText(reason);
			description.appendText("\nExpected: ");
			description.appendDescriptionOf(matcher);
			description.appendText("\n     got: ");
			appendActualDescription(actual, matcher, description);
			description.appendText("\n");
			throw new java.lang.AssertionError(description.toString());
		}
	}

	/**
	 * actualをdescriptionに突っ込みます
	 * @param actual
	 * @param matcher
	 * @param description
	 * @throws AssertionError
	 */
	private static <T> void appendActualDescription(T actual, Matcher<T> matcher, Description description)
			throws AssertionError {
		try {
			// describeMismatchメソッドがあったらそれ
			Method describeMismatch = getDescribeMismatchMethod(matcher);
			if (describeMismatch != null) {
				describeMismatch.invoke(matcher, actual, description);
				return;
			}

			// 中にMatcherが入ってたら再帰
			Matcher<T> innerMatcher = getInnerMatcher(matcher);
			if (innerMatcher != null) {
				appendActualDescription(actual, innerMatcher, description);
				return;
			}

			// 何も無かったら今まで通り
			description.appendValue(actual);
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw new AssertionError(e);
		}
	}

	/**
	 * 内部にMatcherを持ってたら引っ張りだす。
	 * @param matcher 中にMatcherがありそうなMatcher
	 * @return 中のMatcher
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	private static <T> Matcher<T> getInnerMatcher(Matcher<T> matcher) throws IllegalAccessException {
		for (Field f : matcher.getClass().getDeclaredFields()) {
			if (f.getType().isAssignableFrom(Matcher.class)) {
				f.setAccessible(true);
				return (Matcher<T>) f.get(matcher);
			}
		}
		return null;
	}

	/**
	 * DescribeMismatchメソッドがあったら引っ張りだす。
	 * @param matcher DescribeMismatchがありそうなMatcher
	 * @return DescribeMismatchメソッド
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private static <T> Method getDescribeMismatchMethod(Matcher<T> matcher) throws IllegalArgumentException,
			IllegalAccessException {
		for (Method m : matcher.getClass().getMethods()) {
			if (m.isAnnotationPresent(DescribeMismatch.class)) {
				return m;
			}
		}
		return null;
	}
}
