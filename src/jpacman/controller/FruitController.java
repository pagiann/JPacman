package jpacman.controller;

import java.util.Random;

import jpacman.model.Fruit;
import jpacman.util.Timer;
import jpacman.view.FruitRenderer;
import jpacman.view.graphics.Screen;

public class FruitController implements Controller
{
    private long timer;
    private boolean timerSet = false;
    private double amountOfTimeFruitMustStayBeforeDisappears;

    private boolean fruitMustDisappear = false;
    private Random randomTime;

    private Fruit fruit;
    private FruitRenderer fruitRenderer;

    public FruitController(Fruit fruit, FruitRenderer fruitRenderer)
    {
	this.fruit = fruit;
	this.fruitRenderer = fruitRenderer;
	randomTime = new Random(System.currentTimeMillis());
    }

    @Override
    public void update(double delta)
    {
	if (fruit.getAppeared()) {
	    countTimeElapsedSinceFruitAppeared();
	}
    }

    private void countTimeElapsedSinceFruitAppeared()
    {
	if (!timerSet) {
	    timer = System.currentTimeMillis();
	    amountOfTimeFruitMustStayBeforeDisappears = Timer.NINE_SECONDS + (randomTime.nextDouble() * Timer.ONE_SECOND);
	    timerSet = true;
	}

	if (System.currentTimeMillis() - timer >= amountOfTimeFruitMustStayBeforeDisappears) {
	    fruitMustDisappear = true;
	}
    }

    public void renderFruit(Screen screen)
    {
	fruitRenderer.render(screen);
    }

    public Fruit getFruit()
    {
	return fruit;
    }

    public void setFruit(Fruit fruit)
    {
	this.fruit = fruit;
    }

    public boolean isTimerSet()
    {
	return timerSet;
    }

    public void setTimerSet(boolean timerSet)
    {
	this.timerSet = timerSet;
    }

    public boolean getFruitMustDisappear()
    {
	return fruitMustDisappear;
    }

    public void setFruitMustDisappear(boolean fruitMustDisappear)
    {
	this.fruitMustDisappear = fruitMustDisappear;
    }
}
