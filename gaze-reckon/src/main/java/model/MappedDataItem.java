package model;

import model.eyetracker.Message;
import model.test.Stimulus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Mapped item of experimental data from eye-tracker and visual stimulus provided by stimulus visualiser.
 *
 * @Author Dmitry Volovod, Mikhail Turicyn
 * created on 10.03.2016
 *
 */
public class MappedDataItem {
    private double meanX;
    private double meanY;
    private Stimulus stimulus;
    private ArrayList<Message> messages;
    private double minDeltaX;
    private double maxDeltaX;
    private double minDeltaY;
    private double maxDeltaY;

    public MappedDataItem(Stimulus stimulus, ArrayList<Message> messages) {
        this.stimulus = stimulus;
        this.messages = messages;
    }

    public List<Message> getFixationsMessages() {
        List<Message> filteredMessages = new LinkedList<>();
        int status = 0;
        final int THRESHOLD = 100;
        for (int i = 0; i < messages.size(); i++) {
            // Выделение периода фиксации, удаляя саккады и латентный период.
            switch (status) {
                case 0: // Латентный период.
                    if (messages.get(i).values.frame.fix == false) {
                        status = 1;
                    }
                    break;
                case 1: // Саккада.
                    if (messages.get(i).values.frame.fix == true && messages.get(i).values.frame.raw.getDistance(stimulus.getPosition()) < THRESHOLD) {
                        status = 2;
                        filteredMessages.add(messages.get(i));
                    }
                    break;
                case 2: // Фиксация.
                    if (messages.get(i).values.frame.fix == true && messages.get(i).values.frame.raw.getDistance(stimulus.getPosition()) < THRESHOLD) {
                        filteredMessages.add(messages.get(i));
                    }
                    break;
            }
        }
        ArrayList<Message> resultArray = new ArrayList<>();
        resultArray.addAll(filteredMessages);
        return resultArray;
    }

    public double[] computeStatistics() {
        if (messages.isEmpty()) return null;
        List<Message> filteredMessages = getFixationsMessages();
        if (filteredMessages.isEmpty()) {
            System.out.println(stimulus);
            return null;
        }
        //List<Double> deltasX = new LinkedList<>();
        //List<Double> deltasY = new LinkedList<>();
        double deltaX;
        double deltaY;
        meanX = 0;  // Среднее арифметическое по X
        meanY = 0;  // Среднее арифметическое по Y
        minDeltaX = filteredMessages.get(0).values.frame.raw.x - stimulus.getPosition().x;
        maxDeltaX = filteredMessages.get(0).values.frame.raw.x - stimulus.getPosition().x;
        minDeltaY = filteredMessages.get(0).values.frame.raw.y - stimulus.getPosition().y;
        maxDeltaY = filteredMessages.get(0).values.frame.raw.y - stimulus.getPosition().y;
        for (int i = 0; i < filteredMessages.size(); i++) {
            deltaX = filteredMessages.get(i).values.frame.raw.x - stimulus.getPosition().x;
            meanX += filteredMessages.get(i).values.frame.raw.x;
            //deltasX.add(deltaX);
            deltaY = filteredMessages.get(i).values.frame.raw.y - stimulus.getPosition().y;
            meanY += filteredMessages.get(i).values.frame.raw.y;
            //deltasY.add(deltaY);
            if (minDeltaX > deltaX) minDeltaX = deltaX;
            if (maxDeltaX < deltaX) maxDeltaX = deltaX;
            if (minDeltaY > deltaY) minDeltaY = deltaY;
            if (maxDeltaY < deltaY) maxDeltaY = deltaY;
        }
        meanX /= filteredMessages.size();
        meanY /= filteredMessages.size();
        return new double[]{ meanX, minDeltaX, maxDeltaX, meanY, minDeltaY, maxDeltaY };
    }

    public boolean hasMissingData() {
        boolean hasMissingData = false;
        for (Message message : messages) {
            if (message.hasMissingData() != null) {
                hasMissingData = true;
            }
        }
        return hasMissingData;
    }

    @Override
    public String toString() {
        return "stimulus: " + stimulus.toString() + System.lineSeparator()
                + "messages:" + messages.toString();
    }

    public double getMinDeltaX() {
        return minDeltaX;
    }

    public double getMaxDeltaX() {
        return maxDeltaX;
    }

    public double getMinDeltaY() {
        return minDeltaY;
    }

    public double getMaxDeltaY() {
        return maxDeltaY;
    }

    public double getMeanX() {
        return meanX;
    }

    public double getMeanY() {
        return meanY;
    }

    public Stimulus getStimulus() {
        return stimulus;
    }

    public void setStimulus(Stimulus stimulus) {
        this.stimulus = stimulus;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
