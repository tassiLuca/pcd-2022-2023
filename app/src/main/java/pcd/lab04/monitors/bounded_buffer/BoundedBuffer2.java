package pcd.lab04.monitors.bounded_buffer;

import java.util.LinkedList;
import java.util.concurrent.locks.*;

public class BoundedBuffer2<Item> implements IBoundedBuffer<Item> {

	private final LinkedList<Item> buffer;
	private final int maxSize;
	private final Lock mutex;
	private final Condition notEmpty;
	private final Condition notFull;

	public BoundedBuffer2(int size) {
		buffer = new LinkedList<Item>();
		maxSize = size;
		mutex = new ReentrantLock();
		notEmpty = mutex.newCondition();
		notFull = mutex.newCondition();
	}

	public void put(Item item) throws InterruptedException {
		try {
			mutex.lock();
			if (isFull()) {
				notFull.await();
			}
			buffer.addLast(item);
			notEmpty.signal();
		} finally {
			mutex.unlock();
		}
	}

	public Item get() throws InterruptedException {
		try {
			mutex.lock();
			if (isEmpty()) {
				notEmpty.await();
			}
			Item item = buffer.removeFirst();
			notFull.signal();
			return item;
		} finally {
			mutex.unlock();
		}
	}

	private boolean isFull() {
		return buffer.size() == maxSize;
	}

	private boolean isEmpty() {
		return buffer.size() == 0;
	}
}
