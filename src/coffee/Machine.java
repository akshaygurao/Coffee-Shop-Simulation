package coffee;

import java.util.LinkedList;

/**
 * A Machine is used to make a particular Food.  Each Machine makes
 * just one kind of Food.  Each machine has a capacity: it can make
 * that many food items in parallel; if the machine is asked to
 * produce a food item beyond its capacity, the requester blocks.
 * Each food item takes at least item.cookTimeMS milliseconds to
 * produce.
 */
public class Machine {
	public final String machineName;
	public final Food machineFoodType;
	public final LinkedList<Integer> capacity;

	//YOUR CODE GOES HERE...


	/**
	 * The constructor takes at least the name of the machine,
	 * the Food item it makes, and its capacity.  You may extend
	 * it with other arguments, if you wish.  Notice that the
	 * constructor currently does nothing with the capacity; you
	 * must add code to make use of this field (and do whatever
	 * initialization etc. you need).
	 */
	public Machine(String nameIn, Food foodIn, int capacityIn) {
		this.machineName = nameIn;
		this.machineFoodType = foodIn;
		this.capacity = new LinkedList<Integer>();
		for(int i = 0; i < capacityIn; i++) {
			capacity.add(i);
		}
		//YOUR CODE GOES HERE...
		Simulation.logEvent(SimulationEvent.machineStarting(this, foodIn, capacityIn));
	}
	

	

	/**
	 * This method is called by a Cook in order to make the Machine's
	 * food item.  You can extend this method however you like, e.g.,
	 * you can have it take extra parameters or return something other
	 * than Object.  It should block if the machine is currently at full
	 * capacity.  If not, the method should return, so the Cook making
	 * the call can proceed.  You will need to implement some means to
	 * notify the calling Cook when the food item is finished.
	 */
	public void makeFood() throws InterruptedException {
		//YOUR CODE GOES HERE...
		
		if(capacity.size() != 0) {
			synchronized(this) {
				capacity.removeFirst();
				CookAnItem cookItem = new CookAnItem(this);
				cookItem.run();
				capacity.add(1);
				notify();
			}
		}
		else {
			synchronized (this) {
				wait();
			}
			
		}
	
	}

	//THIS MIGHT BE A USEFUL METHOD TO HAVE AND USE BUT IS JUST ONE IDEA
	
	//@Invariant: capacity of the machine
	//Precondition: check if capacity of machine is not full
	//PostCondition: wait for the time per item before continuing
	//Exception: if interrupt method called stop the process  
	private class CookAnItem implements Runnable {
		Machine machine;
		public CookAnItem(Machine machine) {
			this.machine = machine;
		}
		public void run() {
			try {
				//YOUR CODE GOES HERE...
				Simulation.logEvent(SimulationEvent.machineCookingFood(machine, machine.machineFoodType));
				Thread.sleep(machineFoodType.cookTimeMS);
				Simulation.logEvent(SimulationEvent.machineDoneFood(machine, machine.machineFoodType));
				capacity.addFirst(1);
			} catch(InterruptedException e) { 
				e.printStackTrace();
			}
		}
	}
 

	public String toString() {
		return machineName;
	}
}