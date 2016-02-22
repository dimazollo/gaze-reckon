package views.graphs;

import main.Main;
import model.eyetracker.Message;

import java.util.ArrayList;

/**
 * Created by Vano on 22.02.2016.
 */
public class ViewOfData {
    Main mainApp;
    public void metod(){
        ArrayList<Message> messages = mainApp.getMessages();
        //messages.get(0).values.frame.avg
    }
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }
}
