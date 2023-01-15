package sky.pro.skyprotermwork3.model;
import java.util.Objects;

public class Socks {
    private Size size;
    private Color color;
    private Integer cotton;
    private Integer quantity;

    public Socks(Color color, Size size, int cotton, int quantity) {
        this.color = color;
        this.size = size;
        this.cotton = cotton;
        this.quantity = quantity;
    }
    public Socks(Color color, Size size, int cotton) {
        this.color = color;
        this.size = size;
        this.cotton = cotton;

    }
    public Socks() {
    }
    public Color getColor() {
        return this.color;
    }

    public Size getSize() {
        return this.size;
    }

    public int getCotton() {
        return this.cotton;
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
                Objects.equals(socks.cotton, cotton);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, size, cotton);
    }
//    @Override
//    public String toString(){
//        return new String("size:"+getSize()+",color:"+getColor()+",cotton:"+getCotton()+",quantity:"+getQuantity());
//    }
}
