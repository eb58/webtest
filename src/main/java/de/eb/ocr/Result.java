package de.eb.ocr;

import de.eb.ocr.image.Matrix;

public class Result implements Cloneable {

    public Result() {
        cidx = -1;
        c = "~";
        dbdiff = CharDatabase.DIFFMAXVAL;
    }
    String c; // Klassifiziert als dieses Zeichen
    int cidx; // Index des Zeichen in m_db
    int dbdiff; // min Abstand zu allen Zeichen in DB
    Matrix dbm; // Matrix dieses Images
    double mq; // Matching Quotient
    double conf; // Confidence of first guess relativly to second
    int diffSimple;
    int diffAbs;

    /////////////////////////////////////////////////////////////////////////////////
    @Override
    public Object clone() {
        try {
            Result r = (Result) super.clone();
            //r.dbm = new Matrix();
            return (Object) r;
        } catch (CloneNotSupportedException e) { // Dire trouble!!!
            throw new InternalError("But we are Cloneable!!!");
        }
    }

    public static void sortResVec(Result rv[], int N) { // sortiere rv aufsteigend
        for (int i = 0; i < 5; i++)
            for (int j = i + 1; j < N; j++)
                if (rv[i].dbdiff > rv[j].dbdiff) {
                    Result tmp = (Result) rv[i].clone();
                    rv[j] = (Result) rv[i].clone();
                    rv[i] = (Result) tmp.clone();
                }
    }
};
