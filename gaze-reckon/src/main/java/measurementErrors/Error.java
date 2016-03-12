package measurementErrors;

import model.MappedDataItem;
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

    // Вариант под класс MappedData
    static ArrayList<MappedDataItem> filterMappedData(ArrayList<MappedDataItem> mappedDataArrayList) {
        ArrayList<MappedDataItem> filteredMappedDataList = new ArrayList<>();
        for (MappedDataItem mappedDataItem : mappedDataArrayList) {
            String status = LAG_PHASE;
            ArrayList<Message> messages = mappedDataItem.getMessages();
            ArrayList<Message> filteredMessages = new ArrayList<>();
            MappedDataItem filteredMappedDataItem;
            for (Message message : messages) {
                // Выделение периода фиксации, удаляя саккады и латентный период.
                switch (status) {
                    case LAG_PHASE:
                        if (!message.values.frame.fix) {
                            status = SACCADE;
                        }
                        break;

                    case SACCADE:
                        if (message.values.frame.fix) {
                            status = FIXATION;
                            filteredMessages.add(message);
                        }
                        break;

                    case FIXATION:
                        if (message.values.frame.fix) {  // Добавляем только точки, в которых была фиксация.
                            filteredMessages.add(message);
                        }
                        break;
                }
            }
            filteredMappedDataItem = new MappedDataItem(mappedDataItem.getStimulus(), filteredMessages);
            filteredMappedDataList.add(filteredMappedDataItem);
        }
        return filteredMappedDataList;
    }

    // Вариант под LinkedHashMap
    public static LinkedHashMap<Message, Stimulus> filterStimulusMessageMap(HashMap<Message, Stimulus> messageStimulusMap) {
        LinkedHashMap<Message, Stimulus> filteredMap = new LinkedHashMap<>();
        String status = LAG_PHASE;
        Stimulus currentStimulus = messageStimulusMap.entrySet().iterator().next().getValue();  // Первый стимул.
        for (Map.Entry<Message, Stimulus> entry : messageStimulusMap.entrySet()) {
            switch (status) {
                case LAG_PHASE:
                    if (!entry.getKey().values.frame.fix) {
                        status = SACCADE;
                    }
                    break;

                case SACCADE:
                    if (entry.getKey().values.frame.fix) {
                        status = FIXATION;
                        filteredMap.put(entry.getKey(), entry.getValue());
                    }
                    break;

                case FIXATION:
                    if (entry.getValue().equals(currentStimulus)) {     // Если текущий стимул не равен предыдущему, то начало латентного периода.
                        if (entry.getKey().values.frame.fix) {  // Добавляем только точки, в которых была фиксация.
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

    // На базе MappedData
    public static HashMap<Stimulus, Double[]> computeDeviations(List<MappedDataItem> mappedDataList) {
        List<Stimulus> stimuli = new LinkedList<>();
        boolean contains;
        for (MappedDataItem mappedDataItem : mappedDataList) { // Пробегаем по всем стимулам и добавляем только одинаковые.
            Stimulus tmpStimulus = new Stimulus(mappedDataItem.getStimulus().getPosition().x, mappedDataItem.getStimulus().getPosition().y, null);
            contains = false;
            for (Stimulus s : stimuli) {
                if (s.equals(tmpStimulus)) contains = true;
            }
            if (!contains) stimuli.add(tmpStimulus);
        }

        // Отфильтровавыние стимулов, на которых алгоритм выделения фиксации среди саккад и латентного периода не нашел этой самой фиксации.
        List<MappedDataItem> filteredMappedData = new LinkedList<>();
        for (MappedDataItem mappedDataItem : mappedDataList) {
            if(mappedDataItem.computeStatistics() != null) {
                filteredMappedData.add(mappedDataItem);
            }
        }

        // Фильрация "некомплектных" стимулов.
//        for (MappedDataItem mappedDataItem : mappedDataList) {
//            if(!mappedDataItem.hasMissingData()) {
//                mappedDataItem.computeStatistics();
//                filteredMappedData.add(mappedDataItem);
//            }
//        }

        List<Double> deltasX;
        List<Double> deltasY;
        Double minDeltaX;
        Double maxDeltaX;
        Double minDeltaY;
        Double maxDeltaY;
        HashMap<Stimulus, Double[]> results = new HashMap<>();
        for (Stimulus currentStimulus : stimuli) {
            deltasX = new LinkedList<>();
            deltasY = new LinkedList<>();
            minDeltaX = null;
            maxDeltaX = null;
            minDeltaY = null;
            maxDeltaY = null;
            for (MappedDataItem mappedDataItem : filteredMappedData) {
                if (currentStimulus.getPosition().x == mappedDataItem.getStimulus().getPosition().x &&
                        currentStimulus.getPosition().y == mappedDataItem.getStimulus().getPosition().y) {
                    if (minDeltaX == null || maxDeltaX == null || minDeltaY == null || maxDeltaY == null) { // Redundant clause. Can be simplified.
                        minDeltaX = mappedDataItem.getMinDeltaX();
                        maxDeltaX = mappedDataItem.getMaxDeltaX();
                        minDeltaY = mappedDataItem.getMinDeltaY();
                        maxDeltaY = mappedDataItem.getMaxDeltaY();
                    } else {
                        if (Math.abs(minDeltaX) > Math.abs(mappedDataItem.getMinDeltaX())) minDeltaX = mappedDataItem.getMinDeltaX();
                        if (Math.abs(maxDeltaX) < Math.abs(mappedDataItem.getMaxDeltaX())) maxDeltaX = mappedDataItem.getMaxDeltaX();
                        if (Math.abs(minDeltaY) > Math.abs(mappedDataItem.getMinDeltaY())) minDeltaY = mappedDataItem.getMinDeltaY();
                        if (Math.abs(maxDeltaY) < Math.abs(mappedDataItem.getMaxDeltaY())) maxDeltaY = mappedDataItem.getMaxDeltaY();
                    }
                    deltasX.add(mappedDataItem.getMeanX() - currentStimulus.getPosition().x);
                    deltasY.add(mappedDataItem.getMeanY() - currentStimulus.getPosition().y);
                }
                Double absDevX = absoluteDeviation(deltasX);
                Double absDevY = absoluteDeviation(deltasY);
                Double stDevX = standardDeviation(deltasX);
                Double stDevY = standardDeviation(deltasY);
                results.put(currentStimulus, new Double[]{absDevX, absDevY, stDevX, stDevY, minDeltaX, maxDeltaX, minDeltaY, maxDeltaY});
            }
        }
        return results;
    }

    // На базе смапленных хэшкарт.
    public static HashMap<Stimulus, Double[]> computeDeviations(LinkedHashMap<Message, Stimulus> filteredMap) {
        HashSet<Stimulus> stimuliSet = new HashSet<>();
        boolean contains;
        for (Stimulus stimulus : filteredMap.values()) { // Пробегаем по всем стимулам и добавляем только одинаковые.
            Stimulus tmpStimulus = new Stimulus(stimulus.getPosition().x, stimulus.getPosition().y, null);
            contains = false;
            for (Stimulus s : stimuliSet) {
                if (s.equals(tmpStimulus)) contains = true;
            }
            if (!contains) stimuliSet.add(tmpStimulus);
        }
        // Часть с рачётом отклонений.
        ArrayList<Double> deltasX;
        ArrayList<Double> deltasY;
        HashMap<Stimulus, Double[]> results = new HashMap<>();
        for (Stimulus currentStimulus : stimuliSet) {
            deltasX = new ArrayList<>();
            deltasY = new ArrayList<>();
            // Одинаковыми считаются стимулы с одинаковыми координатами.
            for (Map.Entry<Message, Stimulus> entry : filteredMap.entrySet()) {
                if (currentStimulus.getPosition().x == entry.getValue().getPosition().x && currentStimulus.getPosition().y == entry.getValue().getPosition().y) {
                    deltasX.add(entry.getKey().values.frame.raw.x - entry.getValue().getPosition().x);
                    deltasY.add(entry.getKey().values.frame.raw.y - entry.getValue().getPosition().y);
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

    // Отличается от предыдущего тем, что стимулы считаются разными, если предъявлены в одинаковых координатах, но в разное время.
    public static HashMap<Stimulus, Double[]> computeDeviationsForEach
    (LinkedHashMap<Message, Stimulus> filteredMap) {
        HashSet<Stimulus> stimuliSet = new HashSet<>();
        boolean contains;
        for (Stimulus stimulus : filteredMap.values()) { // Пробегаем по всем стимулам и добавляем каждый предъявленный стимул.
            Stimulus tmpStimulus = new Stimulus(stimulus.getPosition().x, stimulus.getPosition().y, stimulus.getTimestamp());
            contains = false;
            for (Stimulus s : stimuliSet) {
                if (s.equals(tmpStimulus)) contains = true;
            }
            if (!contains) stimuliSet.add(tmpStimulus);
        }
        // Часть с рачётом отклонений.
        ArrayList<Double> deltasX;
        ArrayList<Double> deltasY;
        HashMap<Stimulus, Double[]> results = new HashMap<>();
        for (Stimulus currentStimulus : stimuliSet) {
            deltasX = new ArrayList<>();
            deltasY = new ArrayList<>();
            // Одинаковыми считаются стимулы с одинаковыми координатами.
            for (Map.Entry<Message, Stimulus> entry : filteredMap.entrySet()) {
                if (currentStimulus.getPosition().x == entry.getValue().getPosition().x && currentStimulus.getPosition().y == entry.getValue().getPosition().y) {
                    deltasX.add(entry.getKey().values.frame.raw.x - entry.getValue().getPosition().x);
                    deltasY.add(entry.getKey().values.frame.raw.y - entry.getValue().getPosition().y);
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

    // Расчёт среднеквадратичного отклонения.

    public static Double standardDeviation(List<Double> deviations) {
        Double sum = 0.0;
        for (Double deviation : deviations) {
            sum += Math.pow(deviation, 2.0);
        }
        return Math.sqrt(sum / deviations.size());
    }

    // Расчёт абсолютного отклонения.
    public static Double absoluteDeviation(List<Double> deviations) {
        Double sum = 0.0;
        for (Double deviation : deviations) {
            sum += deviation;
        }
        return sum / deviations.size();
    }
}