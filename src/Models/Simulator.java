package Models;

import javafx.scene.canvas.Canvas;

import java.util.Random;


public class Simulator {

	private static final String AD_HOC = "1";
	private static final String PASS = "2";
	private static final String RES = "3";

	private CarQueue entranceCarQueue;
    private CarQueue entrancePassQueue;
    private CarQueue entranceResQueue;
    private CarQueue paymentCarQueue;
    private CarQueue exitCarQueue;
    private SimulatorView simulatorView;

    private int day = 0;
    private int hour = 0;
    private int minute = 0;

    //private int tickPause = 100;

    int weekDayArrivals= 100; // average number of arriving cars per hour
    int weekendArrivals = 200; // average number of arriving cars per hour
    int weekDayPassArrivals= 50; // average number of arriving cars per hour
    int weekendPassArrivals = 5; // average number of arriving cars per hour
    int weekDayResArrivals = 75;
    int weekendResArrivals = 30;

    int enterSpeed = 3; // number of cars that can enter per minute
    int paymentSpeed = 7; // number of cars that can pay per minute
    int exitSpeed = 5; // number of cars that can leave per minute

    /**
     * The constructor of the class Simulator, runs the main simulator by handeling arriving/leaving cars, keeps count of the time and Payments
     */
    public Simulator(Canvas canvas) {
        entranceCarQueue = new CarQueue();
        entrancePassQueue = new CarQueue();
        entranceResQueue = new CarQueue();
        paymentCarQueue = new CarQueue();
        exitCarQueue = new CarQueue();
        simulatorView = new SimulatorView(canvas, 3, 6, 30);
        updateViews();
    }


    /**
     * returns the current instance of simulatorView
     * @return the current instance of the class simulatorView
     */
    public SimulatorView getView() {
        return simulatorView;
    }

    /**
     * Ticks the simulation forward by calling other methods like advanceTime(), updateViews() and handles the entering and leaving of cars
     */
    public void tick() {
    	advanceTime();
    	handleExit();
    	updateViews();
    	// Pause.
    	handleEntrance();
    }

    /**
     * advances the time forward by 1 minute
     */
    private void advanceTime(){
        // Advance the time by one minute.
        minute++;
        while (minute > 59) {
            minute -= 60;
            hour++;
        }
        while (hour > 23) {
            hour -= 24;
            day++;
        }
        while (day > 6) {
            day -= 7;
        }
    }

    /**
     * takes cars from the carQueue and lets them enter the garage
     */
    private void handleEntrance(){
    	carsArriving();
    	carsEntering(entrancePassQueue,true, false);
    	carsEntering(entranceCarQueue,false, false);
    	carsEntering(entranceResQueue,false, true);
    }

    /**
     * Makes cars leave and pay the parking garage
     */
    private void handleExit(){
        carsReadyToLeave();
        carsPaying();
        carsLeaving();
    }

    /**
     * updates the simulator view
     */
    private void updateViews(){
    	simulatorView.tick();
        // Update the car park view.
        simulatorView.updateView();
    }

    /**
     * adds new cars to the carQueue's
     */
    private void carsArriving(){
    	int numberOfCars = getNumberOfCars(weekDayArrivals, weekendArrivals);
        addArrivingCars(numberOfCars, AD_HOC);    	
    	numberOfCars = getNumberOfCars(weekDayPassArrivals, weekendPassArrivals);
        addArrivingCars(numberOfCars, PASS);
        numberOfCars = getNumberOfCars(weekDayResArrivals, weekendResArrivals);
        addArrivingCars(numberOfCars, RES);
    }

