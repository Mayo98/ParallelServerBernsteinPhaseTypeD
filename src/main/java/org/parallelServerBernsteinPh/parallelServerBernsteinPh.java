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


public class parallelServerBernsteinPh {


    public static void setSteadyStateTransition(PetriNet net, Map<RewardRate, BigDecimal> steadyState, int numPlaces, int numPh) {
        Place InitialPlace = net.addPlace("InitialPlace");

        for (RewardRate reward : steadyState.keySet()) {

        }
        for (int i = 0; i < numPh; i++) {
            net.addTransition("sph" + i);
            net.addPrecondition(net.getPlace("InitialPlace"), net.getTransition("sph" + i));
            net.addPostcondition(net.getTransition("sph" + i), net.getPlace("Ph" + i));
        }
        net.addTransition("sp");
        net.addPrecondition(net.getPlace("InitialPlace"), net.getTransition("sp"));
        net.addPostcondition(net.getTransition("sp"), net.getPlace("Pool"));


        //net.addTransition("InitialPlace");
        net.getTransition("Prova").addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("1"), MarkingExpr.from("1*InitialPlace", net)));
    }


    public static String getPoolCopiesRewards() {
        return "If(Pool==0,1,0);If(Pool==1,1,0);If(Pool==2,1,0);If(Pool==3,1,0);If(Pool==4,1,0);If(Pool==5,1,0);If(Pool==6,1,0);If(Pool==7,1,0);If(Pool==8,1,0)";
    }

    public static String getTokensDistributionRewards() {
        return "Pool;Ph1;Ph2;Ph3;Ph4";
    }


    //funzione per cambiare marcatura su Pool
    public static void changePoolsize(PetriNet net, Marking marking, int poolSize) {
        Place pool = net.getPlace("Pool");
        marking.setTokens(pool, poolSize);
    }

    //funzione per cambiare marcatura su Pn
    public static int[] newArrivals(int[]arrivalRates, int arrival, int value) {
        int[] newArrivals = arrivalRates;
        newArrivals[arrival] = value;
        return newArrivals;
    }
    public static void changeInitialPlace(PetriNet net, Marking marking, int poolSize) {
        Place pool = net.getPlace("InitialPlace");
        marking.setTokens(pool, poolSize);
    }


    public static void saveResults(String filePath, Map<RewardRate, BigDecimal> steadyState, int value, PetriNet pn, Marking marking, int mode, int[] arrivalRates) {
        if (mode == 1) {
            String fileNames = filePath + "SteadyStateFileNames.txt";
            BigDecimal b = new BigDecimal(0);
            String result = filePath +  arrivalRates[0] + "P1 " +
                    arrivalRates[1] + "P2 " +
                    arrivalRates[2] + "P3 " +
                    marking.getTokens("Pool") + "Pool.txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileNames, true))) {
                writer.write(result);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(result, false))) {

                for (RewardRate reward : steadyState.keySet()) {
                    writer.write(reward.toString() + " : " + steadyState.get(reward) + " " + value);
                    writer.newLine();
                    BigDecimal bigDecimal = steadyState.get(reward);
                    b = b.add(bigDecimal);
                }
                writer.newLine();
                writer.write(b.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String result = filePath + "SteadyStateTests.txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(result, true))) {

                for (RewardRate reward : steadyState.keySet()) {
                    writer.write(reward.toString() + " : " + steadyState.get(reward) + " " + value);
                    writer.newLine();
                }
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveTransientResults(String filePath, TransientSolution<Marking, RewardRate> rewards, Marking marking, int val, String markingChanged, int[] arrivalRates) {
        String fileNames = filePath + "TransientFileNames.txt";
        String result;
        if(marking.getTokens("InitialPlace") != 0) {
             result = filePath + arrivalRates[0] + "P1 " +
                    arrivalRates[1] + "P2 " +
                    arrivalRates[2] + "P3 " +
                    marking.getTokens("InitialPlace") + "Pool" + markingChanged + ".txt";
        }
        else {
             result = filePath + arrivalRates[0] + "P1 " +
                    arrivalRates[1] + "P2 " +
                    arrivalRates[2] + "P3 " +
                    marking.getTokens("Pool") + "Pool" + markingChanged + ".txt";
        }
        List<RewardRate> rewardsList = rewards.getColumnStates();
        double step = 0.1;
        double time = step;
        double[][][] solutions = rewards.getSolution();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileNames, true))) {
            writer.write(result);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(result, false))) {
            writer.write("Time ");
            for (RewardRate reward : rewardsList) {
                writer.write(reward.toString() + " ");
            }
            writer.newLine();
            for (int i = 0; i < solutions.length; i++) {
                writer.write(time + " ");
                time += step;
                for (int j = 0; j < solutions[i][0].length; j++) {
                    writer.write(solutions[i][0][j] + " ");

                }
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static List<RewardRate> geTokensDistributionRewardsList() {
        List<RewardRate> tokensDistributionRewards = new ArrayList<>();
        String rewards = getTokensDistributionRewards();
        String[] conditions = rewards.split(";");
        for (int i = 0; i < conditions.length; i++) {
            tokensDistributionRewards.add(RewardRate.fromString(conditions[i]));
        }
        return tokensDistributionRewards;
    }

    public static List<RewardRate> getPoolCopiesRewardsList() {
        List<RewardRate> PoolCopiesRewards = new ArrayList<>();
        String rewards = getPoolCopiesRewards();
        String[] conditions = rewards.split(";");
        for (int i = 0; i < conditions.length; i++) {
            PoolCopiesRewards.add(RewardRate.fromString(conditions[i]));
        }
        return PoolCopiesRewards;
    }
    public static BigDecimal getEmptyPoolReward(Map<RewardRate, BigDecimal> steadyState){
        BigDecimal targetValue = null;
        String targetExpression = "If(Pool==0,1,0)";

        for (RewardRate key : steadyState.keySet()) {
            if (key.toString().equals(targetExpression)) { // Confronta la rappresentazione in stringa
                targetValue = steadyState.get(key);
                break;
            }
        }
        return targetValue;
    }

    public static SteadyStateSolution<RewardRate> calculateGSPNSteadyState(PetriNet pn, Marking marking, int poolSize, String filePath, List<RewardRate> rewardList, int mode, int[] arrivalRates) {
        Map<Marking, Double> steadyStateMap = GSPNSteadyState.builder().build().compute(pn, marking);
        Map<Marking, BigDecimal> convertedSteadyStateMap = new HashMap<>();


        for (Map.Entry<Marking, Double> entry : steadyStateMap.entrySet()) {
            convertedSteadyStateMap.put(entry.getKey(), BigDecimal.valueOf(entry.getValue()));
        }
        SteadyStateSolution<Marking> solution = new SteadyStateSolution<>(convertedSteadyStateMap);

        SteadyStateSolution<RewardRate> rewards = SteadyStateSolution.computeRewards(solution, rewardList.toArray(new RewardRate[0]));
        Map<RewardRate, BigDecimal> steadyState = rewards.getSteadyState();
        saveResults(filePath, steadyState, poolSize, pn, marking, mode, arrivalRates);
        return rewards;
    }


    public static void calculateGSPNTransientAnalysis(PetriNet pn, Marking marking, int poolSize, String filePath, List<RewardRate> rewardList, String markingChanged, int[] arrivalRates) {//, SteadyStateSolution<Marking> solution) {
        double step = 0.1;
        Pair<Map<Marking, Integer>, double[][]> result = GSPNTransient.builder()
                .timePoints(0.0, 10.0, step)
                .build().compute(pn, marking);
        Map<Marking, Integer> statePos = result.first();
        double[][] probs = result.second();

        TransientSolution<Marking, Marking> transientSolution = TransientSolution.fromArray(probs, step, statePos, marking);
        TransientSolution<Marking, RewardRate> rewards = TransientSolution.computeRewards(false, transientSolution, rewardList.toArray(new RewardRate[0]));
        //TransientSolution<RewardRate, BigDecimal> rewardsA = new TransientSolution<>(new BigDecimal(10), new BigDecimal(step),)       TransientSolution.computeRewards(true,)
        //rewards.getSolution();
        int value = 1;
        saveTransientResults(filePath, rewards, marking, value, markingChanged, arrivalRates);
    }

    // ## TEST TYPE: INCREASING A SINGOL MARKING ON A SPECIFIC PLACE Pn
    public static void runSteadyStatesTests(PetriNet pn, Builder builder, Marking marking, String filePathP_ss, String filePathA_ss, int numPlaces, int[] arrivalRates) {
        List<RewardRate> tokensDistributionRewardsList = geTokensDistributionRewardsList();
        List<RewardRate> PoolCopiesRewardsList = getPoolCopiesRewardsList();
        SteadyStateSolution<RewardRate> poolCopiesRewards = new SteadyStateSolution<>();
        SteadyStateSolution<RewardRate> tokensDistributionRewards = new SteadyStateSolution<>();
        for (int n = 0; n < numPlaces; n++) {
            String filePathP_ss_arr = filePathP_ss + "/P" + n+1 + " Tests/";
            String filePathA_ss_arr = filePathA_ss + "/P" + n+1 + " Tests/";
            int tmpVal = arrivalRates[n];

            for (int i = 1; i < 15; i++) {
                arrivalRates = newArrivals(arrivalRates, n, i);
                builder.setArrivalRates(arrivalRates);
                PetriNet net = builder.build();
                poolCopiesRewards = calculateGSPNSteadyState(net, marking, i, filePathP_ss_arr, PoolCopiesRewardsList, 0, arrivalRates);
                tokensDistributionRewards = calculateGSPNSteadyState(net, marking, i, filePathA_ss_arr, tokensDistributionRewardsList, 0, arrivalRates);
            }
            arrivalRates = newArrivals(arrivalRates, n, tmpVal);

        }
        String filePathP_ss_pool = filePathP_ss + "/Pool Tests/";
        String filePathA_ss_pool = filePathA_ss + "/Pool Tests/";
        int tmpPool = marking.getTokens("Pool");
        for (int i = 1; i < 15; i++) {
            changePoolsize(pn, marking, i);
            poolCopiesRewards = calculateGSPNSteadyState(pn, marking, i, filePathP_ss_pool, PoolCopiesRewardsList, 0, arrivalRates);
            tokensDistributionRewards = calculateGSPNSteadyState(pn, marking, i, filePathA_ss_pool, tokensDistributionRewardsList, 0, arrivalRates);
        }
        changePoolsize(pn, marking, tmpPool);
    }


    public static void runAllTests(PetriNet pn, Marking marking, int numPlaces, int numPh, int[] arrivalValues, int poolSize, int[] poolSizeValues, int[] arrivalRates, int[] increaseRates) {
        int numTest = poolSizeValues.length;
        String filePathP_ss = System.getProperty("user.dir") + "/SteadyStateResults/PoolCopies/";
        String filePathA_ss = System.getProperty("user.dir") + "/SteadyStateResults/TokensDistribution/";
        String filePathP_ta = System.getProperty("user.dir") + "/TransientResults/PoolCopies/";
        String filePathA_ta = System.getProperty("user.dir") + "/TransientResults/TokensDistribution/";
        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        Builder builder = new Builder(marking, numPlaces, numPh, arrivalRates, poolSize);

        SteadyStateSolution<RewardRate> poolCopiesRewards = new SteadyStateSolution<>();
        SteadyStateSolution<RewardRate> tokenDistributionRewards = new SteadyStateSolution<>();
        List<RewardRate> tokensDistributionRewardsList = geTokensDistributionRewardsList();
        List<RewardRate> poolCopiesRewardsList = getPoolCopiesRewardsList();
        int[]startArrivals = arrivalRates;
        for (int i = 0; i < numTest; i++) {
            int P1 = arrivalValues[i];
            arrivalRates = newArrivals(arrivalRates, 0, P1);
            poolSize = poolSizeValues[i];
            changePoolsize(pn, marking, poolSize);
            for (int j = 0; j < arrivalValues.length; j++) {
                int P2 = arrivalValues[j];
                arrivalRates = newArrivals(arrivalRates, 1, P2);
                for (int k = 0; k < arrivalValues.length; k++) {
                    int P3 = arrivalValues[k];

                    arrivalRates = newArrivals(arrivalRates, 2, P3);
                    System.out.println("Marcatura attuale: " + marking);

                    //CALCOLO STEADY STATE
                    builder.setArrivalRates(arrivalRates);
                    pn = builder.build();

                    poolCopiesRewards = calculateGSPNSteadyState(pn, marking, poolSize, filePathP_ss, poolCopiesRewardsList, 1, arrivalRates);
                    tokenDistributionRewards = calculateGSPNSteadyState(pn, marking, poolSize, filePathA_ss, tokensDistributionRewardsList, 1, arrivalRates);

                    //Calcolato Steady State re-buildo la net a steady state
                    //PetriNet steadyNet = builder.buildSteadyStateNet(tokenDistributionRewards.getSteadyState(), numPh, marking, arrivalRates);
                    //base Transient
                    calculateGSPNTransientAnalysis(pn, marking, poolSize, filePathA_ta, poolCopiesRewardsList, "", arrivalRates);
                    calculateGSPNTransientAnalysis(pn, marking, poolSize, filePathP_ta, poolCopiesRewardsList, "", arrivalRates);

                    for (int pt = 0; pt < numPlaces; pt++) {

                        int tmp = arrivalValues[pt];
                        for (int h = 0; h < increaseRates.length; h++) {
                            //increasing arrivals for Pn
                            String markingChanged = " -- " + tmp + "P" + pt;
                            int increasedValue = tmp + increaseRates[h];
                            System.out.println("Calcolo transiente per : " + marking + " incrementando: " + tmp + "P" + pt + " portandolo a: " + increasedValue);
                            arrivalRates = newArrivals(arrivalRates, pt, increasedValue);
                            String tmpfilePathA_ta = filePathA_ta.concat("IncreasedRate/");
                            String tmpfilePathP_ta = filePathP_ta.concat("IncreasedRate/");
                            //re-build steady net con nuovi arrivals
                            PetriNet tmpSteadyNet = builder.buildSteadyStateNet(tokenDistributionRewards.getSteadyState(), numPh, marking, arrivalRates, poolSize);
                            calculateGSPNTransientAnalysis(tmpSteadyNet, marking, poolSize, tmpfilePathA_ta, tokensDistributionRewardsList, markingChanged, arrivalRates);
                            calculateGSPNTransientAnalysis(tmpSteadyNet, marking, poolSize, tmpfilePathP_ta, poolCopiesRewardsList, markingChanged, arrivalRates);
                        }
                        //Ri sistemo la marcatura incrementata
                        arrivalRates = newArrivals(arrivalRates, pt, tmp);
                    }
                }
            }
        }
    }
    public static void saveRejectionChangedPool(int []arrivalRates, int poolSize, BigDecimal rejectionRate, int changedPool, BigDecimal emptyPoolValue,  String filePath) {
        String result = filePath + "RejectionPoolDimentioning/" +
                arrivalRates[0] + "P1 " +
                arrivalRates[1] + "P2 " +
                arrivalRates[2] + "P3 " +
                poolSize + "Pool.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(result, true))) {
            writer.write(rejectionRate.toString() + " " + changedPool + " "+ emptyPoolValue.toString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void saveRejection(int []arrivalRates, Marking marking, BigDecimal rejectionRate, int poolSize, BigDecimal emptyPoolValue,  String filePath) {
        int totalArr = 0;
        for(int i = 0; i< arrivalRates.length; i++){
            totalArr+= arrivalRates[i];
        }

        double percRej = (rejectionRate.doubleValue()/totalArr)*100;
        String result = filePath + "RejectionTests/"+ arrivalRates[0] + "P1 " +
                arrivalRates[1] + "P2 " +
                arrivalRates[2] + "P3 " +
                marking.getTokens("Pool") + "Pool.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(result, false))) {
            writer.write(rejectionRate.toString() + " " + poolSize + " "+ emptyPoolValue.toString() + " " + percRej);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void computeRejection(int[] arrivalRates, SteadyStateSolution<RewardRate> poolCopiesRewards, Marking marking, String path,int poolSize, int changedVal){
        BigDecimal totalArrivalRate = BigDecimal.ZERO;

        for (int rate : arrivalRates) {
            totalArrivalRate = totalArrivalRate.add(BigDecimal.valueOf(rate));
        }
        BigDecimal emptyPoolValue = getEmptyPoolReward(poolCopiesRewards.getSteadyState());
        System.out.println("Probabilità Pool vuoto: "+ emptyPoolValue.toString());
        System.out.println("Rate arrivi totale: " + totalArrivalRate);
        BigDecimal result = totalArrivalRate.multiply(emptyPoolValue);
        System.out.println("Tasso di Rejection: " + result);

        //SALVATAGGIO: SE changedVal == 0, salvo singolo rejetion else salvo test dimensionamento
        if(changedVal == 0) {
            saveRejection(arrivalRates, marking, result, changedVal, emptyPoolValue, path);
        }else{
            saveRejectionChangedPool(arrivalRates, poolSize, result, changedVal, emptyPoolValue, path);
        }
    }

    public static void poolDimentioning(int[] arrivalRates, PetriNet pn, Marking marking, int poolSize, String filePathP_ss, String filePath_rr,
                                        List<RewardRate> poolCopiesRewardsList){
        for(int i = 1; i <= poolSize*2; i++) {
            SteadyStateSolution<RewardRate> poolCopiesRewards = new SteadyStateSolution<>();
            changePoolsize(pn, marking, i);
            poolCopiesRewards = calculateGSPNSteadyState(pn, marking, i, filePathP_ss, poolCopiesRewardsList, 1, arrivalRates);
            computeRejection(arrivalRates, poolCopiesRewards, marking, filePath_rr, poolSize, i);
        }
        changePoolsize(pn, marking, poolSize);
    }

    public static void rejectionRateAnalysis(int[] arrivalRates, PetriNet pn, Marking marking, int numPh,  int poolSize, String filePathP_ss, String filePathA_ss,
                                             String filePathA_ta, String filePathP_ta){
        String filePath_rr = System.getProperty("user.dir") + "/RejectionRatesAnalysis/PoolCopies/"; ///sistema cartella RR
        BigDecimal totalArrivalRate = BigDecimal.ZERO;

        // configurazioni non onerose
        int [] newArrivalRate = {2, 2, 1};

        for (int rate : arrivalRates) {
            totalArrivalRate = totalArrivalRate.add(BigDecimal.valueOf(rate));
        }
        SteadyStateSolution<RewardRate> poolCopiesRewards = new SteadyStateSolution<>();
        SteadyStateSolution<RewardRate> poolCopiesRewards2 = new SteadyStateSolution<>();
        SteadyStateSolution<RewardRate> tokensDistributionRewards = new SteadyStateSolution<>();
        SteadyStateSolution<RewardRate> tokensDistributionRewards2 = new SteadyStateSolution<>();
        List<RewardRate> tokensDistributionRewardsList = geTokensDistributionRewardsList();
        List<RewardRate> poolCopiesRewardsList = getPoolCopiesRewardsList();
        System.out.println(marking);
        //STEADY STATE CONFIG INIZIALE
        poolCopiesRewards = calculateGSPNSteadyState(pn, marking, poolSize, filePathP_ss, poolCopiesRewardsList, 1, arrivalRates);
        tokensDistributionRewards = calculateGSPNSteadyState(pn, marking, poolSize, filePathA_ss, tokensDistributionRewardsList, 1, arrivalRates);

        computeRejection(arrivalRates, poolCopiesRewards, marking, filePath_rr, poolSize, 0); //sistema changed val

        //TRANSIENTE CONFIGURAZIONE INIZIALE
        calculateGSPNTransientAnalysis(pn, marking, poolSize, filePathP_ta, poolCopiesRewardsList, "", arrivalRates);
        calculateGSPNTransientAnalysis(pn, marking, poolSize, filePathA_ta, tokensDistributionRewardsList, "", arrivalRates);

        //poolDimentioning(arrivalRates, pn, marking, poolSize, filePathP_ss, filePath_rr, poolCopiesRewardsList);


        //Calcolato Steady State re-buildo la net a steady state cambiando i tassi di arrivo
        Builder builder = new Builder(marking, newArrivalRate.length, numPh, newArrivalRate, poolSize);
        PetriNet newNet = builder.build();
        Marking steadyMarking = new Marking();
        PetriNet steadyNet = builder.buildSteadyStateNet(tokensDistributionRewards.getSteadyState(), numPh, steadyMarking, newArrivalRate, poolSize);
        System.out.println(marking);

        String tmpfilePathP_ta = filePathP_ta.concat("IncreasedRate/");
        String tmpfilePathA_ta = filePathA_ta.concat("IncreasedRate/");
        //String tmpfilePathP_ta = filePathP_ta.concat("IncreasedRate/");
        String markingChanged = " -- " + arrivalRates[0] + "P1 " + arrivalRates[1] + "P2 " + arrivalRates[2] + "P3";
        System.out.println(marking);
        calculateGSPNTransientAnalysis(steadyNet, steadyMarking, poolSize, tmpfilePathP_ta, poolCopiesRewardsList, markingChanged, newArrivalRate);
        calculateGSPNTransientAnalysis(steadyNet, steadyMarking, poolSize, tmpfilePathA_ta, tokensDistributionRewardsList, markingChanged, newArrivalRate);

        //calcolo Steady state e Transiente per la nuova configurazione

        poolCopiesRewards2 = calculateGSPNSteadyState(newNet, marking, poolSize, filePathP_ss, poolCopiesRewardsList, 1, newArrivalRate);
        tokensDistributionRewards2 = calculateGSPNSteadyState(newNet, marking, poolSize, filePathA_ss, tokensDistributionRewardsList, 1, newArrivalRate);
        computeRejection(newArrivalRate, poolCopiesRewards2, marking, filePath_rr, poolSize, 0);
        calculateGSPNTransientAnalysis(newNet, marking, poolSize, filePathP_ta, poolCopiesRewardsList, "", newArrivalRate);
        calculateGSPNTransientAnalysis(newNet, marking, poolSize, filePathA_ta, tokensDistributionRewardsList, "", newArrivalRate);

        //Studio sul dimensionamento del pool nella nuova configurazione
        //poolDimentioning(newArrivalRate, newNet, marking, poolSize, filePathP_ss, filePath_rr, poolCopiesRewardsList);

    }
    public static void runSingleConfiguration(PetriNet pn, Marking marking, int poolSize, int[] arrivalRates,
                                              String filePathP_ss, String filePathA_ss, String filePathP_ta, String filePathA_ta) {
        SteadyStateSolution<RewardRate> poolCopiesRewards = new SteadyStateSolution<>();
        SteadyStateSolution<RewardRate> tokensDistributionRewards = new SteadyStateSolution<>();
        List<RewardRate> tokensDistributionRewardsList = geTokensDistributionRewardsList();
        List<RewardRate> poolCopiesRewardsList = getPoolCopiesRewardsList();
        poolCopiesRewards = calculateGSPNSteadyState(pn, marking, poolSize, filePathP_ss, poolCopiesRewardsList, 1, arrivalRates);
        tokensDistributionRewards = calculateGSPNSteadyState(pn, marking, poolSize, filePathA_ss, tokensDistributionRewardsList, 1, arrivalRates);
        calculateGSPNTransientAnalysis(pn, marking, poolSize, filePathA_ta, tokensDistributionRewardsList, "", arrivalRates);
        calculateGSPNTransientAnalysis(pn, marking, poolSize, filePathP_ta, poolCopiesRewardsList, "", arrivalRates);

    }
    public static void runPerturbation(PetriNet pn, Marking marking, int poolSize, int[] arrivalRates, int numPh, int[] newArrivalRate,
                                       String filePathP_ss, String filePathA_ss, String filePathP_ta, String filePathA_ta){
        String perturbedPlace = "P1";
        int tmpPoolsize = marking.getTokens("Pool");
        SteadyStateSolution<RewardRate> poolCopiesRewards = new SteadyStateSolution<>();
        SteadyStateSolution<RewardRate> tokensDistributionRewards = new SteadyStateSolution<>();
        List<RewardRate> tokensDistributionRewardsList = geTokensDistributionRewardsList();
        List<RewardRate> poolCopiesRewardsList = getPoolCopiesRewardsList();

        poolCopiesRewards = calculateGSPNSteadyState(pn, marking, poolSize, filePathP_ss, poolCopiesRewardsList, 1, arrivalRates);
        tokensDistributionRewards = calculateGSPNSteadyState(pn, marking, poolSize, filePathA_ss, tokensDistributionRewardsList, 1, arrivalRates);
        calculateGSPNTransientAnalysis(pn, marking, poolSize, filePathA_ta, tokensDistributionRewardsList, "", arrivalRates);
        calculateGSPNTransientAnalysis(pn, marking, poolSize, filePathP_ta, poolCopiesRewardsList, "", arrivalRates);

        //Calcolato Steady State re-buildo la net a steady state
        Marking steadyMarking = new Marking(); //rappresenterà la nuova marcatura nella nuova Steady Net
        Builder builder = new Builder(steadyMarking, newArrivalRate.length, numPh, newArrivalRate, poolSize);
        PetriNet steadyNet = builder.buildSteadyStateNet(tokensDistributionRewards.getSteadyState(), numPh, steadyMarking, newArrivalRate, poolSize);
        String tmpfilePathP_ta = filePathP_ta.concat("IncreasedRate/");
        String tmpfilePathA_ta = filePathA_ta.concat("IncreasedRate/");
        String markingChanged = " -- " + arrivalRates[0] + "P1 " + arrivalRates[1] + "P2 " + arrivalRates[2] + "P3 " + tmpPoolsize + "Pool";

        calculateGSPNTransientAnalysis(steadyNet, steadyMarking, poolSize, tmpfilePathP_ta, poolCopiesRewardsList, markingChanged, newArrivalRate);
        calculateGSPNTransientAnalysis(steadyNet, steadyMarking, poolSize, tmpfilePathA_ta, tokensDistributionRewardsList, markingChanged, newArrivalRate);
    }
    public static void main(String[] args) {

        //PetriNet pn = new PetriNet();
        Marking marking = new Marking();
        int numPlaces = 3;
        int numPh = 4;
        int[] arrivalRates = {3, 1, 5};
        int poolSize = 8;


        int[] poolSizeValues = {3, 8, 10, 12};
        int[] arrivalValues = {1, 2, 3, 5};
        int[] increaseRates = {1, 2, 3};

        //int numTest = poolSizeValues.length;
        String filePathP_ss = System.getProperty("user.dir") + "/SteadyStateResults/PoolCopies/";
        String filePathA_ss = System.getProperty("user.dir") + "/SteadyStateResults/TokensDistribution/";
        String filePathP_ta = System.getProperty("user.dir") + "/TransientResults/PoolCopies/";
        String filePathA_ta = System.getProperty("user.dir") + "/TransientResults/TokensDistribution/";
        String filePathSS_rr = System.getProperty("user.dir") + "/RejectionRatesAnalysis/PoolCopies/RejectionTests/";
        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        Builder builder = new Builder(marking, numPlaces, numPh, arrivalRates, poolSize);
        PetriNet pn = builder.build();

        rejectionRateAnalysis(arrivalRates, pn, marking, numPh, poolSize, filePathP_ss, filePathA_ss, filePathA_ta, filePathP_ta);
    }
}
        //TUTTI I TEST
        //runAllTests(pn, marking, numPlaces, numPh, arrivalValues, poolSize, poolSizeValues, arrivalRates, increaseRates);

        ///STEADY STATE

        //runSingleConfiguration(pn, marking, poolSize, arrivalRates ,filePathP_ss, filePathA_ss, filePathP_ta, filePathA_ta);
        //int []newArrivals = {2,1,5};
        //runPerturbation(pn, marking, 10, arrivalRates, numPh, newArrivals, filePathP_ss, filePathA_ss, filePathP_ta, filePathA_ta);
        //DA CAPIRE COSA FARCI
        /*
        SteadyStateSolution<RewardRate> poolCopiesRewards = new SteadyStateSolution<>();
        SteadyStateSolution<RewardRate> tokensDistributionRewards = new SteadyStateSolution<>();
        List<RewardRate> tokensDistributionRewardsList = geTokensDistributionRewardsList();
        List<RewardRate> poolCopiesRewardsList = getPoolCopiesRewardsList();
        poolCopiesRewards = calculateGSPNSteadyState(pn, marking, poolSize, filePathP_ss, poolCopiesRewardsList, 1, arrivalRates);
        tokensDistributionRewards = calculateGSPNSteadyState(pn, marking, poolSize, filePathA_ss, tokensDistributionRewardsList, 1, arrivalRates);
        //Calcolato Steady State re-buildo la net a steady state
        PetriNet steadyNet = builder.buildSteadyStateNet(tokensDistributionRewards.getSteadyState(), numPh, marking, arrivalRates);


        //base Transient

        calculateGSPNTransientAnalysis(pn, marking, poolSize, filePathA_ta, tokensDistributionRewardsList, "", arrivalRates);
        calculateGSPNTransientAnalysis(pn, marking, poolSize, filePathP_ta, poolCopiesRewardsList, "", arrivalRates);
        int pt = 3;
        //int tmp = marking.getTokens("P" + pt);
        int tmp = marking.getTokens("InitialPlace");
        String markingChanged = " -- " + tmp + "Init";
        int increasedValue = tmp + increaseRates[1];
        System.out.println("Calcolo transiente per : " + marking + " incrementando: " + tmp + "P" + pt + " portandolo a: " + increasedValue);
        //changeArrivals(steadyNet, marking, pt, increasedValue);
        changeInitialPlace(steadyNet, marking, 6);
        String tmpfilePathA_ta = filePathA_ta.concat("IncreasedRate/");
        String tmpfilePathP_ta = filePathP_ta.concat("IncreasedRate/");
        calculateGSPNTransientAnalysis(steadyNet, marking, poolSize, tmpfilePathA_ta, tokensDistributionRewardsList, markingChanged, arrivalRates);
        calculateGSPNTransientAnalysis(steadyNet, marking, poolSize, tmpfilePathP_ta, poolCopiesRewardsList, markingChanged, arrivalRates);

        */

        /*
        for (int pt = 1; pt <= numPlaces; pt++) {

            int tmp = marking.getTokens("P" + pt);
            for (int h = 0; h < increaseRates.length; h++) {
                //increasing arrivals for Pn
                String markingChanged = " -- " + tmp + "P" + pt;
                int increasedValue = tmp + increaseRates[h];
                System.out.println("Calcolo transiente per : " + marking + " incrementando: " + tmp + "P" + pt + " portandolo a: " + increasedValue);
                changeArrivals(steadyNet, marking, pt, increasedValue);
                String tmpfilePathA_ta = filePathA_ta.concat("IncreasedRate/");
                String tmpfilePathP_ta = filePathP_ta.concat("IncreasedRate/");
                calculateGSPNTransientAnalysis(steadyNet, marking, poolSize, tmpfilePathA_ta, tokensDistributionRewardsList, markingChanged);
                calculateGSPNTransientAnalysis(steadyNet, marking, poolSize, tmpfilePathP_ta, poolCopiesRewardsList, markingChanged);
            }
            */
/*
            //runSteadyStatesTests(pn, builder, marking, filePathP_ss, filePathA_ss, numPlaces);
            //runAllTests(pn, marking, numPlaces, numPh, arrivalValues, poolSize, poolSizeValues, arrivalRates, increaseRates);

            ///





        /* Da guardare
        RegTransient analysis = RegTransient.builder()
                .greedyPolicy(new BigDecimal("12"), new BigDecimal("0.005"))
                .timeStep(new BigDecimal("0.02")).build();

        TransientSolution<DeterministicEnablingState, Marking> solution =
                analysis.compute(pn, marking);

        // display transient probabilities
        new TransientSolutionViewer(solution);

*/

        //System.out.printf("%s", resultReward.toString());
        //System.out.printf("%n%.6f", resultReward.getSteadyState());


        //SteadyStateSolution.computeRewards()
        //GSPN Transient Analysis

/*
        double step = 0.1;
        marking.setTokens(pn.getPlace("Pool"), 2);
        Pair<Map<Marking, Integer>, double[][]> result = GSPNTransient.builder()
                .timePoints(0.0, 10.0, step)
                .build().compute(pn, marking);

        System.out.printf("%n%s", marking);
        Map<Marking, Integer> statePos = result.first();
        double[][] probs = result.second();
        int markingPos = statePos.get(marking);

        for (int t = 0; t < probs.length; t++) {
            double time = t * step;
            for (int i = 0; i < probs.length; i++) {
                System.out.printf("%.1f %.6f %n", time, probs[t][markingPos]);
                //System.out.println();
                //}


            }
        }*/



/*
String unavailabilityReward = builder.getUnavailabilityReward();
String unreliabilityReward = builder.getUnreliabilityReward();
String performabilityReward = builder.getPerformabilityReward(performabilityBoundMap.get(poolSize));
String rewards = unavailabilityReward + ";" + unreliabilityReward + ";" + performabilityReward;

RegSteadyState analysis = RegSteadyState.builder().build();

SteadyStateSolution<Marking> result;
SteadyStateSolution<RewardRate> resultReward;

Map<RewardRate, BigDecimal> steadyState;

for(int waitTrigger = leftBound; waitTrigger<= rightBound; waitTrigger+=granularity){

            builder.changePoolsize(net,marking, poolSize);
            builder.changeTrigger(net,Integer.toString(waitTrigger));

          result = analysis.compute(net, marking);
            resultReward = SteadyStateSolution\.computeRewards(result, rewards);



BigDecimal reliability = steadyState.entrySet().stream().filter(t -> t.getKey().toString().equals(unreliabilityReward)).findFirst().get().getValue();
//BigDecimal availability = steadyState.entrySet().stream().filter(t -> t.getKey().toString().equals("Ko+Rej")).findFirst().get().getValue();
BigDecimal availability = steadyState.entrySet().stream().filter(t -> t.getKey().toString
addRow(filePath, waitTrigger, reliability, availability, performability);
@Override
    public String getPerformabilityReward() {
        return "Ko+Rej";
    }

    @Override
    public String getNickName() {
        return "coordinated-sequential";
    }



//modifica transizione
@Override
    public void changeTrigger(PetriNet net, String triggerValue) {
        Transition trigger = net.getTransition("trigger");
        trigger.removeFeature(StochasticTransitionFeature.class);
        trigger.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal(triggerValue), MarkingExpr.from("1", net)));
    }


 */


