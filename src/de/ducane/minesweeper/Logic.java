package de.ducane.minesweeper;

import java.util.*;

public class Logic {
  private static final Random RANDOM = new Random();
  
  private int mineCount;
  
  private boolean[][] mines;
  private int[][] adjacentMineCounts;
  
  public Logic( final int mineCount, final int width, final int height ) {
    mines = new boolean[ height ][ width ];
    adjacentMineCounts = new int[ height ][ width ];
    this.mineCount = mineCount;
    
    initMines( mineCount );
    initAdjacentMineCounts();
  }
  
  public int getAdjacentMineCount( final int x, final int y ) {
    return adjacentMineCounts[ y ][ x ];
  }
  
  public int getHeight() {
    return mines.length;
  }
  
  public int getWidth() {
    return mines[ 0 ].length;
  }
  
  public int getMineCount() {
    return mineCount;
  }
  
  public boolean inBounds( final int x, final int y ) {
    return x >= 0 && x < getWidth()
        && y >= 0 && y < getHeight();
  }
  
  private void incrementAdjacentMineCount( final int x, final int y ) {
    if ( inBounds( x, y ) && !isMine( x, y ) ) {
      adjacentMineCounts[ y ][ x ]++;
    }
  }
  
  private void initMines( final int mineCount ) {
    for ( int i = 0; i < mineCount; i++ ) {
      int x = RANDOM.nextInt( getWidth() );
      int y = RANDOM.nextInt( getHeight() );
      
      mines[ y ][ x ] = true;
    }
  }
  
  private void initAdjacentMineCounts() {
    for ( int y = 0; y < getHeight(); y++ ) {
      for ( int x = 0; x < getWidth(); x++ ) {
        if ( isMine( x, y ) ) {
          for ( int dy = -1; dy <= 1; dy++ ) {
            for ( int dx = -1; dx <= 1; dx++ ) {
              incrementAdjacentMineCount( x + dx, y + dy );
            }
          }
        }
      }
    }
  }
  
  public boolean isMine( final int x, final int y ) {
    return mines[ y ][ x ];
  }
}