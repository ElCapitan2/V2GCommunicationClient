/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package v2gCommunication.client;

import v2gCommunication.client.userfunctions.*;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import v2gCommunication.client.ui.*;
import v2gCommunication.client.userfunctions.UpdatePassword;
import v2gcommunication.commonclasses.encryption.SHA_1_hash;
import v2gcommunication.commonclasses.requests.Request;
import v2gcommunication.commonclasses.requests.RequestManagement;
import v2gcommunication.commonclasses.tasks.Task;
import v2gcommunication.commonclasses.tasks.TaskManagement;
import v2gcommunication.commonclasses.userrights.UserPrivilage;

/**
 * Class {@code ServerConnection} has three main tasks.
 * 
 * - initialize ServerConnection, TaskManagement, RequestManagement.
 * - wraps calls for UserFunctions to make them available in the UI.
 * - is used to react to responses from server.
 * 
 * The class needs an instance in many components of the UI, it is therefore
 * implemented as Singleton pattern.
 * 
 * @author Alexander Forell
 */
public class ServerConnection {
    
    // Instance of the Class itself.
    private static ServerConnection instance = null;
    
    // username for this session
    private String username = null;
    
    // userprivilages
    private UserPrivilage userPrivilage = null;
    
    // fields for taskManagement, requestManagement, sessionManagement, socket
    private final TaskManagement taskManagement;
    private final RequestManagement requestManagement;
    private final SessionManagementClient sessionManagementClient;
    private Socket socket;
    
    // fields for UI-components which need to be modified according to server data
    private JList jListUsers;
    private JList jListVehiclesVehicleManagement;
    private JList jListVehiclesRequestManagement;
    private JList jListVehiclesDataAccess;
    private JList jListVehicleFunctions;
    private JTextField jTextFieldLogin;
    private JCheckBox jCheckBoxModifyUsers;
    private JCheckBox jCheckBoxModifyVehicles;
    private JCheckBox jCheckBoxCanSendRequest;
    private JPasswordField jPasswordFieldPassword;
    private JPasswordField jPasswordFieldPasswordPassword;
    private JTabbedPane jTabbedPaneMainContent;
    private DataAccess dataAccess;
    private PanelUserManagement panelUserManagement;
    private PanelVehicleManagement panelVehicleManagement;
    private DataRequest panelDataRequest;
    
    private ServerConnection(){
        
        // initialze classes for request, task and session management.
        this.taskManagement = new TaskManagement();
        this.requestManagement = new RequestManagement();
        
        // Adding TaskWorker classes to communicate with server.
        this.requestManagement.addTaskWorker(CreateUser.class);
        this.requestManagement.addTaskWorker(GetUsers.class);
        this.requestManagement.addTaskWorker(DeleteUser.class);
        this.requestManagement.addTaskWorker(GetUserRights.class);
        this.requestManagement.addTaskWorker(UpdatePassword.class);
        this.requestManagement.addTaskWorker(UpdateUserrights.class);
        this.requestManagement.addTaskWorker(AddVehicle.class);
        this.requestManagement.addTaskWorker(GetVehicles.class);
        this.requestManagement.addTaskWorker(DeleteVehicle.class);
        this.requestManagement.addTaskWorker(InitiateRequestVehicleData.class);
        this.requestManagement.addTaskWorker(GetStoredFunctions.class);
        this.requestManagement.addTaskWorker(DownloadData.class);
        this.requestManagement.setStartTaskTransmit(taskManagement);
        this.sessionManagementClient = new SessionManagementClient();
        this.sessionManagementClient.setNewReceiveTaskListener(taskManagement);
        this.sessionManagementClient.setNewSessionGetTasksListener(taskManagement);
        this.sessionManagementClient.setTaskTransmittedListener(taskManagement);
        this.sessionManagementClient.setNewSessionGetRequestsListener(requestManagement);
        this.sessionManagementClient.setRequestReceivedListener(requestManagement);
        this.sessionManagementClient.setRequestTransmittedListener(requestManagement);
        this.taskManagement.setNewTransmitTask(sessionManagementClient);
        this.requestManagement.setAddRequest(sessionManagementClient);
        
    }
    
    
    /**
     * static method get instance stored in field instance, or create a new 
     * instance if it is still null.
     * 
     * @return instance of ServerConnection
     */
    public static synchronized ServerConnection getInstance(){
        if (instance == null) {
            instance = new ServerConnection();
        }
 
        return instance;
    }
    
