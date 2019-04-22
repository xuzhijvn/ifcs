package ims;

/**
 * @author xuzhijun.online  
 * @date 2019年4月21日
 */
public class Test {

	public static void main(String[] args) {
		new Thread(new Task()).start();

	}
	

}
class Task implements Runnable{

	@Override
	public void run() {
		try {
			while(true) {
				test();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	void test() throws Exception{
		for (int i = 0; i < 10; i++) {
			if(i == 5) {
				throw new Exception("xuzhijun test");
			}
			System.out.println(i);
		}
	}
	
}