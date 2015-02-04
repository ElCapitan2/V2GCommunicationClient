/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package v2gCommunication.client;

/**
 * Custom Exception 
 * 
 * Exception is thrown, when startSession in ServerConnection is called more 
 * than once.
 * 
 * @author Alexander Forell
 */
public class ConnectionAlreadyEstablishedException extends Exception{
    //Parameterless Constructor
      public ConnectionAlreadyEstablishedException() {}

      //Constructor that accepts a message
      public ConnectionAlreadyEstablishedException(String message)
      {
         super(message);
      }
}
