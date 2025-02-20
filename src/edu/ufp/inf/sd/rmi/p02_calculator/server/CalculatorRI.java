package edu.ufp.inf.sd.rmi.p02_calculator.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * <p>Title: Projecto SD</p>
 * <p>Description: Projecto apoio aulas SD</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: UFP </p>
 *
 * @author Rui Moreira
 * @version 1.0
 */
 /* TODO:
     1. Insert the other arithmetic polymorphic methods: sub(), mult() and div()
     2. Implement the class CalculatorImpl
     3. Implement the class CalculatorServer
     4. Run the service and then call it by running the client
 */
public interface CalculatorRI extends Remote {
    /**
     * @param a double to add
     * @param b double to add
     * @return result from adding a + b
     * @throws RemoteException if an RMI error occurs
     */
    public double add(double a, double b) throws RemoteException;

    /**
     * @param list of floats to add
     * @return result of adding all list elements
     * @throws RemoteException if an RMI error occurs
     */
    public double add(ArrayList<Double> list) throws RemoteException;

    /**
     * @param a dividend
     * @param b divisor
     * @return result from dividing a / b
     * @throws RemoteException if an RMI error occurs
     */
    public double div(double a, double b) throws RemoteException;

    /**
     * @param a dividend
     * @param b divisor
     * @return result from subtracting a - b
     * @throws RemoteException if an RMI error occurs
     */
    public double sub(double a, double b) throws RemoteException;

    /**
     * @param a dividend
     * @param b divisor
     * @return result from multipling a * b
     * @throws RemoteException if an RMI error occurs
     */
    public double mult(double a, double b) throws RemoteException;
}