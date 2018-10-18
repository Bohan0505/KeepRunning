package comp5216.sydney.edu.au.keeprunning.model;

import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by gaolei on 17/2/27.
 */

@Table(name = "RouteRecord")
public class RouteRecord implements Serializable {

    private long id;
    private long datetime;
    private int costtime;
    private double distance;
    private double speed;
    private String points;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    public int getCosttime() {
        return costtime;
    }

    public void setCosttime(int costtime) {
        this.costtime = costtime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "RouteRecord{" +
                "id=" + id +
                ", datetime=" + datetime +
                ", costtime=" + costtime +
                ", distance=" + distance +
                ", speed=" + speed +
                ", points='" + points + '\'' +
                '}';
    }
}
