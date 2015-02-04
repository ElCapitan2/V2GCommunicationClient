/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package v2gCommunication.client.userfunctions;

import java.util.ArrayList;
import v2gCommunication.client.ServerConnection;
import v2gcommunication.commonclasses.encryption.SHA_1_hash;
import v2gcommunication.commonclasses.requests.Request;
import v2gcommunication.commonclasses.requests.RequestType;
import v2gcommunication.commonclasses.tasks.ParameterSet;
import v2gcommunication.commonclasses.requests.AddRequest;
import v2gcommunication.commonclasses.tasks.StartTaskTransmit;
import v2gcommunication.commoninterfaces.TaskWorker;

/**
 * Client side implementation to build a request to register a vehicle on the
 * server
 * 
 * Class implements Taskworker which extends Runnable.
 * 
 * The run method processes the answer from the server. 
 * 
 * @author Alexander Forell
 */
public class AddVehicle implements TaskWorker{
    private Request request;
    private AddRequest requestReply;
    
    /**
     * Run mehthod must be overridden as it is defined in TaskWorker.
     * 
     * Server will return a request indicating whether the vehicle 
     * was added successfully
     * 
     * This method reads the request and puts out true or false to the 
     * commandline.
     * 
     * It also calls the callback method {@code ServerConnection.vehicleAdded}
     * 
     */
    @Override
    public void run() {
        if (request.getRequestType()==RequestType.ANSWER){
            ArrayList<ParameterSet> parameters = new ArrayList<ParameterSet>();
            parameters.addAll(request.getParameterSet());
            boolean vehAdded = false;
            String vehname = null;
            for (ParameterSet para:parameters){
                if (para.parameterName.equals("vehAdded") && para.parameterType.equals(boolean.class.getName())){
                    vehAdded = Boolean.parseBoolean(para.parameterValue);
                }
                if (para.parameterName.equals("vehname") && para.parameterType.equals(String.class.getName())){
                    vehname = para.parameterValue;
                }
            }
            System.out.println();
            System.out.println("***************************************************");
            System.out.println("ANSWER TO CREATE VEHICLE");
            System.out.println("========================");
            System.out.println("Request for creating vehicle: " + vehname + " processed.");
            System.out.println("Request processed successfully: " + vehAdded);
            System.out.println("***************************************************");
            System.out.println();
            ServerConnection sc = ServerConnection.getInstance();
            sc.vehicleAdded();
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
     * Static method to generate a add vehicle request. The returned 
     * Request can be added to a Session and will then be transmitted 
     * to the server.
     * 
     * @param username      Name of the logged in user
     * @param VIN           VIN of the vehicle
     * @param vehname       Vehicle name
     * @return              Request
     */
    public static Request getAddVehicleRequest(String username, String VIN ,String vehname){
        ArrayList<ParameterSet> parameters = new ArrayList<ParameterSet>();
        
        //Create Parameter set for VIN
        ParameterSet psVIN = new ParameterSet();
        psVIN.parameterName="VIN";
        psVIN.parameterType=String.class.getName();
        SHA_1_hash hash = new SHA_1_hash();
        psVIN.parameterValue=hash.doHash(VIN);
        psVIN.timeStamp = null;
        parameters.add(psVIN);
        
        //Create Parameter set for vehName
        ParameterSet psVehname = new ParameterSet();
        psVehname.parameterName="vehname";
        psVehname.parameterType=String.class.getName();
        psVehname.parameterValue=vehname;
        psVehname.timeStamp = null;
        parameters.add(psVehname);
        
        
        Request createUserRequest = new Request(username, "AddVehicle", parameters);
        return createUserRequest;
    }
    
}
