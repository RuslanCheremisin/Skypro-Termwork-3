package sky.pro.skyprotermwork3.model;

import java.util.Objects;

public class Socks {
    private Size size;
    private Color color;
    private int cottonPercentage;
    private int quantity;

    public Socks(Color color, Size size, int cottonPercentage, int quantity) {
        this.color = color;
        this.size = size;
        if (cottonPercentage <= 0) {
            this.cottonPercentage = 80;
        } else {
            this.cottonPercentage = cottonPercentage;
        }
        if (quantity <= 0) {
            this.quantity = 1;
        } else {
            this.quantity = quantity;
        }
    }

    public Socks(Color color, Size size, int cottonPercentage) {
        this.color = color;
        this.size = size;
        if (cottonPercentage <= 0) {
            this.cottonPercentage = 80;
        } else {
            this.cottonPercentage = cottonPercentage;
        }

    }

    public Socks() {
    }

    public Color getColor() {
        return this.color;
    }

    public Size getSize() {
        return this.size;
    }

    public int getCottonPercentage() {
        return this.cottonPercentage;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Socks socks = (Socks) o;
        return Objects.equals(socks.color, color) &&
                Objects.equals(socks.size, size) &&
                Objects.equals(socks.cottonPercentage, cottonPercentage);
    }
    @Override
    public int hashCode(){
        return Objects.hash(color, size, cottonPercentage);
    }
}
