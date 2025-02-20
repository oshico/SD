package edu.ufp.inf.sd.rmi.p02_calculator.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class CalculatorImpl extends UnicastRemoteObject implements CalculatorRI {

    // Constructor
    protected CalculatorImpl() throws RemoteException {
        super();
    }

    @Override
    public double add(double a, double b) throws RemoteException {
        return a + b;
    }

    @Override
    public double add(ArrayList<Double> list) throws RemoteException {
        double sum = 0;
        for (double num : list) {
            sum += num;
        }
        return sum;
    }

    @Override
    public double sub(double a, double b) throws RemoteException {
        return a - b;
    }

    @Override
    public double mult(double a, double b) throws RemoteException {
        return a * b;
    }

    @Override
    public double div(double a, double b) throws RemoteException {
        if (b == 0) throw new ArithmeticException("Cannot divide by zero");
        return a / b;
    }
}
