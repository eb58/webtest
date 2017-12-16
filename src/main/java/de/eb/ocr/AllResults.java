package de.eb.ocr;

import de.eb.ocr.util.Counter;

public class AllResults {

    Counter cntparts;    // Anzahl der Glyphanteile
    double conf;
    double match;
    String res;
    Result rvCOL[];
    Result rvF[];
    Result rvQUAD[];
    Result rvROW[];
    Result rvS[];
    String secbest;

    public AllResults(int nCharsInDB) {
        conf = 0.0;
        res = "~";
        secbest = "~";
        cntparts = new Counter(0);
        rvS = new Result[nCharsInDB];
    }

    void dump() {
        // DebugMsg( "INFO:>>>>>>>>>> RES:%c CONF:%6.2f<<<<<<<<<<<<<", res, conf );
        // if(rvS[0].c !='~') DebugMsg( "INFO:rsS (%c %6d)(%c %6d)(%c %6d)", rvS[0].c, rvS[0].dbdiff, rvS[1].c,
        // rvS[1].dbdiff, rvS[2].c, rvS[2].dbdiff );
        // if(rvF[0].c !='~') DebugMsg( "INFO:rvF (%c %6d)(%c %6d)(%c %6d)", rvF[0].c, rvF[0].dbdiff, rvF[1].c,
        // rvF[1].dbdiff, rvF[2].c, rvF[2].dbdiff );
        // if(rvROW[0].c !='~') DebugMsg( "INFO:rvR (%c %6d)(%c %6d)(%c %6d)", rvROW[0].c, rvROW[0].dbdiff, rvROW[1].c,
        // rvROW[1].dbdiff, rvROW[2].c, rvROW[2].dbdiff );
        // if(rvCOL[0].c !='~') DebugMsg( "INFO:rsC (%c %6d)(%c %6d)(%c %6d)", rvCOL[0].c, rvCOL[0].dbdiff, rvCOL[1].c,
        // rvCOL[1].dbdiff, rvCOL[2].c, rvCOL[2].dbdiff );
        // if(rvQUAD[0].c!='~') DebugMsg( "INFO:rsQ (%c %6d)(%c %6d)(%c %6d)", rvQUAD[0].c, rvQUAD[0].dbdiff, rvQUAD[1].c,
        // rvQUAD[1].dbdiff, rvQUAD[2].c, rvQUAD[2].dbdiff );
        // }
    }
};


