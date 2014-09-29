/**
 * This file is part of FXGameEngine 
 * A Game Engine written in JavaFX
 * Copyright (C) 2012 Anton Epple <info@eppleton.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://opensource.org/licenses/GPL-2.0.
 * 
 * For alternative licensing or use in closed source projects contact Anton Epple 
 * <info@eppleton.de>
 */
package de.eppleton.fx2d.tilemapeditor;

import de.eppleton.fx2d.tileengine.TileSet;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import net.java.html.canvas.GraphicsContext2D;
import net.java.html.canvas.Style.Color;

class TileSetView extends StackPane {

    ImageView imageView;
    Canvas canvas;
    TileSet set;
    int selectedIndex = 0;
    Color selected = new Color("rgba(100, 100, 255, .005)");

    public TileSetView() {
    }

    public void setTileSet(final TileSet set) {
        this.set = set;
        getChildren().clear();
        imageView = new ImageView();
        getChildren().add(imageView);
        Image image = null;
        try{
            image = new Image(set.getTileImage().getSrc());
        
               
            } catch (IllegalArgumentException iax) {
                try {
                    image = new javafx.scene.image.Image(new FileInputStream(set.getTileImage().getSrc()));
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(TileSetView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        imageView.setImage(image);
        canvas = new Canvas(set.getTileImage().getWidth(), set.getTileImage().getWidth());
        getChildren().add(canvas);
        canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                double x = t.getX();
                double y = t.getY();
                selectedIndex = (int) ((int) x / set.getTilewidth() + (((int) y / set.getTileheight()) * set.getNumColumns()));
         
                updateCanvas();
            }
        });
        updateCanvas();
    }

    public int getSelectedGid() {
        if (set == null) {
            return -1;
        }
        return set.getFirstgid() + selectedIndex;
    }

    public void updateCanvas() {
        GraphicsContext2D graphicsContext2D = new GraphicsContext2D(new JavaFXGraphicsEnvironment(canvas));
        graphicsContext2D.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (int i = 0; i < set.getNumRows(); i++) {
            for (int j = 0; j < set.getNumColumns(); j++) {
                graphicsContext2D.setFillStyle(new Color("#ffffff"));
                graphicsContext2D.fillRect(j * set.getTilewidth(), 0, 1, set.getTileheight() * set.getNumRows());
                graphicsContext2D.fillRect(0, i * set.getTileheight(), set.getTilewidth() * set.getNumColumns(), 1);

                if (selectedIndex >= 0) {
                    graphicsContext2D.setFillStyle(selected);
                    int x = selectedIndex % set.getNumColumns();
                    int y = selectedIndex / set.getNumColumns();
                    graphicsContext2D.fillRect(x * set.getTilewidth(), y * set.getTileheight(), set.getTilewidth(), set.getTileheight());
                }
            }
        }
    }
}
