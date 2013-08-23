/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.eppleton.fx2d.samples.towerdefense;

import de.eppleton.fx2d.GameCanvas;
import de.eppleton.fx2d.IntegerProperty;
import de.eppleton.fx2d.Sprite;
import de.eppleton.fx2d.Renderer;
import de.eppleton.fx2d.action.SpriteBehavior;
import de.eppleton.fx2d.tileengine.TileMapReader;
import de.eppleton.fx2d.tileengine.TileSet;
import de.eppleton.fx2d.tileengine.action.AnimationEvent;
import de.eppleton.fx2d.tileengine.action.AnimationEventHandler;
import de.eppleton.fx2d.tileengine.action.TileSetAnimation;
import de.eppleton.fx2d.tileengine.algorithms.AStar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Rectangle2D;
import javax.xml.bind.JAXBException;
import org.openide.util.Lookup;

/**
 *
 * @author antonepple
 */
public class EnemySprite extends Sprite {

    private double maxHealth = 50;
    private int killPoints = 50;
    private IntegerProperty score;
    private IntegerProperty hits;
    private boolean reachedTarget = false; 
    static TileSet explosion;

    static {
        try {
            explosion = TileMapReader.readSet("/de/eppleton/fx2d/towerdefense/explosion.tsx");
        } catch (JAXBException ex) {
            Logger.getLogger(EnemySprite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private TileSetAnimation explosionAnimation;

    public EnemySprite(GameCanvas parent, IntegerProperty score,IntegerProperty hits, Properties properties, Renderer animation, String name, double x, double y, final int width, final int height) {
        super(parent, animation, name, x, y, width, height, Lookup.EMPTY);
        this.score = score;
        this.hits = hits;
        setCollisionBox(new Rectangle2D(10, 10, 26, 26));
        setEnergy(maxHealth);
        explosionAnimation = new TileSetAnimation(explosion, 100f);
        explosionAnimation.setRepeat(1);
        explosionAnimation.setOnFinished(new AnimationEventHandler() {
            @Override
            public void handleEvent(AnimationEvent event) {
                Sprite target = event.getTarget();
                target.getParent().removeSprite(target);
                getParent().removeSprite(EnemySprite.this);
            }
        });


    }

    public void setAttackPath(final  AStar.PathNode attackPath) {
        addBehaviour(new SpriteBehavior() {
            AStar.PathNode start = attackPath;

            @Override
            public boolean perform(Sprite sprite) {
                double x = sprite.getX();
                double y = sprite.getY();
                double pathX = start.getX() * getWidth();
                double pathY = start.getY() * getHeight();

                if (Math.abs(pathX - x) < 2 && Math.abs(pathY - y) < 2) {
                    start = start.getParent();
                    if (start == null) {
                        reachedTarget = true;
                        sprite.die();
                        return false;
                    }
                }
                if (pathX - x > 1) {
                    sprite.setVelocityX(.5);
                } else if (pathX - x < -1) {
                    sprite.setVelocityX(-.5);
                } else {
                    sprite.setVelocityX(0);
                }
                if (pathY - y > 1) {
                    sprite.setVelocityY(.5);
                } else if (pathY - y < -1) {
                    sprite.setVelocityY(-.5);
                } else {
                    sprite.setVelocityY(0);
                }
                setRotation(Math.toDegrees(Math.atan2(sprite.getVelocityY(), sprite.getVelocityX())));

                return true;
            }
        });

    }

    @Override
    public void die() {
        super.die(); //To change body of generated methods, choose Tools | Templates.
        getParent().addSprite(new Sprite(getParent(), explosionAnimation, "explosion", getX() - 30, getY() - 80, 128, 128, Lookup.EMPTY));
        if (! reachedTarget)score.set(score.integerValue() + killPoints);
        else hits.set(hits.integerValue()+1);
    }

    public double getMaxHealth() {
        return maxHealth;
    }
}
