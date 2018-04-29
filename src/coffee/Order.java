package coffee;

import java.util.List;

public class Order {
	public final List<Food> orderList;
	public final int orderNum;
	
	public Order(List<Food> orderList, int orderNum) {
		this.orderList = orderList;
		this.orderNum = orderNum;
	}
}