    /**
     * removes a car from the carQueue and assigns it a parking space
     * @param queue the carQueue a car enters from
     */
    private void carsEntering(CarQueue queue, boolean passHolder, boolean hasReservation){
        int i = 0;
        // Remove car from the front of the queue and assign to a parking space.
    	while (queue.carsInQueue() > 0 && simulatorView.getNumberOfOpenSpots() > 0 && i < enterSpeed) {
    	    if(!passHolder) {
    	        if(!hasReservation) {
                    AdHocCar car = (AdHocCar) queue.removeCar();
                    Location freeLocation = simulatorView.getFirstFreeLocation();
                    simulatorView.setCarAt(freeLocation, car);
                }else {
    	            CarWithReservedSpot car = (CarWithReservedSpot) queue.removeCar();
    	            int entryCode = car.getEntryCode();
                    Location freeLocation = simulatorView.getFirstFreeLocation();
                    simulatorView.setCarAt(freeLocation, car);
                }
            } else if(passHolder) {
    	        ParkingPassCar car = (ParkingPassCar) queue.removeCar();
    	        Location freeLocation = simulatorView.getFirstPassSpot();
    	        if(freeLocation == null) {
                    freeLocation = simulatorView.getFirstFreeLocation();
                }
    	        simulatorView.setCarAt(freeLocation, car);
            }
			i++;
        }
    }

    /**
     * adds leaving cars to the payment queue
     */
    private void carsReadyToLeave(){
        // Add leaving cars to the payment queue.
        Car car = simulatorView.getFirstLeavingCar();
        while (car != null) {
        	if (car.getHasToPay()){
	            car.setIsPaying(true);
	            paymentCarQueue.addCar(car);
        	}
        	else {
        		carLeavesSpot(car, (car instanceof ParkingPassCar));
        	}
            car = simulatorView.getFirstLeavingCar();
        }
    }

    /**
     * Makes the cars pay
     */
    private void carsPaying(){
        // Let cars pay.
    	int i = 0;
    	while (paymentCarQueue.carsInQueue()>0 && i < paymentSpeed){
            Car car = paymentCarQueue.removeCar();
            // TODO Handle payment.
            carLeavesSpot(car, (car instanceof ParkingPassCar));
            i++;
    	}
    }

    /**
     * Makes the cars leave the parking garage
     */
    private void carsLeaving(){
        // Let cars leave.
    	int i = 0;
    	while (exitCarQueue.carsInQueue()>0 && i < exitSpeed){
            exitCarQueue.removeCar();
            i++;
    	}	
    }

    /**
     *
     * @param weekDay The number of cars on an weekDay
     * @param weekend The number of cars on a weekend
     * @return int the number of cars per hour on the current day
     */
    private int getNumberOfCars(int weekDay, int weekend){
        Random random = new Random();

        // Get the average number of cars that arrive per hour.
        int averageNumberOfCarsPerHour = day < 5 ? weekDay : weekend;

        // Calculate the number of cars that arrive this minute.
        double standardDeviation = averageNumberOfCarsPerHour * 0.3;
        double numberOfCarsPerHour = averageNumberOfCarsPerHour + random.nextGaussian() * standardDeviation;
        return (int)Math.round(numberOfCarsPerHour / 60);	
    }

    /**
     * Add cars to the arriving Queue's
     * @param numberOfCars numbers of cars to be added to the queue of arriving cars
     * @param type the type of the car
     */
    private void addArrivingCars(int numberOfCars, String type){
        // Add the cars to the back of the queue.
    	switch(type) {
            case AD_HOC:
                for (int i = 0; i < numberOfCars; i++) {
                    entranceCarQueue.addCar(new AdHocCar());
                }
                break;
            case PASS:
                for (int i = 0; i < numberOfCars; i++) {
                    entrancePassQueue.addCar(new ParkingPassCar());
                }
                break;
            case RES:
                for(int i = 0; i < numberOfCars; i++) {

                }
    	}
    }

    /**
     * Makes the car leaves the spot
     * @param car the car that has to leave the spot
     */
    private void carLeavesSpot(Car car, boolean hasParkingPass){
		Location location = car.getLocation();
    	simulatorView.removeCarAt(location);

    	/**
		 * Temporary fix, this should be changed as soon as the manager can decide where he/she wants the reserved
		 * spots to be
		 */
    	if (hasParkingPass && location.getFloor() == 0 && (location.getRow() == 0 || location.getRow() == 1))
    		simulatorView.setReservation(location, new Reservation());

        exitCarQueue.addCar(car);
    }

    public SimulatorView getSimulatorView() {
        return simulatorView;
    }

}
