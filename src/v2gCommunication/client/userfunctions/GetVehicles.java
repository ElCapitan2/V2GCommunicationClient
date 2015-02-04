/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package v2gCommunication.client.userfunctions;

import java.util.ArrayList;
import v2gCommunication.client.ServerConnection;
import v2gcommunication.commonclasses.requests.Request;
import v2gcommunication.commonclasses.requests.RequestType;
import v2gcommunication.commonclasses.tasks.ParameterSet;
import v2gcommunication.commonclasses.requests.AddRequest;
import v2gcommunication.commonclasses.tasks.StartTaskTransmit;
import v2gcommunication.commoninterfaces.TaskWorker;

/**
 * Client side implementation to build a request to get a list of vehicles 
 * registered on the server
 * 
 * Class implements Taskworker which extends Runnable.
 * 
 * The run method processes the answer from the server. 
 * 
 * @author Alexander Forell
 */
public class GetVehicles implements TaskWorker{
    private Request request;
    private AddRequest requestReply;
    
    /**
     * Run mehthod must be overridden as it is defined in TaskWorker.
     *
     * This method reads the request and puts out a list of users to the 
     * command line
     * 
     * It also calls the callback method {@code ServerConnection.setUserList}
     * 
     */
    @Override
    public void run() {
        if (request.getRequestType()==RequestType.ANSWER){
            ArrayList<ParameterSet> parameters = new ArrayList<ParameterSet>();
            ArrayList<String> vehicles = new ArrayList<String>();
            parameters.addAll(request.getParameterSet());
            System.out.println();
            System.out.println("***************************************************");
            System.out.println("ANSWER GET VEHCILES");
            System.out.println("===================");
            int i = 0;
            for (ParameterSet para:parameters){
                if (para.parameterName.equals("vehname") && para.parameterType.equals(String.class.getName())){
                    i++;
                    System.out.println("Vehicle " + i + ": " + para.parameterValue);
                    vehicles.add(para.parameterValue);
                }
            }
            System.out.println("***************************************************");
            System.out.println();
            ServerConnection sc = ServerConnection.getInstance();
            sc.setVehicleList(vehicles);
        }
    }

    /**
     * Method defined in Taskworker is used for a the runMethod to reply using
     * Requests or Tasks.
     * 
     * @param requestProcessed  The Request which is currently handled
     * @param requestReply      The Request used to reply
     * @param taskReply         The Task used to reply.
     */
    @Override
    public void inputForRunnable(Request requestProcessed, AddRequest requestReply, StartTaskTransmit taskReply) {
        this.request = requestProcessed;
        this.requestReply = requestReply;
    }
    
    /**
     * Static method to generate a get vehicles request. The retruned 
     * Request will be added can be added to a Session and will then 
     * be transmitted to the server
     * 
     * @param username          username of user logged in
     * @return                  Request
     */
    public static Request getGetVehiclesRequest(String username){
        ArrayList<ParameterSet> parameters = new ArrayList<ParameterSet>();

        Request createUserRequest = new Request(username, "GetVehicles", parameters);
        return createUserRequest;
    }
    
}
