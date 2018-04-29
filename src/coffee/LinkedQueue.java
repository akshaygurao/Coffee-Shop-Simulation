package coffee;

import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("hiding")
public class LinkedQueue <Order> {

	private static class QNode <Order> {
		@SuppressWarnings("unused")
		final Order order;
		final AtomicReference<QNode<Order>> next;
		public QNode(Order order, QNode<Order> next) {
			this.order = order;
			this.next = new AtomicReference<QNode<Order>>(next);
		}
	}
	private final QNode<Order> dummy = new QNode<Order>(null, null);
	private final AtomicReference<QNode<Order>> head
	= new AtomicReference<QNode<Order>>(dummy);
	private final AtomicReference<QNode<Order>> tail
	= new AtomicReference<QNode<Order>>(dummy);

	public boolean put(Order order) {
		QNode<Order> newNode = new QNode<Order>(order, null);
		while (true) {
			QNode<Order> curTail = tail.get();
			QNode<Order> tailNext = curTail.next.get();
			if (curTail == tail.get()) {
				if (tailNext != null) {
					// Queue in intermediate state, advance tail
					tail.compareAndSet(curTail, tailNext);
				} else {
					// In quiescent state, try inserting new node
					if (curTail.next.compareAndSet(null, newNode)) {
						// Insertion succeeded, try advancing tail
						tail.compareAndSet(curTail, newNode);
						return true;
					}
				}
			}
		}
	}	
	@SuppressWarnings("unchecked")
	public final Order removeOrder() {
		return (Order)head;
	}
}

