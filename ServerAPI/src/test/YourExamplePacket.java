
import de.lystx.cloudsystem.library.elements.list.CloudList;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class YourExamplePacket {

    public static void main(String[] args) {

        CloudList<Mensch> list = new CloudList<>();

        list.add(new Mensch("Jonas", 10)).queue();
        list.add(new Mensch("Sandro", 95)).queue();

        System.out.println(list.filter().find(mensch -> mensch.getAlter() == 10));
    }


   @AllArgsConstructor @Getter
   public static class Mensch {

        private final String name;
        private final int alter;

        @Override
        public String toString() {
            return name;
        }
    }
}
