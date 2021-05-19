package net.tassia.hardcore.util;

/**
 * A version of {@link java.util.function.Function} that allows the throwing of exceptions.
 *
 * @param <A> the input type
 * @param <B> the output type
 * @param <E> the exception
 *
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface ThrowingFunction<A, B, E extends Throwable> {

	/**
	 * Invokes the function.
	 *
	 * @param input the input value
	 * @return the output value
	 * @throws E on error
	 */
	B invoke(A input) throws E;

}
