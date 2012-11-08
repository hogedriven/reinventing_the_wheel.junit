package net.hogedriven.reinventing_the_wheel.junit;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = { METHOD })
public @interface DescribeMismatch {
}
