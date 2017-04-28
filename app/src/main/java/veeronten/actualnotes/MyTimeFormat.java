package veeronten.actualnotes;

public class MyTimeFormat {

    public static String convertToString(final int hours, final int minutes){
        StringBuilder answer = new StringBuilder();
        if(new String(hours+"").length()==1)
            answer.append("0");
        answer.append(hours+":");
        if(new String(minutes+"").length()==1)
            answer.append("0");
        answer.append(minutes);
        return answer.toString();
    }

    public static int getHoursFromString(String time){
        return Integer.valueOf(time.split(":")[0]);
    }

    public static int getMinutesFromString(String time){
        return Integer.valueOf(time.split(":")[1]);
    }
}