    /**
     * Wrapped method to start a session, it sets the username field in this 
     * instance and connects to the server.
     * 
     * Method can only be called once, if called a second time 
     * {@code ConnectionAlreadyEstablishedException} is thrown.
     * 
     * @param username      username for logon to server.
     * @param password      username for logon to vehicle.
     * @throws IOException
     * @throws ConnectionAlreadyEstablishedException 
     */
    public void startSession(String username, String password) throws IOException, ConnectionAlreadyEstablishedException{
        
        if (this.username == null){
            this.username = username;
        }
        else {
            throw new ConnectionAlreadyEstablishedException();
        }
        Socket s = new Socket("localhost",25001);
        this.socket = s;
        SHA_1_hash sha1 = new SHA_1_hash();
        this.sessionManagementClient.startSession(s, username, sha1.doHash(password));
    }
    
    
    /**
     * Setter for JTabbedPane holding the Main content of the UI.
     * 
     * @param jTabbedPaneMainContent 
     */
    public void setJTabbedPaneMainContent(JTabbedPane jTabbedPaneMainContent){
        this.jTabbedPaneMainContent = jTabbedPaneMainContent;
    }
    
    /**
     * Setter for dataAccess-Pane of the UI.
     * 
     * @param dataAccess 
     */
    public void setDataAccess(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }
    
    /**
     * Setter for UserManagement panel
     * 
     * @param panelUserManagement 
     */
    public void setPanelUserManagement(PanelUserManagement panelUserManagement){
        this.panelUserManagement = panelUserManagement;
    }
    
    /**
     * Setter for VehicleManagement panel
     * 
     * @param panelVehicleManagement 
     */
    public void setPanelVehicleManagement(PanelVehicleManagement panelVehicleManagement){
        this.panelVehicleManagement = panelVehicleManagement;
    }

    /**
     * Setter for DataRequest panel
     * 
     * @param panelDataRequest
     */
    public void setDataRequest(DataRequest panelDataRequest){
        this.panelDataRequest = panelDataRequest;
    }
    
    /**
     * Setter for JListUsers in Usermanagement panel
     * 
     * @param jListUsers 
     */
    public void setJListUsers(JList jListUsers)   {
        this.jListUsers = jListUsers;
    }
    
    /**
     * Setter for JList with vehicles in vehicle management panel
     * 
     * @param jListVehicles 
     */
    public void setJListVehiclesVehicleManagement(JList jListVehicles)   {
        this.jListVehiclesVehicleManagement = jListVehicles;
    }
    
    /**
     * Setter for JList with vehicles in request management panel
     * 
     * @param jListVehicles 
     */
    public void setJListVehiclesRequestManagement(JList jListVehicles)   {
        this.jListVehiclesRequestManagement = jListVehicles;
    }
    
    /**
     * Setter for JList with vehicles in data access panel
     * 
     * @param jListVehicles 
     */
    public void setJListVehiclesDataAccess(JList jListVehicles)   {
        this.jListVehiclesDataAccess = jListVehicles;
    }
    
    /**
     * Setter for List of fuctions in data access panel
     * 
     * @param jListVehicleFunctions 
     */
    public void setJListVehiclesFunctions(JList jListVehicleFunctions)   {
        this.jListVehicleFunctions = jListVehicleFunctions;
    }
    
    /**
     * Setter for login testfield in user management panel.
     * 
     * @param jTextFieldLogin 
     */
    public void setJTextFieldLogin(JTextField jTextFieldLogin){
        this.jTextFieldLogin = jTextFieldLogin;
    }
    
    /**
     * Setter for checkbox CanModifyUsers on user management panel.
     * 
     * @param jCheckBoxModifyUsers 
     */
    public void setJCheckBoxModifyUsers(JCheckBox jCheckBoxModifyUsers){
        this.jCheckBoxModifyUsers = jCheckBoxModifyUsers;
    }
    
    /**
     * Setter for checkbox CanModifyVehicles on user management panel.
     * 
     * @param jCheckBoxModifyVehicles 
     */
    public void setJCheckBoxModifyVehicles(JCheckBox jCheckBoxModifyVehicles){
        this.jCheckBoxModifyVehicles = jCheckBoxModifyVehicles;
    }
    
    /**
     * Setter for checkbox CanSendRequest on user management panel.
     * 
     * @param jCheckBoxCanSendRequest 
     */
    public void setJCheckBoxCanSendRequest(JCheckBox jCheckBoxCanSendRequest){
        this.jCheckBoxCanSendRequest = jCheckBoxCanSendRequest;
    }
    
