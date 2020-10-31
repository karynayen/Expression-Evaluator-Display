import java.util.ArrayList;
import java.util.EmptyStackException;

// TODO: Auto-generated Javadoc
/**
 * The Class GenericStack. Implements a generic software stack for any element
 *
 *
 * @param <E> the element type
 */
public class GenericStack<E>  {
	
	/** The stack.  The stack will be built on a generic ArrayList, but will only
	 *  expose stack methods push, pop, peek, isEmpty and getSize.
	 */
	private ArrayList<E> stack;
	
	/**
	 * Instantiates a new generic stack. The stack is empty at the beginning
	 */
	public GenericStack() {
		stack = new ArrayList<>();
	}
	
	/**
	 * Gets the size of the stack. The Top of Stack is size - 1;
	 *
	 * @return the size
	 */
	public int getSize() {
		return stack.size();
	}

	/**
	 * Pushes an object onto the stack using the ArrayList add method. This also
	 * adjusts the size of the stack directly...
	 *
	 * @param o the object to be added to the stack
	 */
	public void push(E o) {
		stack.add(o);
	};
	
	/**
	 * Pops the object off of the top of the stack, and returns it. The ArrayList
	 * remove method is used to implement this.
	 *
	 * @return the object at the top of the stack
	 * @throws EmptyStackException if an attempt was made to pop on an empty stack
	 */
	public E pop() throws EmptyStackException {
		if (isEmpty()) {
			throw new EmptyStackException();
		} else {
			return (stack.remove(getSize() - 1));
		}
	}
	
	/**
	 * Peek - this is a Java stack function that returns the object at the
	 * top of the stack without removing it from the stack.
	 *
	 * @return the object at the top of the stack
	 * @throws EmptyStackException if an attempt was made to peek on an empty stack
	 */
	public E peek() throws EmptyStackException {
		if (isEmpty()) {
			throw new EmptyStackException();
		} else {
			return stack.get(getSize() - 1);
		}
	}
	
	/**
	 * Checks if the stack is empty.
	 *
	 * @return true, if the stack is empty
	 */
	public boolean isEmpty() {
		return (getSize() == 0);	
	}
	
	/**
	 * To string
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return("stack: "+stack.toString());
	}
}