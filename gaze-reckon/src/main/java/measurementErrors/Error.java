package measurementErrors;

import model.MappedData;
import model.eyetracker.Message;
import model.test.Stimulus;

import java.util.*;

/**
 * @Author Dmitry Volovod
 * created on 10.03.2016
 */
public final class Error {
    private final static String LAG_PHASE = "lag phase";
    private final static String SACCADE = "saccade";
    private final static String FIXATION = "fixation";

    static double computeStandardDeviation(ArrayList<MappedData> mappedDataArrayList) {
        HashMap<Message, Stimulus> filteredMap = new LinkedHashMap<>();
        for (MappedData mappedData : mappedDataArrayList) {
            ArrayList<Message> messages = mappedData.getMessages();
            for (int i = 0; i < messages.size(); i++) {
                //TODO-Dmitry Выделение периода фиксации, удаляя саккады и латентный период.
            }
        }
        return Double.NaN;
    }

    public static LinkedHashMap<Message, Stimulus> filterStimulusMessageMap(HashMap<Message, Stimulus> messageStimulusMap) {
        LinkedHashMap<Message, Stimulus> filteredMap = new LinkedHashMap<>();
        String status = LAG_PHASE;
        Stimulus currentStimulus = messageStimulusMap.entrySet().iterator().next().getValue();  // Первый стимул.
        for (Map.Entry<Message, Stimulus> entry : messageStimulusMap.entrySet()) {
            switch (status) {
                case LAG_PHASE:
                    if (entry.getKey().values.frame.fix == false) {
                        status = SACCADE;
                    }
                    break;

                case SACCADE:
                    if (entry.getKey().values.frame.fix == true) {
                        status = FIXATION;
                        filteredMap.put(entry.getKey(), entry.getValue());
                    }
                    break;

                case FIXATION:
                    if (entry.getValue().equals(currentStimulus)) {     // Если текущий стимул не равен предыдущему, то начало латентного периода.
                        if (entry.getKey().values.frame.fix == true) {  // Добавляем только точки, в которых была фиксация.
                            filteredMap.put(entry.getKey(), entry.getValue());
                        }
                    } else {
                        status = LAG_PHASE;
                        currentStimulus = entry.getValue();
                    }
                    break;
            }
        }
        return filteredMap;
    }

    public static HashMap<Stimulus, Double[]> computeDeviations(LinkedHashMap<Message, Stimulus> filteredMap) {
        HashSet<Stimulus> stimuliSet = new HashSet<>();
        boolean contains;
        for (Stimulus stimulus : filteredMap.values()) { // Пробегаем по всем стимулам и добавляем только одинаковые.
            Stimulus tmpStimulus = new Stimulus(stimulus.getPosition().x, stimulus.getPosition().y);
            contains = false;
            for (Stimulus s : stimuliSet) {
                if (s.equals(tmpStimulus)) contains = true;
            }
            if (!contains) stimuliSet.add(tmpStimulus);
        }
        ArrayList<Double> deltasX;
        ArrayList<Double> deltasY;
        HashMap<Stimulus, Double[]> results = new HashMap<>();
        for (Stimulus currentStimulus : stimuliSet) {
            deltasX = new ArrayList<>();
            deltasY = new ArrayList<>();
            for (Map.Entry<Message, Stimulus> entry : filteredMap.entrySet()) {
                if (currentStimulus.getPosition().x == entry.getValue().getPosition().x && currentStimulus.getPosition().y == entry.getValue().getPosition().y) {
                    deltasX.add(entry.getValue().getPosition().x - entry.getKey().values.frame.raw.x);
                    deltasY.add(entry.getValue().getPosition().y - entry.getKey().values.frame.raw.y);
                }
            }
            Double absDevX = absoluteDeviation(deltasX);
            Double absDevY = absoluteDeviation(deltasY);
            Double stDevX = standardDeviation(deltasX);
            Double stDevY = standardDeviation(deltasY);
            results.put(currentStimulus, new Double[]{absDevX, absDevY, stDevX, stDevY});
        }
        return results;
    }

    private static Double standardDeviation(ArrayList<Double> deviations) {
        Double sum = 0.0;
        for (Double deviation : deviations) {
            sum += Math.pow(deviation, 2.0);
        }
        return Math.sqrt(sum/deviations.size());
    }

    private static Double absoluteDeviation(ArrayList<Double> deviations) {
        Double sum = 0.0;
        for (Double deviation : deviations) {
            sum += deviation;
        }
        return sum/deviations.size();
    }
}