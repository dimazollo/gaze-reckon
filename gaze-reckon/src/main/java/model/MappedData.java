package model;

import model.eyetracker.Message;
import model.test.Stimulus;

import java.util.ArrayList;

/**
 * @Author Dmitry Volovod, Mikhail Turicyn
 * created on 10.03.2016
 */
public class MappedData {
    private Stimulus stimulus;
    private ArrayList<Message> messages;
    private Double delta;
    private Boolean idt;

    public MappedData(Stimulus stimulus, ArrayList<Message> messages) {
        this.stimulus = stimulus;
        this.messages = messages;
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

    @Override
    public String toString() {
        return "stimulus: " + stimulus.toString() + System.lineSeparator()
                + "messages:" + messages.toString()
                + "delta= " + delta + System.lineSeparator()
                + "idt = " + idt + System.lineSeparator();
    }
}