    /**
     * Setter for passwordfield on user management panel.
     * 
     * @param jPasswordFieldPassword 
     */
    public void setJPasswordFieldPassword(JPasswordField jPasswordFieldPassword){
        this.jPasswordFieldPassword = jPasswordFieldPassword;
    }
    
    /**
     * Setter for password confirm field on user management panel.
     * 
     * @param jPasswordFieldPasswordPassword 
     */
    public void setJPasswordFieldPasswordPassword(JPasswordField jPasswordFieldPasswordPassword){
        this.jPasswordFieldPasswordPassword = jPasswordFieldPasswordPassword;
    }
    
    /**
     * Wrapper method create user create a new user for the client
     * 
     * @param newUsername           Name of the new user to be created
     * @param password              Password for this new user
     * @param canModifyUsers        True if user can add and delete users
     * @param canModifyVehicles     True if user can add or delete vehicles
     * @param canSendRequest        True if user can send request to vehicle
     */
    public void createUser(String newUsername ,String password, 
            boolean canModifyUsers, boolean canModifyVehicles, boolean canSendRequest){
        Request re = CreateUser.getCreateUserRequest(username, newUsername, 
                password, canModifyUsers, canModifyVehicles, canSendRequest);
        requestManagement.addRequest(re);
    }
    
    /**
     * Callback method called if user was created. 
     */
    public void userCreated(){
        getUsers();
    }
    
    /**
     * Wrapper Method to create GetUsersRequest.
     */
    public void getUsers(){
        Request re = GetUsers.getGetUsersRequest(username);
        requestManagement.addRequest(re);
    }
    
    /**
     * Mehtod to close this session.
     */
    public void closeSession(){
        this.instance = null;
        this.username = null;
        try {
            socket.close();
        } catch (IOException ex) {
            
        }  
    }
    
    /**
     * Callback for {@code getUsers()} this methods updates the user list.
     * 
     * @param users 
     */
    public void setUserList(ArrayList<String> users){
        DefaultListModel model = new DefaultListModel();
        for (String user:users){
            model.addElement(user);
        }
        jListUsers.setModel(model);
    }
    
    /**
     * Deletes a user form the database
     * 
     * @param userToBeDeleted   username of the user to be deleted.
     */
    public void deleteUser(String userToBeDeleted){
        Request re = DeleteUser.getDeleteUserRequest(username, userToBeDeleted);
        requestManagement.addRequest(re);
    }
    
    /**
     * CallBack for {@code deleteUser}
     */
    public void userDeleted(){
        getUsers();
    }
    
    /**
     * Wrapper method to get userrights from server
     * 
     * @param userToGetUserRights 
     */
    public void getUserRights(String userToGetUserRights){
        Request re = GetUserRights.getGetUserRightsRequest(username, userToGetUserRights);
        requestManagement.addRequest(re);
    }
    
    /**
     * Callback mehtod {@code getUserRights}.
     * 
     * Sets accessrights for the logged in user, and updates the user management
     * panel in case it the selected user is not the loggeed in user.
     * 
     * @param userPrivilage 
     */
    public void userrightsReceived(UserPrivilage userPrivilage){
        this.jTextFieldLogin.setText(userPrivilage.username);
        this.jCheckBoxModifyUsers.setSelected(userPrivilage.canModifyUsers);
        this.jCheckBoxModifyVehicles.setSelected(userPrivilage.canModifyVehicles);
        this.jCheckBoxCanSendRequest.setSelected(userPrivilage.canSendRequests);
        this.jPasswordFieldPassword.setText(null);
        this.jPasswordFieldPasswordPassword.setText(null);
        if (this.username.equals(userPrivilage.username)){
            if (userPrivilage.canModifyUsers){
                this.jTabbedPaneMainContent.add("User Management", this.panelUserManagement);
            }
            if (userPrivilage.canModifyVehicles){
                this.jTabbedPaneMainContent.add("Vehicle Management", this.panelVehicleManagement);
            }
            if (userPrivilage.canSendRequests){
                this.jTabbedPaneMainContent.add("Send Requests", this.panelDataRequest);
            }
            this.jTabbedPaneMainContent.add("Download Data", this.dataAccess);
        }
    }
    
    /**
     * Create a changePassword request
     * 
     * @param nameOfUser    Username of whom the password shall be reset
     * @param password      password.
     */
    public void changePassword(String nameOfUser ,String password){
        Request re = UpdatePassword.getUpdatePasswordRequest(username, nameOfUser, 
                password);
        requestManagement.addRequest(re);
    }
    
