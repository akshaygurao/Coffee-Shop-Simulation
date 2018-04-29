package coffee;

import java.util.LinkedList;
import java.util.List;

/**
 * Customers are simulation actors that have two fields: a name, and a list of
 * Food items that constitute the Customer's order. When running, an customer
 * attempts to enter the coffee shop (only successful if the coffee shop has a
 * free table), place its order, and then leave the coffee shop when the order
 * is complete.
 */
public class Customer implements Runnable {
	// JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
	private final String name;
	private final List<Food> order;
	private final int orderNum;
	public static final Object lock = new Object();
	public static Object lock1 = new Object();
	private static int runningCounter = 0;
	private static volatile LinkedList<Integer> tableList = Simulation.tableList;

	/**
	 * You can feel free modify this constructor. It must take at least the name and
	 * order but may take other parameters if you would find adding them useful.
	 */
	public Customer(String name, List<Food> order) {
		this.name = name;
		this.order = order;
		this.orderNum = ++runningCounter;
	}

	public String toString() {
		return name;
	}

	/**
	 * This method defines what an Customer does: The customer attempts to enter the
	 * coffee shop (only successful when the coffee shop has a free table), place
	 * its order, and then leave the coffee shop when the order is complete.
	 */
	// @Invariant: name, order list and ordernum of the customer
	// Precondition: if the table is empty then the order is processed
	// Postcondition: places the order of the items
	// Exception: if interrupt method is called then customer waiting to order will
	// be declined
	public synchronized void run() {
		// YOUR CODE GOES HERE...
		Simulation.logEvent(SimulationEvent.customerStarting(this));

		try {
			if (tableList.size() != 0) {
				synchronized (this) {
					tableList.remove();
					Simulation.logEvent(SimulationEvent.customerEnteredCoffeeShop(this));
					Order order = new Order(this.order, this.orderNum);
					Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, this.order, this.orderNum));
					Simulation.orderList.put(order);
					synchronized (lock) {
						lock.wait();
					}
					Simulation.logEvent(SimulationEvent.customerReceivedOrder(this, this.order, this.orderNum));
					Simulation.logEvent(SimulationEvent.customerLeavingCoffeeShop(this));
					tableList.addFirst(1);
					this.notifyAll();
					System.out.println("Order Number" + orderNum + "is Complete!");	
				}
			} else {
				synchronized (this) {
					tableList.add(1);
					System.out.println("Waiting for tables to empty");
					this.wait();
					
				}
			}
		} catch (InterruptedException e) {
			Simulation.logEvent(SimulationEvent.customerLeavingCoffeeShop(this));
		}

	}
}