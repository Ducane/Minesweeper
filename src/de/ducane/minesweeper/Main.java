package de.ducane.minesweeper;

import javax.swing.*;

public final class Main {
  private Main() {
  }
  
  public static void main( final String[] args ) {
    final JFrame window = new JFrame( "Minesweeper" );
    window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    window.setSize( 600, 600 );
    window.setLocationRelativeTo( null );
    
    final Logic logic = new Logic( 40, 20, 20 );
    window.setContentPane( new GamePanel( logic ) );
    
    window.setVisible( true );
  }
}