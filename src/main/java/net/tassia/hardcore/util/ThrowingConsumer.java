package net.tassia.hardcore.util;

/**
 * A version of {@link java.util.function.Consumer} that allows the throwing of exceptions.
 *
 * @param <A> the input type
 * @param <E> the exception
 *
 * @see java.util.function.Consumer
 */
@FunctionalInterface
public interface ThrowingConsumer<A, E extends Throwable> {

	/**
	 * Invokes this function.
	 *
	 * @param input the input value
	 * @throws E on error
	 */
	void invoke(A input) throws E;

}
