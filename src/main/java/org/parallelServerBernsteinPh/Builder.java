package org.parallelServerBernsteinPh;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.oristool.models.gspn.GSPNSteadyState;
import org.oristool.models.gspn.GSPNTransient;
import org.oristool.models.stpn.*;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;
import org.oristool.util.Pair;


public class Builder {
    private Marking marking;
    private int numPlaces;
    private int numPh;
    private int[] arrivalRates;
    private int poolSize;
    private PetriNet net;

    // Costruttore della classe
    public Builder(Marking marking, int numPlaces, int numPh, int[] arrivalRates, int poolSize) {
        this.marking = marking;
        this.numPlaces = numPlaces;
        this.numPh = numPh;
        this.arrivalRates = arrivalRates;
        this.poolSize = poolSize;
    }
    public void setPetriNet(PetriNet pn){
        this.net = pn;
    }
    public PetriNet getPetriNet(){
        return this.net;
    }
    public void setArrivalRates(int[] arrivalRates){
        this.arrivalRates = arrivalRates;
    }


    public PetriNet build() {
        PetriNet net = new PetriNet();
        // Generare Pn e An
        for (int i = 1; i <= numPlaces; i++) {
            //net.addPlace("P" + i);
            net.addPlace("A" + i);
        }
        Place Pool = net.addPlace("Pool");

        //Generare Phn
        for (int i = 1; i <= numPh; i++) {
            net.addPlace("Ph" + i);
        }

        // Generare Transizioni
        for (int i = 1; i <= numPh; i++) {
            net.addTransition("t" + i);
        }


        for (int i = 1; i <= numPlaces; i++) {
            for (int j = 1; j <= numPh; j++) {
                net.addTransition("t" + i + j);
            }
        }

        //Generare Precondizioni e PostCondizioni  ->Pool
        for(int i = 1; i<= numPlaces;i++){
            for(int j = 1; j<= numPh;j++){
                //An -> tnk
                net.addPrecondition(net.getPlace("A"+i), net.getTransition("t"+i+j));
                //An <- tnk
                net.addPostcondition(net.getTransition("t"+i+j), net.getPlace("A"+i));
                //Pool ->Tnk
                net.addPrecondition(Pool, net.getTransition("t"+i+j));
                //Tnk -> Phn
                net.addPostcondition(net.getTransition("t"+i+j), net.getPlace("Ph" + j));
            }
        }
        //PHn -> tn
        for(int i = 1; i <= numPh;i++){
            net.addPrecondition(net.getPlace("Ph"+i), net.getTransition("t"+i));
        }
        //Tn -> Ph n+1
        for(int i = 1; i<numPh; i++)
        {
            net.addPostcondition(net.getTransition("t"+ i), net.getPlace("Ph"+ (i+1)));
        }
        //Phn -> Pool
        net.addPostcondition(net.getTransition("t"+ numPh), net.getPlace("Pool"));

        //Generating Properties
        for(int i = 1; i <= numPlaces; i++)
        {
            marking.setTokens(net.getPlace("A"+ i), 1);
            //marking.setTokens(net.getPlace("P"+ i), arrivalRates[i-1]);
            marking.setTokens(net.getPlace("Ph"+ i), 0);
        }
        marking.setTokens(Pool, poolSize);

        net.getTransition("t1").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("1*Ph1" , net)));
        net.getTransition("t11").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.25*"+ arrivalRates[0], net)));
        net.getTransition("t12").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.25*"+ arrivalRates[0], net)));
        net.getTransition("t13").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.25*"+ arrivalRates[0], net)));
        net.getTransition("t14").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.25*"+ arrivalRates[0], net)));
        net.getTransition("t2").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("2*Ph2", net)));
        net.getTransition("t21").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.1*"+ arrivalRates[1], net)));
        net.getTransition("t22").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.2*"+ arrivalRates[1], net)));
        net.getTransition("t23").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.3*"+ arrivalRates[1], net)));
        net.getTransition("t24").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.4*"+ arrivalRates[1], net)));
        net.getTransition("t3").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("3*Ph3", net)));
        net.getTransition("t31").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.4*"+ arrivalRates[2], net)));
        net.getTransition("t32").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.3*"+ arrivalRates[2], net)));
        net.getTransition("t33").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.2*"+ arrivalRates[2], net)));
        net.getTransition("t34").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.1*"+ arrivalRates[2], net)));
        net.getTransition("t4").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("4*Ph4", net)));
        setPetriNet(net);
        return net;
    }

    public PetriNet setSteadyStateTransition(PetriNet net, Map<RewardRate, BigDecimal> steadyState, int numPh, Marking marking) {
        Place InitialPlace = net.addPlace("InitialPlace");
        marking.setTokens(net.getPlace("Pool"), 0);
        marking.setTokens(net.getPlace("InitialPlace"), poolSize);

        for (RewardRate rewards : steadyState.keySet()) {
            String transitionName = rewards.toString()+"_t";
            net.addTransition(transitionName);
            net.addPrecondition(net.getPlace("InitialPlace"), net.getTransition(transitionName));
            net.addPostcondition(net.getTransition(transitionName), net.getPlace(rewards.toString()));
            net.getTransition(transitionName).addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal(0), MarkingExpr.from(steadyState.get(rewards).toString(), net)));
        }
        return net;
    }
    public PetriNet buildSteadyStateNet(Map<RewardRate, BigDecimal> steadyState, int numPh, Marking marking, int[]newArrivalRates, int poolSize) {
        PetriNet net = new PetriNet();
        Place initialPlace = net.addPlace("InitialPlace");

        // Generare Pn e An
        for (int i = 1; i <= numPlaces; i++) {
            net.addPlace("A" + i);
        }
        Place Pool = net.addPlace("Pool");

        //Generare Phn
        for (int i = 1; i <= numPh; i++) {
            net.addPlace("Ph" + i);
        }

        // Generare Transizioni
        for (int i = 1; i <= numPh; i++) {
            net.addTransition("t" + i);
        }


        for (int i = 1; i <= numPlaces; i++) {
            for (int j = 1; j <= numPh; j++) {
                net.addTransition("t" + i + j);
            }
        }

        //Generare Precondizioni e PostCondizioni  ->Pool
        for(int i = 1; i<= numPlaces;i++){
            for(int j = 1; j<= numPh;j++){
                //An -> tnk
                net.addPrecondition(net.getPlace("A"+i), net.getTransition("t"+i+j));
                //An <- tnk
                net.addPostcondition(net.getTransition("t"+i+j), net.getPlace("A"+i));
                //Pool ->Tnk
                net.addPrecondition(Pool, net.getTransition("t"+i+j));
                //Tnk -> Phn
                net.addPostcondition(net.getTransition("t"+i+j), net.getPlace("Ph" + j));
            }
        }
        //PHn -> tn
        for(int i = 1; i <= numPh;i++){
            net.addPrecondition(net.getPlace("Ph"+i), net.getTransition("t"+i));
        }
        //Tn -> Ph n+1
        for(int i = 1; i<numPh; i++)
        {
            net.addPostcondition(net.getTransition("t"+ i), net.getPlace("Ph"+ (i+1)));
        }
        //Phn -> Pool
        net.addPostcondition(net.getTransition("t"+ numPh), net.getPlace("Pool"));

        //Generating Properties
        for(int i = 1; i <= numPlaces; i++)
        {
            marking.setTokens(net.getPlace("A"+ i), 1);
            //marking.setTokens(net.getPlace("P"+ i), marking.getTokens(net.getPlace("P"+ i)));
            marking.setTokens(net.getPlace("Ph"+ i), 0);
        }
        marking.setTokens(initialPlace, poolSize);
        marking.setTokens(Pool, 0);
        net.getTransition("t1").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("1*Ph1", net)));
        net.getTransition("t11").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.25*"+newArrivalRates[0], net)));
        net.getTransition("t12").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.25*"+newArrivalRates[0], net)));
        net.getTransition("t13").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.25*"+newArrivalRates[0], net)));
        net.getTransition("t14").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.25*"+newArrivalRates[0], net)));
        net.getTransition("t2").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("2*Ph2", net)));
        net.getTransition("t21").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.1*"+newArrivalRates[1], net)));
        net.getTransition("t22").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.2*"+newArrivalRates[1], net)));
        net.getTransition("t23").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.3*"+newArrivalRates[1], net)));
        net.getTransition("t24").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.4*"+newArrivalRates[1], net)));
        net.getTransition("t3").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("3*Ph3", net)));
        net.getTransition("t31").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.4*"+newArrivalRates[2], net)));
        net.getTransition("t32").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.3*"+newArrivalRates[2], net)));
        net.getTransition("t33").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.2*"+newArrivalRates[2], net)));
        net.getTransition("t34").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("0.1*"+newArrivalRates[2], net)));
        net.getTransition("t4").addFeature(StochasticTransitionFeature.newExponentialInstance(new BigDecimal("1"), MarkingExpr.from("4*Ph4", net)));
        for (RewardRate rewards : steadyState.keySet()) {
            String transitionName = rewards.toString()+"_t";
            net.addTransition(transitionName);
            net.addPrecondition(initialPlace, net.getTransition(transitionName));
            net.addPostcondition(net.getTransition(transitionName), net.getPlace(rewards.toString()));
            net.getTransition(transitionName).addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal(0), MarkingExpr.from(steadyState.get(rewards).toString(), net)));
        }

        return net;
    }



}

        /*
        for(int i = 1; i<= numPh; i++){
            net.addTransition("sph" + i);
            net.addPrecondition(net.getPlace("InitialPlace"), net.getTransition("sph"+i));
            net.addPostcondition(net.getTransition("sph"+i), net.getPlace("Ph"+i));
        }
        net.addTransition("sp");
        net.addPrecondition(net.getPlace("InitialPlace"), net.getTransition("sp"));
        net.addPostcondition(net.getTransition("sp"), net.getPlace("Pool"));


        //net.addTransition("InitialPlace");
        net.getTransition("sph1").addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("1"), MarkingExpr.from("1*" , net)));
    }
}
*/