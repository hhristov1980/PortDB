package main.cargo;

import java.util.Objects;

public class Cargo {
    private static int uniqieId = 1;
    private String cargoId;

    public Cargo(){
        this.cargoId = "Cargo Id "+uniqieId++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cargo cargo = (Cargo) o;
        return cargoId == cargo.cargoId;
    }

    public String getCargoId() {
        return cargoId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cargoId);
    }
}
