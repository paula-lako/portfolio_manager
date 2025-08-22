package com.neueda.portfoliomanager.entity;

import java.util.Arrays;
import java.util.List;

public enum StockTicker {
    PKO, PKN, PZU, SPL, PEO, DNP, ING, MBK, ALE, LPP,
    KGH, PGE, CDR, ZAB, ACP, ALR, TPE, BHW, BDX, CCC,
    PCO, OPL, ENA, CPS, XTB, KTY, KRU, MRB, CAR, ASE,
    GPW, WPL, RBW, NEU, MIL, PEP, PKP, APR, STP, LWB,
    BFT, DOM, ERB, ENT, UNI, ANR, SNK;


    public static List<String> getAll() {
        return Arrays.stream(values())
                .map(Enum::name)
                .toList();
    }
}

