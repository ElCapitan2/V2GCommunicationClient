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
 * Client side implementation to build a request to get type of stored data 
 * for a vehicle on the server.
 * 
 * Class implements Taskworker which extends Runnable.
 * 
 * The run method processes the answer from the server. 
 * 
 * @author Alexander Forell
 */
public class GetStoredFunctions implements TaskWorker{
    private Request request;
    private AddRequest requestReply;
    
    /**
     * Run method must be overridden as it is defined in TaskWorker.
     * 
     * Server will return a request including a list of functions stored 
     * on the server.
     * 
     * It also calls the callback method {@code ServerConnection.setVehicleFunctions}
     * 
     */
    @Override
    public void run() {
        if (request.getRequestType()==RequestType.ANSWER){
            ArrayList<ParameterSet> parameters = new ArrayList<ParameterSet>();
            parameters.addAll(request.getParameterSet());
            ArrayList<String> storedFunctions = new ArrayList<String>();
            System.out.println();
            System.out.println("***************************************************");
            System.out.println("ANSWER GET VEHCILES");
            System.out.println("===================");
            System.out.println("Functions stored in requested vehicles:");
            int i = 0;
            for (ParameterSet para:parameters){
                if (para.parameterName.equals("function") && para.parameterType.equals(String.class.getName())){
                    i++;
                    storedFunctions.add( para.parameterValue);
                    System.out.println("Function " + i + ": " + para.parameterValue);
                }
            }
            System.out.println("***************************************************");
            System.out.println();
            ServerConnection sc = ServerConnection.getInstance();
            sc.setVehicleFunctions(storedFunctions);
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
     * Static method to generate a request to get stored functions for a vehicle. 
     * The returned Request will be added can be added to a Session and will then 
     * be transmitted to the server 
     * 
     * @param username          username of the user logged in.
     * @param vehnames          vehiclenames of the vehicles.
     * @return 
     */
    public static Request getGetStoredFunctionsRequest(String username, ArrayList<String> vehnames){
        ArrayList<ParameterSet> parameters = new ArrayList<ParameterSet>();
        
        for (String vehname:vehnames){ 
            ParameterSet vehicleName = new ParameterSet();
            vehicleName.parameterName = "vehname";
            vehicleName.parameterType = String.class.getName();
            vehicleName.parameterValue = vehname;
            vehicleName.timeStamp = null;
            parameters.add(vehicleName);
        }
        
        Request createUserRequest = new Request(username, "GetStoredFunctions", parameters);
        return createUserRequest;
    }
    
}
