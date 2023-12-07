package com.example.appproject;

public class DataModel {
    private int id;
    private String[] myArrayColumn;
    private double[] myDoubleArrayColumn;
    private String year;

    public DataModel(int id, String[] myArrayColumn, double[] myDoubleArrayColumn, String year) {
        this.id = id;
        this.myArrayColumn = myArrayColumn;
        this.myDoubleArrayColumn = myDoubleArrayColumn;
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public String[] getMyArrayColumn() {
        return myArrayColumn;
    }

    public double[] getMyDoubleArrayColumn() {
        return myDoubleArrayColumn;
    }

    public String getYear() {
        return year;
    }
}
