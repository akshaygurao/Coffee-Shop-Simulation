package coffee;

/**
 * Cooks are simulation actors that have at least one field, a name. When
 * running, a cook attempts to retrieve outstanding orders placed by Eaters and
 * process them.
 */
public class Cook implements Runnable {
	private final String name;
	private final Machine fryer;
	private final Machine grill;
	private final Machine coffeeMaker2000;


	/**
	 * You can feel free modify this constructor. It must take at least the name,
	 * but may take other parameters if you would find adding them useful.
	 *
	 * @param: the
	 *             name of the cook
	 */

	public Cook(String name, Machine fryer, Machine grill, Machine coffeeMaker2000) {
		this.name = name;
		this.fryer = fryer;
		this.grill = grill;
		this.coffeeMaker2000 = coffeeMaker2000;
	}

	public String toString() {
		return name;
	}

	/**
	 * This method executes as follows. The cook tries to retrieve orders placed by
	 * Customers. For each order, a List<Food>, the cook submits each Food item in
	 * the List to an appropriate Machine, by calling makeFood(). Once all machines
	 * have produced the desired Food, the order is complete, and the Customer is
	 * notified. The cook can then go to process the next order. If during its
	 * execution the cook is interrupted (i.e., some other thread calls the
	 * interrupt() method on it, which could raise InterruptedException if the cook
	 * is blocking), then it terminates.
	 */

	// @Invariant: name of the cook
	// Precondition: cook thread is initialized
	// Postcondition: cook thread runs until interrupted
	// Exception: stops when interrupt method is called
	public void run() {

		Simulation.logEvent(SimulationEvent.cookStarting(this));
		try {
			while (true) {
				// YOUR CODE GOES HERE...
				if (Simulation.orderList.equals(null)) {
					Order order = Simulation.orderList.removeOrder();
					Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, order.orderList, order.orderNum));
					for (Food f : order.orderList) {
						switch (f.cookTimeMS) {
						case 600:
								Simulation.logEvent(
										SimulationEvent.cookStartedFood(this, FoodType.burger, order.orderNum));
								grill.makeFood();
								Simulation.logEvent(
										SimulationEvent.cookFinishedFood(this, FoodType.burger, order.orderNum));
							break;
						case 450:
								Simulation.logEvent(
										SimulationEvent.cookStartedFood(this, FoodType.fries, order.orderNum));
								fryer.makeFood();
								Simulation.logEvent(
										SimulationEvent.cookFinishedFood(this, FoodType.fries, order.orderNum));
							break;
						case 150:
								Simulation.logEvent(
										SimulationEvent.cookStartedFood(this, FoodType.coffee, order.orderNum));
								coffeeMaker2000.makeFood();
								Simulation.logEvent(
										SimulationEvent.cookFinishedFood(this, FoodType.coffee, order.orderNum));
							break;
						}
					}
					Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, order.orderNum));
					synchronized (Customer.lock) {
						Customer.lock.notify();
					}
					
				} 
				
			}
		} catch (InterruptedException e) {
			// This code assumes the provided code in the Simulation class
			// that interrupts each cook thread when all customers are done.
			// You might need to change this if you change how things are
			// done in the Simulation class.
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
	}
}