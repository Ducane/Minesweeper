package de.ducane.minesweeper;

import static de.androbin.gfx.util.GraphicsUtil.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import de.androbin.gfx.util.*;

public final class GamePanel extends JPanel {
  private Logic logic;
  
  private Point pressed;
  private Point lBomb;
  
  private final boolean[][] opened;
  private final boolean[][] flagged;
  
  private State state = State.ROBIN;
  
  private BufferedImage[] numbers;
  private BufferedImage pressedButton;
  private BufferedImage button;
  private BufferedImage bomb;
  private BufferedImage lostBomb;
  private BufferedImage flag;
  
  public GamePanel( final Logic logic ) {
    this.logic = logic;
    
    this.opened = new boolean[ logic.getHeight() ][ logic.getWidth() ];
    this.flagged = new boolean[ logic.getHeight() ][ logic.getWidth() ];
    
    loadImages();
    
    addMouseListener( new MouseAdapter() {
      @ Override
      public void mousePressed( final MouseEvent event ) {
        final int x = event.getX() * logic.getWidth() / getWidth();
        final int y = event.getY() * logic.getHeight() / getHeight();
        
        if ( event.getButton() == MouseEvent.BUTTON1 && state == State.ROBIN
            && logic.inBounds( x, y ) ) {
          pressed = new Point( x, y );
        }
        
        repaint();
      }
      
      @ Override
      public void mouseReleased( final MouseEvent event ) {
        pressed = null;
        
        final int x = event.getX() * logic.getWidth() / getWidth();
        final int y = event.getY() * logic.getHeight() / getHeight();
        
        if ( state == State.ROBIN && !opened[ y ][ x ] ) {
          if ( event.getButton() == MouseEvent.BUTTON1 ) {
            if ( !flagged[ y ][ x ] ) {
              if ( logic.isMine( x, y ) ) {
                lBomb = new Point( x, y );
                openMines();
                state = State.LOOSE;
              }
              
              if ( !flagged[ y ][ x ] ) {
                openAdjacent( x, y );
              }
              
              if ( allOpened() ) {
                flagMines();
                state = State.WIN;
              }
            }
          } else {
            flagged[ y ][ x ] ^= true;
          }
        }
        
        repaint();
      }
    } );
    
    addMouseMotionListener( new MouseMotionAdapter() {
      @ Override
      public void mouseDragged( final MouseEvent event ) {
        final int x = event.getX() * logic.getWidth() / getWidth();
        final int y = event.getY() * logic.getHeight() / getHeight();
        
        if ( pressed != null && state == State.ROBIN && logic.inBounds( x, y ) ) {
          pressed = new Point( x, y );
        }
        
        repaint();
      }
    } );
  }
  
  private void loadImages() {
    pressedButton = ImageUtil.loadImage( "pressedbutton.png" );
    bomb = ImageUtil.loadImage( "bomb.png" );
    lostBomb = ImageUtil.loadImage( "lostbomb.png" );
    button = ImageUtil.loadImage( "button.png" );
    flag = ImageUtil.loadImage( "flag.png" );
    
    numbers = new BufferedImage[ 8 ];
    for ( int i = 0; i < numbers.length; i++ ) {
      numbers[ i ] = ImageUtil.loadImage( ( i + 1 ) + ".png" );
    }
  }
  
  @ Override
  protected void paintComponent( final Graphics g ) {
    final Graphics2D g2d = (Graphics2D) g;
    g2d.setFont( new Font( "Calibri", 0, 20 ) );
    FontMetrics fm = g2d.getFontMetrics();
    
    final float rectWidth = (float) getWidth() / logic.getWidth();
    final float rectHeight = (float) getHeight() / logic.getHeight();
    
    for ( int y = 0; y < logic.getHeight(); y++ ) {
      for ( int x = 0; x < logic.getWidth(); x++ ) {
        if ( opened[ y ][ x ] ) {
          final int count = logic.getAdjacentMineCount( x, y );
          drawImage( g2d, pressedButton, rectWidth * x, rectHeight * y,
              rectWidth, rectHeight );
          
          if ( count > 0 ) {
            drawImage( g2d, numbers[ count - 1 ], rectWidth * x, rectHeight * y,
                rectWidth, rectHeight );
          }
          
          if ( logic.isMine( x, y ) ) {
            drawImage( g2d, bomb, rectWidth * x, rectHeight * y,
                rectWidth, rectHeight );
          }
        } else {
          drawImage( g2d, button, rectWidth * x, rectHeight * y,
              rectWidth, rectHeight );
          
          if ( flagged[ y ][ x ] ) {
            drawImage( g2d, flag, rectWidth * x, rectHeight * y,
                rectWidth, rectHeight );
          }
        }
      }
    }
    
    if ( pressed != null && !opened[ pressed.y ][ pressed.x ]
        && !flagged[ pressed.y ][ pressed.x ] ) {
      drawImage( g2d, pressedButton, rectWidth * pressed.x,
          rectHeight * pressed.y,
          rectWidth, rectHeight );
    }
    
    if ( state == State.LOOSE && lBomb != null ) {
      drawImage( g2d, lostBomb, rectWidth * lBomb.x,
          rectHeight * lBomb.y,
          rectWidth, rectHeight );
    }
    
    if ( state != State.ROBIN ) {
      g2d.setColor( new Color( 0f, 0f, 0f, 0.5f ) );
      fillRect( g2d, 0, 0, getWidth(), getHeight() );
      
      g2d.setColor( Color.WHITE );
      g2d.setFont( new Font( "Calibri", 1, 50 ) );
      fm = g2d.getFontMetrics();
      
      if ( state == State.WIN ) {
        g2d.drawString( "Gewonnen!", ( getWidth() - fm.stringWidth( "Gewonnen!" ) ) / 2,
            ( getHeight() - fm.getHeight() ) / 2 + fm.getAscent() );
      } else {
        g2d.drawString( "Verloren...", ( getWidth() - fm.stringWidth( "Verloren..." ) ) / 2,
            ( getHeight() - fm.getHeight() ) / 2 );
      }
    }
  }
  
  private void openAdjacent( final int x, final int y ) {
    if ( !logic.inBounds( x, y ) || opened[ y ][ x ] ) {
      return;
    }
    
    opened[ y ][ x ] = true;
    
    if ( logic.getAdjacentMineCount( x, y ) == 0 ) {
      openAdjacent( x + 1, y );
      openAdjacent( x - 1, y );
      openAdjacent( x, y + 1 );
      openAdjacent( x, y - 1 );  
      openAdjacent( x + 1, y + 1 );
      openAdjacent( x + 1, y - 1 );
      openAdjacent( x - 1, y + 1 );
      openAdjacent( x - 1, y - 1 );
    }
  }
  
  private void openMines() {
    for ( int y = 0; y < opened.length; y++ ) {
      for ( int x = 0; x < opened[ y ].length; x++ ) {
        if ( logic.isMine( x, y ) ) {
          opened[ y ][ x ] = true;
        }
      }
    }
  }
  
  private void flagMines() {
    for ( int y = 0; y < opened.length; y++ ) {
      for ( int x = 0; x < opened[ y ].length; x++ ) {
        if ( logic.isMine( x, y ) ) {
          flagged[ y ][ x ] = true;
        }
      }
    }
  }
  
  private boolean allOpened() {
    for ( int y = 0; y < opened.length; y++ ) {
      for ( int x = 0; x < opened[ y ].length; x++ ) {
        if ( !logic.isMine( x, y ) && !opened[ y ][ x ] ) {
          return false;
        }
      }
    }
    
    return true;
  }
}