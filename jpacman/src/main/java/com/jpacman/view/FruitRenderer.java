package com.jpacman.view;

import com.jpacman.model.Fruit;
import com.jpacman.view.graphics.Screen;
import com.jpacman.view.graphics.Sprite;
import com.jpacman.view.graphics.SpriteSheet;

public class FruitRenderer implements Renderer
{
    // fruit/"bonus symbol" sprites
    public static final Sprite cherries = new Sprite(SpriteSheet.icons, 28, 4, 9);
    public static final Sprite strawberry = new Sprite(SpriteSheet.icons, 28, 5, 9);
    public static final Sprite banana = new Sprite(SpriteSheet.icons, 28, 6, 9);
    public static final Sprite apple = new Sprite(SpriteSheet.icons, 28, 7, 9);
    public static final Sprite watermelon = new Sprite(SpriteSheet.icons, 28, 8, 9);
    public static final Sprite carrot = new Sprite(SpriteSheet.icons, 28, 9, 9);
    public static final Sprite bananas = new Sprite(SpriteSheet.icons, 28, 10, 9);

    private final Fruit fruit;
    private final int offset;

    public FruitRenderer(Fruit fruit, int offset)
    {
	this.fruit = fruit;
	this.offset = offset;
    }

    @Override
    public void render(Screen screen)
    {
	if (fruit.getAppeared()) {
	    screen.renderGameObject(fruit.getSprite(), fruit.getPosition().x, fruit.getPosition().y, offset, offset);
	}
    }
}
