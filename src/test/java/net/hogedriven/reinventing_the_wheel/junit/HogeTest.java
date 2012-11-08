package net.hogedriven.reinventing_the_wheel.junit;

import static net.hogedriven.reinventing_the_wheel.junit.HogeAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.hogedriven.reinventing_the_wheel.junit.DescribeMismatch;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;

public class HogeTest {

	@Test
	public void testSuccess() throws Exception {
		List<?> actual = Arrays.asList("H", "O", "G", "E");
		assertThat(actual, is(size(4)));
	}

	@Test
	public void testFail() throws Exception {
		List<?> actual = Arrays.asList("H", "O", "G", "E");
		assertThat(actual, is(size(3)));
	}

	private Matcher<Collection<?>> size(final int size) {
		return new TypeSafeMatcher<Collection<?>>() {
			@Override
			public void describeTo(Description description) {
				description.appendText("number of elements is " + size);
			}

			@Override
			public boolean matchesSafely(Collection<?> actual) {
				return actual.size() == size;
			}

			@DescribeMismatch
			public void describeMismatch(Collection<?> actual, Description description) {
				description.appendText("number of elements was " + actual.size());
			}
		};
	}
}