    /**
     * Create Request to update userrights 
     * 
     * @param nameOfUser            Name of user
     * @param canModifyUsers        True if user can add and delete users
     * @param canModifyVehicles     True if user can add or delete vehicles
     * @param canSendRequest        True if user can send request to vehicle
     */
    public void updateUserRights(String nameOfUser, 
        boolean canModifyUsers, boolean canModifyVehicles, boolean canSendRequest){
        Request re = UpdateUserrights.getUpdateUserrightsRequest(username, nameOfUser, 
                canModifyUsers, canModifyVehicles, canSendRequest);
        requestManagement.addRequest(re);
    }
    
    /**
     * Create Request add vehcile
     * 
     * @param VIN       VIN of the vehicle
     * @param vehname   Name of the vehicle
     */
    public void addVehicle(String VIN, String vehname){
        Request re = AddVehicle.getAddVehicleRequest(username,VIN ,vehname); 
        requestManagement.addRequest(re);
    }
    
    /**
     * Callback to {@code addVehicle}
     */
    public void vehicleAdded(){
        getVehicles();
    }
    
    /**
     * Create request to Receive a List of vehicles.
     */
    public void getVehicles(){
        Request re = GetVehicles.getGetVehiclesRequest(username);
        requestManagement.addRequest(re);
    }
    
    /**
     * Callback to {code get vehicles}. Updates the List displaying vehicles 
     * in the UI.
     * 
     * @param vehicles      List of vehicles.
     */
    public void setVehicleList(ArrayList<String> vehicles){
        DefaultListModel model = new DefaultListModel();
        for (String vehicle:vehicles){
            model.addElement(vehicle);
        }
        jListVehiclesVehicleManagement.setModel(model);
        jListVehiclesRequestManagement.setModel(model);
        jListVehiclesDataAccess.setModel(model);
    }
    
    /**
     * Create request delete a vehicle.
     * 
     * @param vehname 
     */
    public void deleteVehicle(String vehname){
        Request re = DeleteVehicle.getDeleteVehicleRequest(username,vehname); 
        requestManagement.addRequest(re);
    }
    /**
     * Callback method for {@code deleteVehicle}
     */
    public void vehicleDeleted(){
        getVehicles();
    }
    
    /**
     * Build a request to be transferred to transferred to the vehicles.
     * 
     * @param vehnames          Names of the vehicles
     * @param functionNames     Functions to be extracted from vehicles
     * @param intervall         Intervall in [ms]
     * @param iterations        Number of iterations [-]
     */
    public void initiateRequest(ArrayList<String> vehnames, ArrayList<String> functionNames, 
            int intervall, int iterations){
        Request re = InitiateRequestVehicleData.getInitiateRequestVehicleDataRequest(username, 
                vehnames, functionNames, intervall, iterations); 
        requestManagement.addRequest(re);
    }
    
    /**
     * Request to get functions for which there is data stored in the databaase.
     * 
     * @param vehnames 
     */
    public void getVehicleFunctions(ArrayList<String> vehnames){
        Request re = GetStoredFunctions.getGetStoredFunctionsRequest(username, vehnames);
        requestManagement.addRequest(re);
    }
    
    
    /**
     * Callback to {@code getVehicleFunctions}. Updates the lsit hoding the 
     * vehicle functions.
     * 
     * @param vehiclefunctions 
     */
    public void setVehicleFunctions(ArrayList<String> vehiclefunctions){
        DefaultListModel model = new DefaultListModel();
        for (String function:vehiclefunctions){
            model.addElement(function);
        }
        jListVehicleFunctions.setModel(model);
    }
    
    /**
     * Create a download data request.
     * 
     * @param vehicles      List of vehicles
     * @param functions     List of functions
     * @param fullpath      Path of File in which data shall be stored.
     */
    public void downloadData(ArrayList<String> vehicles, ArrayList<String> functions, String fullpath){
        Request re = DownloadData.getDownloadDataRequest(username, vehicles, functions, fullpath);
        requestManagement.addRequest(re);
    }
    
    /**
     * Method needed to get the Tasks in which the data hase been stored
     * 
     * @param taskID
     * @return 
     */
    public Task getReceiveTask(String taskID){
        return taskManagement.getReceiveTask(taskID);
    }
    
    /**
     * Method to remove recheive task from list if data is stored.
     * 
     * @param ta 
     */
    public void dataStored(Task ta){
        taskManagement.dataStored(ta);
    }
    
}
