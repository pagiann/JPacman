package jpacman.controller;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import jpacman.model.Maze;
import jpacman.model.Pill;
import jpacman.model.PowerPill;
import jpacman.view.PillRenderer;
import jpacman.view.graphics.Screen;
import jpacman.view.graphics.Sprite;

public class PillController implements Controller
{
    private ArrayList<Pill> pills;
    private ArrayList<PillRenderer> pillsRenderers;
    private ArrayList<Point> pillsPositions;

    private int totalPills;
    private int pillsEaten;
    private int pillsRemaining;

    @Override
    public void update(double delta)
    {
	pillsRemaining = totalPills - pillsEaten;

	for (Pill pill : pills) {
	    if (pill instanceof PowerPill) {
		if (pill.getAnimationCounter() < 4000) {
		    pill.increaseAnimationCounterByOne();
		} else {
		    pill.setAnimationCounter(0);
		}
	    }
	}
    }

    public void renderPills(Screen screen)
    {
	Iterator<PillRenderer> it = pillsRenderers.iterator();
	while (it.hasNext()) {
	    it.next().render(screen);
	}
    }

    public void createPills(ArrayList<Pill> pills)
    {
	this.pills = pills;
	totalPills = pills.size();
	createPillRenderers();
    }

    private void createPillRenderers()
    {
	pillsRenderers = new ArrayList<PillRenderer>(pills.size());
	Iterator<Pill> it = pills.iterator();
	while (it.hasNext()) {
	    pillsRenderers.add(new PillRenderer(it.next(), Sprite.SPRITE_SIZE - Maze.TILE + Pill.SIZE / 2));
	}
    }

    public void addPill(Pill pill)
    {
	pills.add(pill);
	pillsRenderers.add(new PillRenderer(pill, Sprite.SPRITE_SIZE - Maze.TILE + Pill.SIZE / 2));
    }

    public void removePill(int index)
    {
	pills.remove(index);
	pillsRenderers.remove(index);
    }

    public ArrayList<Pill> getPills()
    {
	return pills;
    }

    public ArrayList<Point> getPillsPositions()
    {
	pillsPositions = new ArrayList<Point>();
	for (Pill pill : pills) {
	    pillsPositions.add(pill.getPosition());
	}

	return pillsPositions;
    }

    public int getPillsEaten()
    {
	return pillsEaten;
    }

    public void setPillsEaten(int pillsEaten)
    {
	this.pillsEaten = pillsEaten;
    }

    public void increasePillsEatenByOne()
    {
	pillsEaten++;
    }

    public int getPillsRemaining()
    {
	return pillsRemaining;
    }

    public void setPillsRemaining(int pillsRemaining)
    {
	this.pillsRemaining = pillsRemaining;
    }
}
